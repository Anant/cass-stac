package com.datastax.oss.cass_stac.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GeometryDto {
	private String type;
	private List<Float[][]> coordinates;
}
