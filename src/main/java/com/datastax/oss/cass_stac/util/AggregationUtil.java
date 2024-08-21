package com.datastax.oss.cass_stac.util;

import com.datastax.oss.cass_stac.entity.Aggregation;
import com.datastax.oss.cass_stac.entity.Item;
import lombok.Getter;

import java.util.List;

@Getter
public enum AggregationUtil {

    TOTAL_COUNT("total_count") {
        @Override
        public Aggregation apply(List<Item> items) {
//            private String name;
//            private String data_type;
//            private List<Bucket> buckets;
//            private Integer overflow;
//            private Object value;
            return new Aggregation("total_count", "numeric", null, 0, items.size());
        }
    };
//    DATETIME_MIN("datetime_min") {
//        @Override
//        public Aggregation apply(List<Item> items) {
//            return items.stream()
//                    .map(Item::getDatetime)
//                    .min(Instant::compareTo)
//                    .orElse(null);
//        }
//    },
//    DATETIME_MAX("datetime_max") {
//        @Override
//        public Aggregation apply(List<Item> items) {
//            return items.stream()
//                    .map(Item::getDatetime)
//                    .max(Instant::compareTo)
//                    .orElse(null);
//        }
//    },
//    COLLECTION_FREQUENCY("collection_frequency") {
//        @Override
//        public Aggregation apply(List<Item> items) {
//        }
//    },
//    CLOUD_COVER_FREQUENCY("cloud_cover_frequency") {
//        @Override
//        public Aggregation apply(List<Item> items) {
//        }
//    },
//    DATETIME_FREQUENCY("datetime_frequency") {
//        @Override
//        public Aggregation apply(List<Item> items) {
//        }
//    };

    private final String fieldName;

    public abstract Aggregation apply(List<Item> items);


    AggregationUtil(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return fieldName;
    }
}


