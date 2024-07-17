package com.datastax.oss.cass_stac.dao;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.datastax.oss.cass_stac.entity.Item;
import com.datastax.oss.cass_stac.entity.ItemPrimaryKey;
@Repository
public interface ItemDao extends  CassandraRepository<Item, ItemPrimaryKey>{

	

}
