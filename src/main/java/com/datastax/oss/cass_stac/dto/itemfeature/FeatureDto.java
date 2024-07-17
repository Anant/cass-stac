package com.datastax.oss.cass_stac.dto.itemfeature;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class FeatureDto {
	private String type;
	private String stac_version;
	private List<String> stac_extensions;
	private String id;
	private String label;
	private String partition_id;
	private GeometryDto geometry;
	private List<Float> bbox;
	private String collection;
	private List<LinkDto> links;
	private Map<String, AssetDto> assets;
	private Map<String, Object> properties;
	private String additional_attributes;
}
