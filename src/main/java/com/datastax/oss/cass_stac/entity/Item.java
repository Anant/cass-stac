package com.datastax.oss.cass_stac.entity;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.oss.driver.api.core.data.CqlVector;

import lombok.Data;

@Data
@Table(value = "item")
@Getter
@Setter
public class Item {
	@PrimaryKey
	private ItemPrimaryKey id;
	private String collection;
	private Instant datetime;
	private ByteBuffer geometry;
	private Map<String, String> indexed_properties_text;
	private Map<String, Number> indexed_properties_double;
	private Map<String, Boolean> indexed_properties_boolean;
	private Map<String, Instant> indexed_properties_timestamp;
	private String properties;
	private String additional_attributes;
	private CqlVector<Float> centroid;
}
