package com.datastax.oss.cass_stac.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@Table(value = "feature_collection")
public class FeatureCollection {
	@PrimaryKey
	private String item_id;
	private String additional_attributes;
	private String partition_id;
	private String properties;
	
}
