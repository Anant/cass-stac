package com.datastax.oss.cass_stac.dto.collection;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.datastax.oss.cass_stac.dto.collection.serializer.CollectionDtoSerializer;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonSerialize(using = CollectionDtoSerializer.class)
public class CollectionDto {
    private String type;
    @JsonProperty("item_id")
    private String itemId;
    private Map<String, JsonNode> crs = new HashMap<>();
    private PropertiesDto properties;
    private List<FeatureDto> features;

    private Map<String, JsonNode> additionalProperties = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalProperty(String key, JsonNode value) {
        this.additionalProperties.put(key, value);
    }
}
