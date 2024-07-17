package com.datastax.oss.cass_stac.dto.collection;

import com.datastax.oss.cass_stac.dto.collection.serializer.CoordinateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.locationtech.jts.algorithm.Centroid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.util.Map;

@Data
public class FeatureDto {
    private String type;
    private GeometryDto geometry;
    private Map<String, Object> properties;

    @JsonSerialize(using = CoordinateSerializer.class)
    private Coordinate centroid;
}
