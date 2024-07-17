package com.datastax.oss.cass_stac.entity;

import java.time.Instant;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table(value = "item_ids")
public class ItemId {
	@PrimaryKey
	private String id;
	private Instant datetime;
	private String partition_id;
	
}
