package com.datastax.oss.cass_stac.dao;

import com.datastax.oss.cass_stac.entity.Feature;
import com.datastax.oss.cass_stac.entity.FeaturePrimaryKey;

import java.time.Instant;
import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureDao extends  CassandraRepository<Feature, FeaturePrimaryKey>{

	@Query(value = "SELECT * FROM feature where partition_id = :partition_id AND item_id = :item_id AND label = :label AND datetime = :datetime")
	List<Feature> findFeatureByIdLabelAndDate(@Param("partition_id") final String partition_id, @Param("item_id") final String item_id, @Param("label") final String label, @Param("datetime") final Instant datetime);

	@Query(value = "SELECT * FROM feature WHERE partition_id = :partition_id AND item_id = :item_id")
	List<Feature> findFeatureById(@Param("partition_id") String partitionId, @Param("item_id") String itemId);

	@Query(value = "SELECT * FROM feature WHERE partition_id = :partition_id AND item_id = :item_id AND label = :label")
	List<Feature> findFeatureByIdAndLabel(@Param("partition_id") String partitionId, @Param("item_id") String itemId, @Param("label") String label);

	@Query(value = "SELECT * FROM feature WHERE partition_id = :partition_id AND item_id = :item_id")
	List<Feature> findFeatureByPartitionIdAndId(@Param("partition_id") String partitionId, @Param("item_id") String itemId);

	@Query(value = "SELECT * FROM feature WHERE partition_id = :partition_id")
	List<Feature> findFeatureByPartitionId(@Param("partition_id") String partitionId);

}
