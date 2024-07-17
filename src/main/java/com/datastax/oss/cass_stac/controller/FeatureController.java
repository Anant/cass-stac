package com.datastax.oss.cass_stac.controller;

import com.datastax.oss.cass_stac.model.FeatureModelResponse;
import com.datastax.oss.cass_stac.service.FeatureService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feature")
@Tag(name="Feature", description="The STAC Feature to insert and get")
@Schema(hidden = true)
public class FeatureController {

	private final FeatureService featureService;

	@Operation(description="POST method to store Feature data")
	@PostMapping
	public ResponseEntity<?> saveFeature(@RequestBody final String json) {

		final Map<String, String> message = new HashMap<>();

		try {
			message.put("message", "Feature Added Suucessful");
			featureService.save(json);
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception ex) {
			message.put("message", ex.getLocalizedMessage());
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(description = "Get Feature by passing the values")
	@GetMapping
	public ResponseEntity<?> getFeature(@RequestParam final String itemid,
										@RequestParam(required = false) final String label,
										@RequestParam(required = false) final String datetime) {
		try {
			final List<FeatureModelResponse> featureModel = featureService.getFeatureByItemId(itemid, label, datetime);
			return new ResponseEntity<>(featureModel, HttpStatus.OK);
		} catch (Exception ex) {
			final Map<String, String> message = new HashMap<>();
			message.put("message", ex.getLocalizedMessage());
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
