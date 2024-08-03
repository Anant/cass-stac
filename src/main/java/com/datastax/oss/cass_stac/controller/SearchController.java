package com.datastax.oss.cass_stac.controller;

import com.datastax.oss.cass_stac.dto.itemfeature.GeometryDto;
import com.datastax.oss.cass_stac.entity.Item;
import com.datastax.oss.cass_stac.model.ItemSearchRequest;
import com.datastax.oss.cass_stac.model.ItemSearchResponse;
import com.datastax.oss.cass_stac.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ItemService itemService;

    @Operation(summary = "Search STAC items with simple filtering.",
            description = "Retrieve Items matching filters. Intended as a shorthand API for simple queries.",
            tags = "Item Search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "An Item.",
                    content = {@Content(mediaType = "application/geo+json",
                            schema = @Schema(implementation = Item.class)),
                            @Content(mediaType = "text/html",
                                    schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "4XX", description = "Client error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "5XX", description = "Server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/item")
    public ResponseEntity<?> getItemSearch(@Parameter(description = """
            Only features that have a geometry that intersects the bounding box are selected.
            The bounding box is provided as four or six numbers, depending on whether the coordinate reference system includes a vertical axis (height or depth):""",
            schema = @Schema(type = "array", example = "[144.814896,-37.945733,144.838158,-37.927299]")) @RequestParam(required = false) List<Float> bbox,
                                           @Parameter(description = "The optional intersects parameter filters the result Items in the same was as bbox, only with a GeoJSON Geometry rather than a bbox.") @RequestParam(required = false) GeometryDto intersects,
                                           @Parameter(description = "Either a date-time or an interval, open or closed. Date and time expressions adhere to RFC 3339. Open intervals are expressed using double-dots.",
                                                   examples = {
                                                           @ExampleObject(name = "A closed interval", value = "2023-01-30T00:00:00Z/2018-03-18T12:31:12Z"),
                                                           @ExampleObject(name = "Open intervals", value = """
                                                                   "2023-01-30T00:00:00Z/.." or "../2023-01-30T12:31:12Z
                                                                   """),
                                                           @ExampleObject(name = "A date-time", value = "2023-01-30T23:20:50Z")
                                                   }) @RequestParam(required = false) String datetime,
                                           @Parameter(description = "Limit the number of results.") @RequestParam(required = false, defaultValue = "10") Integer limit,
                                           @Parameter(description = "List of item IDs to filter by.") @RequestParam(required = false) List<String> ids,
                                           @Parameter(description = "List of collections to search within.") @RequestParam(required = false) List<String> collections,
                                           @Parameter(description = "Return partition IDs") @RequestParam(defaultValue = "true") final Boolean includeIds,
                                           @Parameter(description = "Return count of partitions") @RequestParam(required = false, defaultValue = "true") final Boolean includeCount,
                                           @Parameter(description = "Return Items in response") @RequestParam(required = false, defaultValue = "false") final Boolean includeObjects) {

        try {
            ItemSearchResponse response = itemService.search(
                    bbox,
                    intersects,
                    datetime,
                    limit,
                    ids,
                    collections,
                    includeCount,
                    includeIds,
                    includeObjects);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            final Map<String, String> message = new HashMap<>();
            logger.error("Failed to fetch partitions.", ex);
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Search STAC items with simple filtering.",
            description = "Retrieve Items matching filters. Intended as a shorthand API for simple queries.",
            tags = "Item Search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "An Item.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Item.class)),
                            @Content(mediaType = "text/html",
                                    schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "4XX", description = "Client error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "5XX", description = "Server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/item")
    public ResponseEntity<?> postItemSearch(
            @Parameter @RequestBody final ItemSearchRequest request,
            @Parameter(description = "Return partition IDs") @RequestParam(defaultValue = "true") final Boolean includeIds,
            @Parameter(description = "Return count of partitions") @RequestParam(required = false, defaultValue = "true") final Boolean includeCount,
            @Parameter(description = "Return Items in response") @RequestParam(required = false, defaultValue = "false") final Boolean includeObjects) {


        final Map<String, String> message = new HashMap<>();
        try {
            ItemSearchResponse response = itemService.search(
                    request.getBbox(),
                    request.getIntersects(),
                    request.getDatetime(),
                    request.getLimit(),
                    request.getIds(),
                    request.getCollections(),
                    includeCount,
                    includeIds,
                    includeObjects);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Failed to fetch partitions.", ex);
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

