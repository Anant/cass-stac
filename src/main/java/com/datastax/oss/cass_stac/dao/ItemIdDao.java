package com.datastax.oss.cass_stac.dao;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.datastax.oss.cass_stac.entity.ItemId;

@Repository
public interface ItemIdDao extends CassandraRepository<ItemId, String>{

}
