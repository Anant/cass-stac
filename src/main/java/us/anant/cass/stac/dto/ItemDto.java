package us.anant.cass.stac.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemDto {
	private String type;
	private String stac_version;
	private List<String> stac_extensions;
	private String id;
	private GeoDto geometry;
	private List<Float> bbox;
	private String collection;
	private List<LinkDto> links;
	private Map<String, AssetDto> assets;
}
