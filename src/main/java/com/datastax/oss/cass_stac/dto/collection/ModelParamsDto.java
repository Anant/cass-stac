package com.datastax.oss.cass_stac.dto.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModelParamsDto {
    @JsonProperty("rotation")
    private int rotation;
}
