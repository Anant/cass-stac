package com.datastax.oss.cass_stac.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.datastax.oss.cass_stac.dto.itemfeature.CatalogDto;
import com.datastax.oss.cass_stac.dto.itemfeature.LinkDto;

@Service
public class IndexService {

	public CatalogDto getCatalog() {
		return createCatalog();
	}
	
	private CatalogDto createCatalog() {
		final String[] comformsTo = {
			"https://api.stacspec.org/v1.0.0/core",
        "https://api.stacspec.org/v1.0.0/item-search"
		};
		final LinkDto relDto = LinkDto.builder()
								.href("https://stac-api.example.com")
								.rel("self")
								.type("application/json")
								.build();

		final LinkDto rootDto = LinkDto.builder()
								.href("https://stac-api.example.com")
								.rel("root")
								.type("application/json")
								.build();

		final LinkDto servicedescDto = LinkDto.builder()
								.href("https://stac-api.example.com/api")
								.rel("service-desc")
								.type("application/vnd.oai.openapi+json;version=3.0")
								.build();

		final LinkDto serviceDocDto = LinkDto.builder()
								.href("https://stac-api.example.com/api.html")
								.rel("service-doc")
								.type("text/html")
								.build();
		
		final LinkDto serviceGetcDto = LinkDto.builder()
								.href("https://stac-api.example.com")
								.rel("search")
								.type("application/json")
								.method("GET")
								.build();
		final LinkDto servicePostcDto = LinkDto.builder()
								.href("https://stac-api.example.com/search")
								.rel("search")
								.type("application/json")
								.method("POST")
								.build();
		final List<LinkDto> links = new ArrayList<>();
		links.add(relDto);
		links.add(rootDto);
		links.add(servicedescDto);
		links.add(serviceDocDto);
		links.add(serviceGetcDto);
		links.add(servicePostcDto);

		return CatalogDto.builder()
				.stac_extensions(null)
				.stac_version("1.0")
				.id("examples")
				.type("Catalog")
				.description("This Catalog aims to demonstrate the a simple landing page")
				.title("A simple STAC API Example")
				.conformsTo(Arrays.asList(comformsTo))
				.links(links)
				.build();
	}
}
