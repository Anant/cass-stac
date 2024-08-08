package com.datastax.oss.cass_stac.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Query {
    private Property property_name;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Property {
    private String operator;
}