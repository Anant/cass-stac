package com.datastax.oss.cass_stac.dao;

import com.datastax.oss.cass_stac.entity.Item;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.datastax.oss.cass_stac.entity.ItemId;

import java.util.List;

@Repository
public interface ItemIdDao extends CassandraRepository<ItemId, String> {
    @Query(value = "SELECT * FROM item_ids where id in :id")
    List<ItemId> findItemIdsByIds(@Param("id") final List<String> id);
}
