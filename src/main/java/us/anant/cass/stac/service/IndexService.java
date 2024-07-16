package us.anant.cass.stac.service;

import org.springframework.stereotype.Service;

import us.anant.cass.stac.dto.CatalogDto;

@Service
public class IndexService {

	public CatalogDto getCatalog() {
		return createCatalog();
	}
	
	private CatalogDto createCatalog() {
		return CatalogDto.builder()
				.id("examples")
				.type("Catalog")
				.build();
	}
}
