package com.datastax.oss.cass_stac.util;

import java.nio.ByteBuffer;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;

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

}
