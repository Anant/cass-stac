package com.datastax.oss.cass_stac.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CoOrdinateDto {
	private Float x;
	private Float y;
}
