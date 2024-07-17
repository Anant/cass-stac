package com.datastax.oss.cass_stac.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.cass_stac.dto.itemfeature.CatalogDto;
import com.datastax.oss.cass_stac.service.IndexService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Hidden
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name="Catalog", description="The index or Home")
@Schema(hidden = true)
public class IndexController {
	
	private final IndexService indexService;
	
	@Operation(description = "Just return a catalog")
	@GetMapping
	public ResponseEntity<?> index() {
		final CatalogDto dto = indexService.getCatalog();
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
}
