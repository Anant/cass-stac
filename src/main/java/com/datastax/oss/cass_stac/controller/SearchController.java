package com.datastax.oss.cass_stac.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.cass_stac.dao.ItemDao;
import com.datastax.oss.cass_stac.dto.itemfeature.ItemDto;

import lombok.RequiredArgsConstructor;

@Hidden
@RestController
@RequiredArgsConstructor
@Schema(hidden = true)
public class SearchController {
	private final ItemDao itemDao;
	
	public List<ItemDto> search() {
		return null;
	}
}
