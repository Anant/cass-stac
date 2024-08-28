package com.datastax.oss.cass_stac.dao;

import com.datastax.oss.cass_stac.entity.Item;
import com.datastax.oss.cass_stac.entity.ItemPrimaryKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDao extends CassandraRepository<Item, ItemPrimaryKey> {
    @NotNull
    Slice<Item> findAll(@NotNull Pageable pageable);

    @Query(value = "SELECT * FROM item where partition_id = :partition_id AND id = :id")
    List<Item> findItemByPartitionIdAndId(@Param("partition_id") final String partition_id, @Param("id") final String id);

    @Query(value = "SELECT * FROM item where id in :id ALLOW FILTERING")
    List<Item> findItemByIds(@Param("id") final List<String> id);

    @Query(value = "SELECT * FROM item WHERE partition_id = :partition_id ")
    List<Item> findItemByPartitionId(@Param("partition_id") String partitionId);

}
