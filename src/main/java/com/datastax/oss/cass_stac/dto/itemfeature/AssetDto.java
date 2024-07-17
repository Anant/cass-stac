package com.datastax.oss.cass_stac.dto.itemfeature;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AssetDto {
	private String href;
	private String title;
	private String description;
	private String type;
	private List<String> roles;
}
