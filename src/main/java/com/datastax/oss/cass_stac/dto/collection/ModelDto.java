package com.datastax.oss.cass_stac.dto.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModelDto {
    @JsonProperty("id")
    private String id;
    @JsonProperty("params")
    private ModelParamsDto params;
}
