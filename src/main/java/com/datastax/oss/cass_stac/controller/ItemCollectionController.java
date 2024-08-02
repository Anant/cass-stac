package com.datastax.oss.cass_stac.controller;

import com.datastax.oss.cass_stac.model.ItemModelResponse;
import com.datastax.oss.cass_stac.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-collection")
@Tag(name = "Item Collection", description = "The STAC Item object is the most important object in a STAC system. An Item is the entity that contains metadata for a scene and links to the assets.\n" +
        "Item objects are the leaf nodes for a graph of Catalog and Collection objects.")
@Schema(hidden = true)
public class ItemCollectionController {

    private static final Logger logger = LoggerFactory.getLogger(ItemCollectionController.class);
    private final ItemService itemService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(description="POST method to store Item data as per new format")
    @PostMapping
    public ResponseEntity<?> saveNewItem(@RequestBody final String json) {
        final Map<String, String> message = new HashMap<>();
        try {
            logger.debug("Received JSON for saving new item: " + json);
            message.put("message", "New Item Added Successfully");
            itemService.saveNewGeoJson(json);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Failed to save new item.", ex);
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
