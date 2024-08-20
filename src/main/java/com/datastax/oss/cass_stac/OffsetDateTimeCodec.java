package com.datastax.oss.cass_stac;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.time.*;

public class OffsetDateTimeCodec implements TypeCodec<OffsetDateTime> {
    public Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public OffsetDateTimeCodec() {
    }

    @Override
    public @NotNull GenericType<OffsetDateTime> getJavaType() {
        return GenericType.of(OffsetDateTime.class);
    }

    @Override
    public @NotNull DataType getCqlType() {
        return DataTypes.TIMESTAMP;
    }

    @Override
    public ByteBuffer encode(OffsetDateTime value, @NotNull ProtocolVersion protocolVersion) {
        if (value == null) {
            return null;
        }
        Instant instantValue = value.toInstant();
        return TypeCodecs.TIMESTAMP.encode(instantValue, protocolVersion);
    }

    @Override
    public OffsetDateTime decode(ByteBuffer bytes, @NotNull ProtocolVersion protocolVersion) {
        Instant instantValue = TypeCodecs.TIMESTAMP.decode(bytes, protocolVersion);
        return instantValue == null ? null : instantValue.atOffset(ZoneOffset.UTC);
    }

    @Override
    // We get in a string of our format, we need to convert to a proper CQL-formatted string
    public @NotNull String format(OffsetDateTime value) {
        return value == null ? "" : value.toString();
    }

    @Override
    // We get in a proper CQL-formatted string, we need to convert to our format
    public OffsetDateTime parse(String value) {
        return OffsetDateTime.parse(value);
    }
}