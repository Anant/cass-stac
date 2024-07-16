package us.anant.cass.stac.dto;

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
