package com.datastax.oss.cass_stac.service;

import org.springframework.stereotype.Service;

import com.datastax.oss.cass_stac.dto.CatalogDto;

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
