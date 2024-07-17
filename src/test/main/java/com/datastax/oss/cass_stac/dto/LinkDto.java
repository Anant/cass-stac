package com.datastax.oss.cass_stac.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LinkDto {
	private String href;
	private String rel;
	private String type;
	private String title;
}
