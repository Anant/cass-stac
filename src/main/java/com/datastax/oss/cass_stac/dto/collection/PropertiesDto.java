package com.datastax.oss.cass_stac.dto.collection;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PropertiesDto {
    @JsonProperty("tile_id")
    private String tileId;
    private ModelDto model;

    private Map<String, JsonNode> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String key, JsonNode value) {
        this.additionalProperties.put(key, value);
    }
}
