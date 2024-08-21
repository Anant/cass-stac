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
public class Aggregation {
    private String name;
    private String data_type;
    private List<Bucket> buckets;
    private Integer overflow;
    private Object value;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Bucket {
    private String key;
    private String data_type;
    private Integer frequency;
    private Double from;
    private Double to;
}