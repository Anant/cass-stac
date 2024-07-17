package com.datastax.oss.cass_stac.dao;

import com.datastax.oss.cass_stac.entity.Feature;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.datastax.oss.cass_stac.entity.Item;
import com.datastax.oss.cass_stac.entity.ItemPrimaryKey;

import java.time.Instant;
import java.util.List;

@Repository
public interface ItemDao extends  CassandraRepository<Item, ItemPrimaryKey>{
    @Query(value = "SELECT * FROM item where partition_id = :partition_id AND id = :id")
    List<Item> findItemByPartitionIdAndId(@Param("partition_id") final String partition_id, @Param("id") final String id);

    @Query(value = "SELECT * FROM item WHERE partition_id = :partition_id ")
    List<Item> findItemByPartitionId(@Param("partition_id") String partitionId);

}
