package com.datastax.oss.cass_stac.dao;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GeoPartition {
    public static final int MIN_RESOLUTION = 0;
    public static final int MAX_RESOLUTION = 15;
    public static final int DEFAULT_RESOLUTION = 6;
    private final int resolution;
    private final GeometryFactory geometryFactory;
    private final int idBucketCount;
    private H3Core h3;

    public GeoPartition(int resolution) {
        if (resolution < MIN_RESOLUTION || resolution > MAX_RESOLUTION) {
            throw new IllegalArgumentException("Invalid resolution, must be integer from "+MIN_RESOLUTION+" to "+MAX_RESOLUTION+" (inclusive)");
        }
        this.resolution = resolution;
        //this.idBucketCount = ConfigManager.getInstance().getIntProperty("database.partition.id_bucket_count", 1000000);
        this.idBucketCount = 1000000;
        this.geometryFactory = new GeometryFactory();
        try {
            this.h3 = H3Core.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing H3: " + e.getMessage());
        }
    }

    public GeoPartition() {
        this(DEFAULT_RESOLUTION);
    }

    public String getGeoPartitionForPoint(@NotNull Point point) {
        long cell = getH3CellForPoint(point);
        return h3.h3ToString(cell);
    }

    public List<String> getGeoPartitions(@NotNull Polygon polygon) {
        return streamGeoPartitions(polygon).collect(Collectors.toList());
    }

    public Stream<String> streamGeoPartitions(@NotNull Polygon polygon) {
        List<Long> partitionList = getInternalPartitions(polygon);
        return partitionList.stream().map(h3::h3ToString);
    }

    public List<Polygon> getGeoPartitionPolygons(@NotNull Polygon polygon) {
        List<Long> partitionList = getInternalPartitions(polygon);
        return asPolygons(partitionList);
    }

    public int bucketForId(String id) {
        return Math.abs(id.hashCode()) % this.idBucketCount;
    }

    private List<Long> getInternalPartitions(@NotNull Polygon polygon) {
        List<Long> partitionList = getInitialPartitions(polygon);

        MultiPolygon coveragePolygon = null;

        while (null == coveragePolygon || !coveragePolygon.covers(polygon)) {

            // Convert cells to JTS MultiPolygon to check coverage
            coveragePolygon = asMultiPolygon(partitionList);

            // Find what areas are uncovered
            Geometry uncoveredAreas = polygon.difference(coveragePolygon);
            if (uncoveredAreas.isEmpty()) {
                break; // everything is covered!
            }

            // For each disconnected part of the uncovered areas, find additional cells to cover them.
            for (int i = 0; i < uncoveredAreas.getNumGeometries(); i++) {
                Geometry part = uncoveredAreas.getGeometryN(i);
                if (part instanceof Polygon) {
                    List<Long> additionalCells = getInitialPartitions((Polygon) part);
                    partitionList.addAll(additionalCells);
                }
            }

            // Remove duplicates and ensure all cells are unique.
            partitionList = new ArrayList<>(new HashSet<>(partitionList));
        }
        return partitionList;
    }

    private @NotNull Long getH3CellForPoint(@NotNull Point point) {
        return h3.latLngToCell(point.getY(), point.getX(), this.resolution);
    }

    private @NotNull List<Long> getInitialPartitions(@NotNull Polygon polygon) {
        List<LatLng> shell = convertCoordsToLatLngList(polygon.getExteriorRing().getCoordinates());
        List<List<LatLng>> holes = IntStream.range(0, polygon.getNumInteriorRing())
                .mapToObj(i -> convertCoordsToLatLngList(polygon.getInteriorRingN(i).getCoordinates()))
                .collect(Collectors.toList());

        List<Long> partitionList = new ArrayList<>(h3.polygonToCells(shell, holes, this.resolution));

        if (partitionList.isEmpty()) {
            partitionList.add(getH3CellForPoint(polygon.getCentroid()));
        }

        return partitionList;
    }

    private List<Polygon> asPolygons(List<Long> cells) {
        return h3.cellsToMultiPolygon(cells, false)
                .stream()
                .map(this::convertH3PolygonToJtsPolygon)
                .toList();
    }

    private  MultiPolygon asMultiPolygon(List<Long> cells) {
        List<Polygon> polygons = asPolygons(cells);
        return new MultiPolygon(polygons.toArray(new Polygon[0]), geometryFactory);
    }

    private Polygon convertH3PolygonToJtsPolygon(@NotNull List<List<LatLng>> h3Polygon) {
        LinearRing outerRing = geometryFactory.createLinearRing(
                convertLatLngListToCoords(h3Polygon.get(0)));
        LinearRing[] holes = h3Polygon.stream().skip(1)
                .map(this::convertLatLngListToCoords)
                .map(geometryFactory::createLinearRing)
                .toArray(LinearRing[]::new);

        return geometryFactory.createPolygon(outerRing, holes);
    }

    private List<LatLng> convertCoordsToLatLngList(Coordinate[] coordinates) {
        return Arrays.stream(coordinates)
                .map(coord -> new LatLng(coord.y, coord.x))
                .collect(Collectors.toList());
    }

    private Coordinate @NotNull[] convertLatLngListToCoords(@NotNull List<LatLng> latLngs) {
        Coordinate[] coords = new Coordinate[latLngs.size() + 1];
        for (int i = 0; i < latLngs.size(); i++) {
            coords[i] = new Coordinate(latLngs.get(i).lng, latLngs.get(i).lat);
        }
        coords[latLngs.size()] = coords[0];  // Close the ring by repeating the first coordinate
        return coords;
    }
}