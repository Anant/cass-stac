package com.datastax.oss.cass_stac.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCollection {
    private String type = "FeatureCollection"; // REQUIRED. Always "FeatureCollection" to provide compatibility with GeoJSON.
    private Optional<List<Item>> features;
    private Optional<List<String>> partition_ids;
    private Optional<Integer> numberMatched;
    private Optional<Integer> numberReturned;
}
