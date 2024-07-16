package us.anant.cass.stac.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CatalogDto {
	private String type;
	private String stac_version;
	private List<String> stac_extensions;
	private String id;
	private String title;
	private String description;
	private List<LinkDto> links;
}
