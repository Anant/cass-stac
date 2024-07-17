package com.datastax.oss.cass_stac.dto.collection.serializer;

import com.datastax.oss.cass_stac.dto.collection.CollectionDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Map;

public class CollectionDtoSerializer extends JsonSerializer<CollectionDto> {
    @Override
    public void serialize(CollectionDto value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        if (value.getItemId() != null) {
            gen.writeStringField("item_id", value.getItemId());
        }
        if (value.getType() != null) {
            gen.writeStringField("type", value.getType());
        }
        if (value.getCrs() != null) {
            gen.writeObjectField("crs", value.getCrs());
        }
        if (value.getProperties() != null) {
            gen.writeObjectField("properties", value.getProperties());
        }
        if (value.getFeatures() != null) {
            gen.writeObjectField("features", value.getFeatures());
        }
        if (value.getAdditionalProperties() != null) {
            for (Map.Entry<String, JsonNode> entry : value.getAdditionalProperties().entrySet()) {
                gen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }
        gen.writeEndObject();
    }
}
