package com.datastax.oss.cass_stac.model;

import com.datastax.oss.cass_stac.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private List<String> partitions;
    private Integer count;
    private Optional<List<Item>> items = Optional.empty();
}
