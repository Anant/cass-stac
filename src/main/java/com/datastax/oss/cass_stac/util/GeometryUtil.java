package com.datastax.oss.cass_stac.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;

import com.datastax.oss.cass_stac.dto.itemfeature.GeometryDto;

public class GeometryUtil {


    public static Geometry fromGeometryByteBuffer(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.duplicate().get(bytes);
            return new WKBReader().read(bytes);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse geometry from ByteBuffer", e);
        }
    }

    public static ByteBuffer toByteBuffer(Geometry geometry) {
        if (geometry == null) {
            return ByteBuffer.allocate(0);
        }
        byte[] bytes = new WKBWriter().write(geometry);
        return ByteBuffer.wrap(bytes);
    }
    
    public static  Geometry createGeometryFromDto(GeometryDto geometryDto) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (Double[] coordinate : geometryDto.getCoordinates()) {
                coordinates.add(new Coordinate(coordinate[0], coordinate[1]));
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPolygon(coordinates.toArray(new Coordinate[0]));
}

}
