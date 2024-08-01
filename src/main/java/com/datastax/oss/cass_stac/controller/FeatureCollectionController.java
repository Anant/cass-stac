package com.datastax.oss.cass_stac.controller;

import com.datastax.oss.cass_stac.dto.collection.CollectionDto;
import com.datastax.oss.cass_stac.service.CollectionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Hidden
@RequestMapping("/feature-collection")
@Tag(name = "Feature Collection", description = "The STAC Features to insert")
public class FeatureCollectionController {

    @Autowired
    private CollectionService collectionService;

    @PostMapping
    public ResponseEntity<?> parseFeatures(@RequestBody final String json) {
        try {
            CollectionDto collection = collectionService.parseFeatureFromJson(json);
            return new ResponseEntity<>(collection, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
