package com.datastax.oss.cass_stac.dto.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GeometryDto {
    @JsonProperty("type")
    private String type;
    @JsonProperty("coordinates")
    private List<List<List<Double>>> coordinates;
}
