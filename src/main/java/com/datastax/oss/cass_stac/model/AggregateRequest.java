package com.datastax.oss.cass_stac.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AggregateRequest {
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
    @JsonProperty("query")
    private Map<String, Map<String, Object>> query;
    @JsonProperty("aggregations")
    private List<String> aggregations;
    @JsonProperty("ranges")
    private List<Range> ranges;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Range {
        private Optional<Double> from = Optional.of(Double.MIN_VALUE);
        private Optional<Double> to = Optional.of(Double.MAX_VALUE);


        public boolean contains(double value) {
            return value >= from.orElse(Double.MIN_VALUE) && value < to.orElse(Double.MAX_VALUE);
        }

        public String toString() {
            String _from = from.isPresent() ? from.get() == Double.MIN_VALUE ? "*" : from.get().toString() : "*";
            String _to = to.isPresent() ? to.get() == Double.MAX_VALUE ? "*" : to.get().toString() : "*";
            return _from + "-" + _to;
        }
    }
}
