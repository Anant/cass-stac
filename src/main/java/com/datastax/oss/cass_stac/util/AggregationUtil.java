package com.datastax.oss.cass_stac.util;

import com.datastax.oss.cass_stac.entity.Aggregation;
import com.datastax.oss.cass_stac.entity.Item;
import com.datastax.oss.cass_stac.model.AggregateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Getter
public enum AggregationUtil {

    TOTAL_COUNT("total_count") {
        @Override
        public Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges) {
            return new Aggregation("total_count", "numeric", null, 0, Optional.of(items.size()));
        }
    },
    DATETIME_MIN("datetime_min") {
        @Override
        public Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges) {
            return new Aggregation("datetime_min", "datetime", null, 0, Optional.ofNullable(items.stream()
                    .map(Item::getDatetime)
                    .min(Instant::compareTo)
                    .orElse(null)));
        }
    },
    DATETIME_MAX("datetime_max") {
        @Override
        public Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges) {
            return new Aggregation("datetime_max", "datetime", null, 0, Optional.ofNullable(items.stream()
                    .map(Item::getDatetime)
                    .max(Instant::compareTo)
                    .orElse(null)));
        }
    },
    COLLECTION_FREQUENCY("collection_frequency") {
        @Override
        public Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges) {
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
    CLOUD_COVER_FREQUENCY("cloud_cover_frequency") {
        @Override
        public Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges) {
            Map<Optional<AggregateRequest.Range>, Long> frequencyMap = items.stream().collect(Collectors.groupingBy(item -> {
                Double value;
                try {
                    value = item.getCloudCover();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                List<AggregateRequest.Range> _ranges = new ArrayList<>();
                        if(ranges == null)
                            _ranges.add(new AggregateRequest.Range());
                        else _ranges = ranges;
                return _ranges.stream()
                        .filter(range -> range.contains(value)).findFirst();
            }, Collectors.counting()));
            List<Aggregation.Bucket> buckets =
                    frequencyMap.entrySet().stream()
                            .map(entry -> {
                                Aggregation.Bucket bucket = new Aggregation.Bucket();
                                if (entry.getKey().isPresent()) {
                                    bucket.setKey(entry.getKey().get().toString());
                                    bucket.setFrequency(Math.toIntExact(entry.getValue()));
                                    bucket.setData_type("numeric");
                                    if (entry.getKey().get().getFrom().isPresent())
                                        bucket.setFrom(entry.getKey().get().getFrom().orElse(null));
                                    if (entry.getKey().get().getTo().isPresent())
                                        bucket.setTo(entry.getKey().get().getTo().orElse(null));
                                }
                                return bucket;
                            })
                            .filter(bucket -> !bucket.equals(new Aggregation.Bucket()))
                            .collect(Collectors.toList());

            return new Aggregation("cloud_cover", "numeric", buckets, 0, Optional.empty());

        }
    },
    DATETIME_FREQUENCY("datetime_frequency") {
        @Override
        public Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges) {
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
    },
    DATETIME_YEARLY("datetime_yearly") {
        @Override
        public Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges) {
            Map<Integer, Long> frequencyMap = items.stream().collect(Collectors.groupingBy(item ->
             Year.from(item.getDatetime().atZone(ZoneId.systemDefault())).getValue(), Collectors.counting()));
            List<Aggregation.Bucket> buckets =
                    frequencyMap.entrySet().stream()
                            .map(entry -> {
                                Aggregation.Bucket bucket = new Aggregation.Bucket();
                                bucket.setKey(entry.getKey().toString());
                                bucket.setFrequency(Math.toIntExact(entry.getValue()));
                                bucket.setData_type("numeric");
                                return bucket;
                            })
                            .collect(Collectors.toList());
            return new Aggregation("datetime", "numeric", buckets, 0, Optional.empty());
        }
    },
    DATETIME_MONTHLY("datetime_monthly") {
        @Override
        public Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges) {
            Map<Integer, Long> frequencyMap = items.stream().collect(Collectors.groupingBy(item ->
                    Month.from(item.getDatetime().atZone(ZoneId.systemDefault())).getValue(), Collectors.counting()));
            List<Aggregation.Bucket> buckets =
                    frequencyMap.entrySet().stream()
                            .map(entry -> {
                                Aggregation.Bucket bucket = new Aggregation.Bucket();
                                bucket.setKey(entry.getKey().toString());
                                bucket.setFrequency(Math.toIntExact(entry.getValue()));
                                bucket.setData_type("numeric");
                                return bucket;
                            })
                            .collect(Collectors.toList());
            return new Aggregation("datetime", "numeric", buckets, 0, Optional.empty());
        }
    };

    private final String fieldName;

    public abstract Aggregation apply(List<Item> items, List<AggregateRequest.Range> ranges);


    AggregationUtil(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return fieldName;
    }
}


