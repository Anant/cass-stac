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
public class ImageResponse {
    private Optional<List<String>> partitions = Optional.empty();
    private Optional<Integer> count = Optional.empty();
    private Optional<List<Item>> items = Optional.empty();
}
