package com.datastax.oss.cass_stac.entity;

import com.datastax.oss.driver.api.core.data.CqlVector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Map;

@Data
@Getter
@Setter
public class Item {
    @PrimaryKey
    private ItemPrimaryKey id;
    private String collection;
    private Instant datetime;
    private ByteBuffer geometry;
    private Map<String, String> indexed_properties_text;
    private Map<String, Number> indexed_properties_double;
    private Map<String, Boolean> indexed_properties_boolean;
    private Map<String, Instant> indexed_properties_timestamp;
    private String properties;
    private String additional_attributes;
    private CqlVector<Float> centroid;

    public Double getCloudCover() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Map<String, Object> map = objectMapper.readValue(properties, new TypeReference<>() {
        });
        Object value = map.get("eo:cloud_cover");
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                return Double.longBitsToDouble(Long.getLong(value.toString()));
            }
        }
        return null;
    }
}
