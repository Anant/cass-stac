package com.datastax.oss.cass_stac.dao.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class GeometryUtil {
    private static final Logger logger = LoggerFactory.getLogger(GeometryUtil.class);

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