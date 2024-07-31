package com.datastax.oss.cass_stac.util;

import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class GeoTimePartition {

    private final int geoResolution;
    private final TimeResolution timeResolution;

    public GeoTimePartition(int geoResolution, TimeResolution timeResolution) {
        this.geoResolution = geoResolution;
        this.timeResolution = timeResolution;
    }

    public String getGeoTimePartitionForPoint(Point point, OffsetDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("datetime field is required but is missing or null");
        }

        String geoHash = getGeoHash(point, geoResolution);
        String timeHash = getTimeHash(dateTime, timeResolution);

        return geoHash + "-" + timeHash;
    }

    private String getGeoHash(Point point, int resolution) {
        // Implement a simple geohashing logic
        double lat = point.getY();
        double lon = point.getX();
        return String.format("%s,%s", roundToResolution(lat, resolution), roundToResolution(lon, resolution));
    }

    private String roundToResolution(double value, int resolution) {

        double scale = Math.pow(10, resolution);
        return String.format("%.0f", Math.floor(value * scale) / scale);
    }

    private String getTimeHash(OffsetDateTime dateTime, TimeResolution resolution) {
        DateTimeFormatter formatter = switch (resolution) {
            case YEAR -> DateTimeFormatter.ofPattern("yyyy");
            case MONTH -> DateTimeFormatter.ofPattern("yyyy-MM");
            case DAY -> DateTimeFormatter.ofPattern("yyyy-MM-dd");
        };
        return dateTime.format(formatter);
    }

    public enum TimeResolution {
        YEAR,
        MONTH,
        DAY
    }
}
