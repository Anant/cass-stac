package com.datastax.oss.cass_stac.controller;

import com.datastax.oss.cass_stac.model.ItemModelResponse;
import com.datastax.oss.cass_stac.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Tag(name = "Items", description = "The STAC Item object is the most important object in a STAC system. An Item is the entity that contains metadata for a scene and links to the assets.\n" +
        "Item objects are the leaf nodes for a graph of Catalog and Collection objects.")
@Schema(hidden = true)
public class ParallelItemController {

    private final ItemService itemService;

    @Operation(description = "Get method to fetch Item data in parallel based on a list of item IDs")
    @GetMapping
    public ResponseEntity<?> getItemsParallel(@RequestParam final List<String> ids) {
        try {
            final List<ItemModelResponse> itemModels = ids.stream()
		    .map(itemService::getItemsByIdParallel)
		    .toList().stream().map(CompletableFuture::join).collect(Collectors.toList());
            return new ResponseEntity<>(itemModels, HttpStatus.OK);
        } catch (Exception ex) {
            final Map<String, String> message = new HashMap<>();
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
