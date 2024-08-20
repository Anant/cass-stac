package com.datastax.oss.cass_stac.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortBy {
    private String field;
    private String direction;

    public SortBy(String direction, String field) {
        this.field = field;
        this.direction = direction;
    }
}
