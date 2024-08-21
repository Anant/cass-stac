package com.datastax.oss.cass_stac.controller;

import com.datastax.oss.cass_stac.entity.AggregationCollection;
import com.datastax.oss.cass_stac.model.AggregateRequest;
import com.datastax.oss.cass_stac.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Tag(name = "aggregate", description = "Item Aggregations.")
public class AggController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ItemService itemService;

    @Operation(summary = "Aggregate STAC items.",
            description = "Retrieves an aggregation of the group of Items matching the provided predicates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "An Aggregation Collection.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AggregationCollection.class)),
                            @Content(mediaType = "text/html",
                                    schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "4XX", description = "Client error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "5XX", description = "Server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/aggregate")
    public ResponseEntity<?> aggregate(
            @Parameter @RequestBody final AggregateRequest request) {

        final Map<String, String> message = new HashMap<>();
        if(request.getIntersects() != null && request.getBbox() != null)
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        try {
            AggregationCollection response = itemService.agg(
                    request.getBbox(),
                    request.getIntersects(),
                    request.getDatetime(),
                    request.getIds(),
                    request.getCollections(),
                    request.getQuery(),
                    request.getAggregations()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Failed to fetch partitions.", ex);
            message.put("message", ex.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

