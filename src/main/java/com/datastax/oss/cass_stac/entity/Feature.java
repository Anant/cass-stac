package com.datastax.oss.cass_stac.entity;

import com.datastax.oss.driver.api.core.data.CqlVector;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Table(value = "feature")
public class Feature {
	@PrimaryKey
	private FeaturePrimaryKey id;
	private String additional_attributes;
	private ByteBuffer geometry;
	private Map<String, Boolean> indexed_properties_boolean;
	private Map<String, Double> indexed_properties_double;
	private Map<String, String> indexed_properties_text;
	private Map<String, Instant> indexed_properties_timestamp;
	private String properties;
}
