package com.datastax.oss.cass_stac.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregationCollection {
    private final String type = "AggregationCollection"; // REQUIRED. Always "AggregationCollection"
    private List<Aggregation> aggregations;
}
