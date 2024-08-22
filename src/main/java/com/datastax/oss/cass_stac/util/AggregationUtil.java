package com.datastax.oss.cass_stac.util;

import com.datastax.oss.cass_stac.entity.Aggregation;
import com.datastax.oss.cass_stac.entity.Item;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public enum AggregationUtil {

    TOTAL_COUNT("total_count") {
        @Override
        public Aggregation apply(List<Item> items) {
            return new Aggregation("total_count", "numeric", null, 0, Optional.of(items.size()));
        }
    },
    DATETIME_MIN("datetime_min") {
        @Override
        public Aggregation apply(List<Item> items) {
            return new Aggregation("datetime_min", "datetime", null, 0, Optional.of( items.stream()
                    .map(Item::getDatetime)
                    .min(Instant::compareTo)
                    .orElse(null)));
        }
    },
    DATETIME_MAX("datetime_max") {
        @Override
        public Aggregation apply(List<Item> items) {
            return new Aggregation("datetime_max", "datetime", null, 0, Optional.of( items.stream()
                    .map(Item::getDatetime)
                    .max(Instant::compareTo)
                    .orElse(null)));
        }
    },
    COLLECTION_FREQUENCY("collection_frequency") {
        @Override
        public Aggregation apply(List<Item> items) {
            Map<String, Long> frequencyMap = items.stream().collect(Collectors.groupingBy(Item::getCollection, Collectors.counting()));
            List<Aggregation.Bucket> buckets =
                    frequencyMap.entrySet().stream()
                            .map(entry -> {
                                Aggregation.Bucket bucket = new Aggregation.Bucket();
                                bucket.setKey(entry.getKey());
                                bucket.setFrequency(Math.toIntExact(entry.getValue()));
                                bucket.setData_type("numeric");
                                return bucket;
                            })
                            .collect(Collectors.toList());
            return new Aggregation("collections", "numeric", buckets, 0, Optional.empty());
        }
    },
//    CLOUD_COVER_FREQUENCY("cloud_cover_frequency") {
//        @Override
//        public Aggregation apply(List<Item> items) {
//        }
//    },
    DATETIME_FREQUENCY("datetime_frequency") {
        @Override
        public Aggregation apply(List<Item> items) {
            Map<Instant, Long> frequencyMap = items.stream().collect(Collectors.groupingBy(Item::getDatetime, Collectors.counting()));
            List<Aggregation.Bucket> buckets =
                    frequencyMap.entrySet().stream()
                            .map(entry -> {
                                Aggregation.Bucket bucket = new Aggregation.Bucket();
                                bucket.setKey(entry.getKey().toString());
                                bucket.setFrequency(Math.toIntExact(entry.getValue()));
                                bucket.setData_type("datetime");
                                return bucket;
                            })
                            .collect(Collectors.toList());
            return new Aggregation("datetime", "numeric", buckets, 0, Optional.empty());

        }
    };

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


