package com.datastax.oss.cass_stac.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemSearchRequest {
    @JsonProperty("bbox")
    private List<Float> bbox;
    @JsonProperty("datetime")
    private String datetime;
    @JsonProperty("intersects")
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry intersects;
    @JsonProperty("collections")
    private List<String> collections;
    @JsonProperty("ids")
    private List<String> ids;
    @JsonProperty("limit")
    private Integer limit = 10;
}
