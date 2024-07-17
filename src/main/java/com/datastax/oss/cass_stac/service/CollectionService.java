package com.datastax.oss.cass_stac.service;

import com.datastax.oss.cass_stac.dto.collection.CollectionDto;
import com.datastax.oss.cass_stac.dto.collection.GeometryDto;
import com.datastax.oss.cass_stac.dto.collection.FeatureDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class CollectionService {
    private static final Logger logger = LoggerFactory.getLogger(CollectionService.class);
    private final ObjectMapper objectMapper;

    public CollectionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CollectionDto parseFeatureFromFile(String filePath) throws IOException {
        File jsonFile = new File(filePath);
        return parseFeatureFromJsonFile(jsonFile);
    }

    public CollectionDto parseFeatureFromJson(String json) throws IOException {
        return parseFeatureFromJsonString(json);
    }

    private CollectionDto parseFeatureFromJsonFile(File jsonFile) throws IOException {
        CollectionDto collection = objectMapper.readValue(jsonFile, CollectionDto.class);
        processCollection(collection);
        return collection;
    }

    private CollectionDto parseFeatureFromJsonString(String json) throws IOException {
        CollectionDto collection = objectMapper.readValue(json, CollectionDto.class);
        processCollection(collection);
        return collection;
    }

    private void processCollection(CollectionDto collection) {
        if (collection.getFeatures() != null && !collection.getFeatures().isEmpty()) {
            for (FeatureDto feature : collection.getFeatures()) {
                GeometryDto geometryDto = feature.getGeometry();
                Coordinate centroid = computeCentroid(geometryDto.getCoordinates().get(0));
                feature.setCentroid(centroid);
                logger.info("Computed Centroid for feature: {}", centroid);
            }
        }

        // Ensure item_id is set
        collection.setItemId(collection.getProperties().getTileId());
    }

    private Coordinate computeCentroid(List<List<Double>> coordinates) {
        GeometryFactory geometryFactory = new GeometryFactory();
        List<Coordinate> coordinateList = new ArrayList<>();

        for (List<Double> coordPair : coordinates) {
            coordinateList.add(new Coordinate(coordPair.get(0), coordPair.get(1)));
        }

        Coordinate[] coordinateArray = coordinateList.toArray(new Coordinate[0]);
        LinearRing linearRing = geometryFactory.createLinearRing(coordinateArray);
        Polygon polygon = geometryFactory.createPolygon(linearRing);

        return polygon.getCentroid().getCoordinate();
    }

}
