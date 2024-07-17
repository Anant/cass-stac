package com.datastax.oss.cass_stac.dao;

import com.datastax.oss.cass_stac.entity.FeatureCollection;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureCollectionDao extends CassandraRepository<FeatureCollection, String>{

}
