package com.datastax.oss.cass_stac.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import com.datastax.oss.driver.api.core.data.CqlVector;

import java.io.Serializable;
import java.time.Instant;


@Data
@PrimaryKeyClass
public class FeaturePrimaryKey implements Serializable{

	private static final long serialVersionUID = 6214163699083729797L;
	
	@PrimaryKeyColumn(type=PrimaryKeyType.PARTITIONED, ordinal = 0)
	private String partition_id;
	
	@PrimaryKeyColumn(type=PrimaryKeyType.PARTITIONED, ordinal = 1)
	private String item_id;

	@PrimaryKeyColumn(type=PrimaryKeyType.CLUSTERED, ordinal = 2)
	private String label;

	@PrimaryKeyColumn(type=PrimaryKeyType.CLUSTERED, ordinal = 3)
	private Instant datetime;

	@PrimaryKeyColumn(type=PrimaryKeyType.CLUSTERED, ordinal = 4)
	private CqlVector<Float> centroid;
}
