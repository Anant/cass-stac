package com.datastax.oss.cass_stac.dto.itemfeature;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GeometryDto {
	private String type;
	private List<Double[]> coordinates;
}
