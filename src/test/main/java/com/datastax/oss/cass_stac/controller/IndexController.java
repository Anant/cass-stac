package com.datastax.oss.cass_stac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.cass_stac.dto.CatalogDto;
import com.datastax.oss.cass_stac.service.IndexService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {
	
	private final IndexService indexService;
	
	@GetMapping
	public ResponseEntity<?> index() {
		final CatalogDto dto = indexService.getCatalog();
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
}
