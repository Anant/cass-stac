package com.datastax.oss.cass_stac.util;

import lombok.Getter;

@Getter
public enum AggregationUtil {
    TOTAL_COUNT("total_count"),
    DATETIME_MIN("datetime_min"),
    DATETIME_MAX("datetime_max"),
    COLLECTION_FREQUENCY("collection_frequency"),
    CLOUD_COVER_FREQUENCY("cloud_cover_frequency"),
    DATETIME_FREQUENCY("datetime_frequency");

    private final String fieldName;

    AggregationUtil(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return fieldName;
    }
}

