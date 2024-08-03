package com.datastax.oss.cass_stac.model;

import com.datastax.oss.cass_stac.entity.Item;
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
public class ItemSearchResponse {
    private String type = "FeatureCollection";
    private Optional<List<Item>> features;
    private Optional<List<String>> partition_ids;
    private Optional<Integer> numberMatched;
    private Optional<Integer> numberReturned;

}
