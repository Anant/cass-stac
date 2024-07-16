package us.anant.cass.stac.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GeometryDto {
	private String type;
	private List<List<Float>> coordinates;
}
