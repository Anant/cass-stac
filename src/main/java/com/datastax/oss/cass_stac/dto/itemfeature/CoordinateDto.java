package com.datastax.oss.cass_stac.dto.itemfeature;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CoordinateDto {
	private Float x;
	private Float y;
}
