package com.datastax.oss.cass_stac.service;



import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.datastax.oss.cass_stac.dao.GeoTimePartition;
import com.datastax.oss.cass_stac.dao.ItemDao;
import com.datastax.oss.cass_stac.dto.CoOrdinateDto;
import com.datastax.oss.cass_stac.dto.GeometryDto;
import com.datastax.oss.cass_stac.dto.ItemDto;
import com.datastax.oss.cass_stac.entity.Item;
import com.datastax.oss.cass_stac.entity.ItemPrimaryKey;
import com.datastax.oss.cass_stac.util.GeometryUtil;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemDao itemDao;
	
	public void add(ItemDto dto) {
		final Item item = convertItemToDao(dto);
		itemDao.save(item);	
		
	}
	
	private Item convertItemToDao(ItemDto dto)  {
		final Item item = new Item();
		
        final int geoResolution = 6;
        final GeoTimePartition.TimeResolution timeResolution = GeoTimePartition.TimeResolution.valueOf("MONTH");
        final GeoTimePartition partitioner = new GeoTimePartition(geoResolution, timeResolution);
     
        final Map<String, Object> properties = dto.getProperties();
        final Object dateTime = properties.containsKey("datetime") ? properties.get("datetime") : properties.get("start_datetime");
        final OffsetDateTime datetime = (OffsetDateTime) dateTime;
        
        final GeometryDto geometryDto = dto.getGeometry();
        ObjectMapper mapper = new ObjectMapper();
        final JsonNode jsonNode = mapper.convertValue(geometryDto, JsonNode.class);
        final Geometry geometry;
		try {
			geometry = jsonNode != null ? mapper.treeToValue(jsonNode, Geometry.class) : null;
		} catch (JsonProcessingException | IllegalArgumentException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}
		
		final List<Float[][]> coordinates = geometryDto.getCoordinates();
        
        final Point centroid = geometry.getCentroid();
        
        final String partitionId = partitioner.getGeoTimePartitionForPoint(centroid, datetime);
        final String id = dto.getId();
        
        final ItemPrimaryKey pk = new ItemPrimaryKey();
        pk.setId(id);
        pk.setPartition_id(partitionId);
    
        item.setId(pk);
        
        CqlVector<Float> centroidVector = CqlVector.newInstance(Arrays.asList((float) centroid.getY(), (float) centroid.getX()));
        item.setCentroid(centroidVector);
        
        item.setGeometry(GeometryUtil.toByteBuffer(geometry));
        
        item.setCollection(dto.getCollection());
        item.setAdditional_attributes(dto.getAdditional_attributes());
        item.setProperties(properties.toString());
        
        
		
		return item;
	}

}
