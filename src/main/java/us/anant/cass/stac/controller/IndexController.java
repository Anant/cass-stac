package us.anant.cass.stac.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import us.anant.cass.stac.dto.CatalogDto;
import us.anant.cass.stac.service.IndexService;

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
