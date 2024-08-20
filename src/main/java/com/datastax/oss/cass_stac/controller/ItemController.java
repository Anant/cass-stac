package com.datastax.oss.cass_stac.controller;

import com.datastax.oss.cass_stac.model.ImageResponse;
import com.datastax.oss.cass_stac.model.ItemModelRequest;
import com.datastax.oss.cass_stac.model.ItemModelResponse;
import com.datastax.oss.cass_stac.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
@Tag(name = "Item", description = "The STAC Item object is the most important object in a STAC system. An Item is the entity that contains metadata for a scene and links to the assets.\n" +
        "Item objects are the leaf nodes for a graph of Catalog and Collection objects.")
@Schema(hidden = true)
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(description="POST method to store Item data")
    @PostMapping
    public ResponseEntity<?> saveItem(@RequestBody final String json) {
        final Map<String, String> message = new HashMap<>();
        try {
            logger.debug("Received JSON for saving item: " + json);
            message.put("message", "Item Added Successfully");
            itemService.saveGeoJson(json);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Failed to save item.", ex);
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(description="POST method to store Item data in batches")
    @PostMapping("/batch")
    public ResponseEntity<?> saveItems(@RequestBody final List<Map<String, Object>> items) {
        final Map<String, String> message = new HashMap<>();
        try {
            for (Map<String, Object> item : items) {
                String json = objectMapper.writeValueAsString(item);
                logger.debug("Received JSON content for saving item: " + json);
                itemService.saveGeoJson(json);
            }
            message.put("message", "Items Added Successfully");
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Failed to save items.", ex);
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(description = "Get method to fetch Item data based on item ID")
    @GetMapping
    public ResponseEntity<?> getItem(@RequestParam final String id) {
        try {
            final ItemModelResponse itemModel = itemService.getItemById(id);
            return new ResponseEntity<>(itemModel, HttpStatus.OK);
        } catch (Exception ex) {
            final Map<String, String> message = new HashMap<>();
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(description = "Fetch partitions from Item data given spatio-temporal data")
    @PostMapping("/images")
    public ResponseEntity<?> getImages(@RequestBody final ItemModelRequest request,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime minDate,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime maxDate,
                                       @RequestParam(required = false) final List<String> objectTypeFilter,
                                       @RequestParam(required = false) final String whereClause,
                                       @RequestParam(required = false) final Object bindVars,
                                       @RequestParam(required = false, defaultValue = "false") final Boolean useCentroid,
                                       @RequestParam(required = false, defaultValue = "true") final Boolean filterObjectsByPolygon,
                                       @RequestParam(required = false, defaultValue = "true") final Boolean includeIds,
                                       @RequestParam(required = false, defaultValue = "true") final Boolean includeCount,
                                       @RequestParam(required = false, defaultValue = "false") final Boolean includeObjects) {

        final Map<String, String> message = new HashMap<>();
        try {
            logger.debug("Request {}", request.toString());
            ImageResponse response = itemService.getPartitions(request,
                    minDate,
                    maxDate,
                    objectTypeFilter,
                    whereClause,
                    bindVars,
                    useCentroid,
                    includeCount,
                    includeIds,
                    filterObjectsByPolygon,
                    includeObjects);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Failed to fetch partitions.", ex);
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
