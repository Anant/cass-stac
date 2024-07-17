package com.datastax.oss.cass_stac.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.cass_stac.dao.ItemDao;
import com.datastax.oss.cass_stac.dto.itemfeature.ItemDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SearchController {
	private final ItemDao itemDao;
	
	public List<ItemDto> search() {
		return null;
	}
}
