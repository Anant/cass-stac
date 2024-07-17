package com.datastax.oss.cass_stac.service;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.datastax.oss.cass_stac.dao.FeatureCollectionDao;
import com.datastax.oss.cass_stac.entity.*;
import com.datastax.oss.cass_stac.model.FeatureModelRequest;
import com.datastax.oss.cass_stac.model.FeatureModelResponse;

import com.datastax.oss.cass_stac.util.PropertyUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.datastax.oss.cass_stac.dao.FeatureDao;
import com.datastax.oss.cass_stac.dao.GeoTimePartition;
import com.datastax.oss.cass_stac.dto.itemfeature.FeatureDto;
import com.datastax.oss.cass_stac.util.GeometryUtil;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeatureService {
        private final FeatureDao featureDao;
        private final FeatureCollectionDao featureCollectionDao;

        private static final Map<String, String> propertyIndexMap = PropertyUtil.getPropertyMap("dao.feature.property.IndexList");

        public List<FeatureModelResponse> getFeatureByItemId(final String itemId, String label, String dateTime) {
                final FeatureCollection featureCollection = featureCollectionDao.findById(itemId)
                        .orElseThrow(() -> new RuntimeException(itemId + " is not found"));
                final String partitionId = featureCollection.getPartition_id();
                final FeaturePrimaryKey pk = new FeaturePrimaryKey();
                pk.setPartition_id(partitionId);
                pk.setItem_id(itemId);
                List<Feature> feature ;
                if ((label == null || label.isEmpty()) && (dateTime == null || dateTime.isEmpty())) {
                        feature = featureDao.findFeatureByPartitionIdAndId(partitionId, itemId);
                }
                else if (dateTime == null || dateTime.isEmpty()) {
                        feature = featureDao.findFeatureByIdAndLabel(partitionId, itemId, label);
                }
                else {
                        final OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTime);
                        final Instant instantDateTime = offsetDateTime.toInstant();
                        feature = featureDao.findFeatureByIdLabelAndDate(partitionId, itemId, label, instantDateTime);
                }

            return feature.stream().map(this::convertToResponse).collect(Collectors.toList());
        }

        private FeatureModelResponse convertToResponse(Feature feature) {
                final String item_id = feature.getId().getItem_id();
                final String label = feature.getId().getLabel();
                final String propeties_String = feature.getProperties();
                final String additional_attributes = feature.getAdditional_attributes();
                final ByteBuffer geometryByteBuffer = feature.getGeometry();
                final Geometry geometry = GeometryUtil.fromGeometryByteBuffer(geometryByteBuffer);

            FeatureModelResponse response = null;
            try {
                response = new FeatureModelResponse(item_id, label, geometry.toString(), propeties_String, additional_attributes);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
                return response;
                }

        public void save(final String json) {

                final Feature feature = converFeatureJsonToFeature(json);
                final Feature ft = featureDao.save(feature);
                final FeatureCollection featureCollection = createFeatureCollection(ft);
                featureCollectionDao.save(featureCollection);
        }

        private Feature converFeatureJsonToFeature(final String json) {
                final ObjectMapper objectMapper = new ObjectMapper();
                try {
                        final int geoResolution = 6;
                        final GeoTimePartition.TimeResolution timeResolution = GeoTimePartition.TimeResolution.valueOf("MONTH");
                        final GeoTimePartition partitioner = new GeoTimePartition(geoResolution, timeResolution);

                        final FeatureModelRequest featureModel = objectMapper.readValue(json, FeatureModelRequest.class);
                        final PropertyUtil propertyUtil = new PropertyUtil(propertyIndexMap, featureModel);
                        Point centroid = featureModel.getGeometry().getCentroid();
                        CqlVector<Float> centroidVector = CqlVector.newInstance(Arrays.asList((float) centroid.getY(), (float) centroid.getX()));

                        OffsetDateTime datetime = (OffsetDateTime) (featureModel.getProperties().containsKey("datetime") ? featureModel.getProperties().get("datetime") : featureModel.getProperties().get("start_datetime"));
                        String item_id = featureModel.getItem_id();

                        String partitionId;
//                        partitionId = partitioner.getGeoTimePartitionForPoint(centroid, datetime);
//                        final FeatureCollection featureCollection = featureCollectionDao.findById(item_id)
//                                .orElseThrow(() -> new RuntimeException(item_id + " is not found"));

                        try {
                                final FeatureCollection featureCollection = featureCollectionDao.findById(item_id)
                                        .orElseThrow(() -> new RuntimeException(item_id + " is not found"));
                                        partitionId = featureCollection.getPartition_id();
                        } catch (RuntimeException e){
                                partitionId = partitioner.getGeoTimePartitionForPoint(centroid, datetime);
                        }
                        final FeaturePrimaryKey pk = new FeaturePrimaryKey();
                        pk.setItem_id(item_id);
                        pk.setPartition_id(partitionId);
                        pk.setLabel(featureModel.getLabel());
                        pk.setDatetime(datetime.toInstant());
                        pk.setCentroid(centroidVector);

                        final Feature feature = new Feature();

                        feature.setId(pk);
                        feature.setGeometry(GeometryUtil.toByteBuffer(featureModel.getGeometry()));
                        feature.setProperties(featureModel.getPropertiesAsString());
                        feature.setAdditional_attributes(featureModel.getAdditionalAttributesAsString());
                        feature.setIndexed_properties_boolean(propertyUtil.getIndexedBooleanProps());
                        feature.setIndexed_properties_double(propertyUtil.getIndexedNumberProps());
                        feature.setIndexed_properties_text(propertyUtil.getIndexedTextProps());
                        feature.setIndexed_properties_timestamp(propertyUtil.getIndexedTimestampProps());
                        return feature;
                } catch (JsonProcessingException e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                }
        }

        private FeatureCollection createFeatureCollection(final Feature ft) {
                final String item_id = ft.getId().getItem_id();
                final String partition_id = ft.getId().getPartition_id();
                final String properties = ft.getProperties();
                final String additional_attributes = ft.getAdditional_attributes();
                final FeatureCollection featureCollection = new FeatureCollection();
                featureCollection.setItem_id(item_id);
                featureCollection.setPartition_id(partition_id);
                featureCollection.setProperties(properties);
                featureCollection.setAdditional_attributes(additional_attributes);
                return featureCollection;

        }

        private FeatureDto convertFeatureToDto(final Feature feature) {
                return FeatureDto.builder()
                        .id(feature.getId().getItem_id())
                        .partition_id(feature.getId().getPartition_id())
                        .label(feature.getId().getLabel())
                        .additional_attributes(feature.getAdditional_attributes())
                        .build();
        }
}