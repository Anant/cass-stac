package com.datastax.oss.cass_stac.service;

import com.datastax.oss.cass_stac.dao.GeoPartition;
import com.datastax.oss.cass_stac.dao.GeoTimePartition;
import com.datastax.oss.cass_stac.dao.ItemDao;
import com.datastax.oss.cass_stac.dao.ItemIdDao;
import com.datastax.oss.cass_stac.dto.itemfeature.ItemDto;
import com.datastax.oss.cass_stac.entity.Item;
import com.datastax.oss.cass_stac.entity.ItemId;
import com.datastax.oss.cass_stac.entity.ItemPrimaryKey;
import com.datastax.oss.cass_stac.model.ImageResponse;
import com.datastax.oss.cass_stac.model.ItemModelRequest;
import com.datastax.oss.cass_stac.model.ItemModelResponse;
import com.datastax.oss.cass_stac.util.GeoJsonParser;
import com.datastax.oss.cass_stac.util.GeometryUtil;
import com.datastax.oss.cass_stac.util.PropertyUtil;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemDao itemDao;
    private final ItemIdDao itemIdDao;
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private static final Map<String, String> propertyIndexMap = PropertyUtil.getPropertyMap("dao.item.property.IndexList");
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Set<String> datetimeFields = new HashSet<>(Arrays.asList("datetime", "start_datetime", "end_datetime"));

    public ItemModelResponse getItemById(final String id) {
        final ItemId itemId = itemIdDao.findById(id)
                .orElseThrow(() -> new RuntimeException(id + " is not found"));
        final String partitionId = itemId.getPartition_id();
        final ItemPrimaryKey pk = new ItemPrimaryKey();
        pk.setPartition_id(partitionId);
        pk.setId(id);
        final Item item = itemDao.findById(pk)
                .orElseThrow(() -> new RuntimeException("There are no item found for the " + id));
        final String collection = item.getCollection();
        final ByteBuffer geometryByteBuffer = item.getGeometry();
        final Geometry geometry = GeometryUtil.fromGeometryByteBuffer(geometryByteBuffer);

        final String propertiesString = item.getProperties();
        final String additionalAttributesString = item.getAdditional_attributes();

        try {
            return new ItemModelResponse(id, collection, geometry.toString(), propertiesString, additionalAttributesString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    public void save(ItemModelRequest itemModelRequest) {
        logger.debug("Saving itemModelRequest: " + itemModelRequest);
        if (itemModelRequest.getProperties() == null) {
            logger.error("ItemModelRequest properties are null!");
        } else {
            logger.debug("ItemModelRequest properties: " + itemModelRequest.getProperties());
        }
        final Item item = convertItemModelRequestToItem(itemModelRequest);
        final Item it = itemDao.save(item);
        final ItemId itemId = createItemId(it);
        itemIdDao.save(itemId);
    }

    public void saveGeoJson(String geoJson) {
        try {
            logger.debug("Saving GeoJSON.");
            ItemModelRequest itemModelRequest = GeoJsonParser.parseGeoJson(geoJson);
            logger.debug("GeoJSON parsed: " + itemModelRequest);
            save(itemModelRequest);
        } catch (IOException e) {
            logger.error("Failed to parse or save the GeoJSON item.", e);
            throw new RuntimeException("Failed to parse or save the GeoJSON item.", e);
        }
    }

    public void saveNewGeoJson(String geoJson) {
        try {
            logger.debug("Saving new GeoJSON.");
            ItemModelRequest itemModelRequest = parseNewGeoJson(geoJson);
            logger.debug("New GeoJSON parsed: " + itemModelRequest);
            save(itemModelRequest);
        } catch (IOException e) {
            logger.error("Failed to parse or save the new GeoJSON item.", e);
            throw new RuntimeException("Failed to parse or save the new GeoJSON item.", e);
        }
    }

    private Item convertItemModelRequestToItem(final ItemModelRequest itemModel) {
        final int geoResolution = 6;
        final GeoTimePartition.TimeResolution timeResolution = GeoTimePartition.TimeResolution.valueOf("MONTH");
        final GeoTimePartition partitioner = new GeoTimePartition(geoResolution, timeResolution);
        final PropertyUtil propertyUtil = new PropertyUtil(propertyIndexMap, itemModel);
        Point centroid = itemModel.getGeometry().getCentroid();
        CqlVector<Float> centroidVector = CqlVector.newInstance(Arrays.asList((float) centroid.getY(), (float) centroid.getX()));

        OffsetDateTime datetime = parseDatetime(itemModel);

        if (datetime == null) {
            logger.error("datetime field is missing or null in both root level and properties");
            throw new IllegalArgumentException("datetime field is required but is missing or null");
        }

        String partitionId = partitioner.getGeoTimePartitionForPoint(centroid, datetime);
        String id = itemModel.getId();
        final ItemPrimaryKey pk = new ItemPrimaryKey();

        pk.setId(id);
        pk.setPartition_id(partitionId);

        final Item item = new Item();
        item.setId(pk);
        item.setCollection(itemModel.getCollection());
        item.setGeometry(GeometryUtil.toByteBuffer(itemModel.getGeometry()));
        item.setDatetime(datetime.toInstant());
        item.setCentroid(centroidVector);
        try {
            item.setProperties(objectMapper.writeValueAsString(itemModel.getProperties()));
            item.setAdditional_attributes(objectMapper.writeValueAsString(itemModel.getContent()));
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert properties to string.", e);
            throw new RuntimeException("Failed to convert properties to string.", e);
        }
        item.setIndexed_properties_boolean(propertyUtil.getIndexedBooleanProps());
        item.setIndexed_properties_double(propertyUtil.getIndexedNumberProps());
        item.setIndexed_properties_text(propertyUtil.getIndexedTextProps());
        item.setIndexed_properties_timestamp(propertyUtil.getIndexedTimestampProps());
        return item;
    }

    private OffsetDateTime parseDatetime(ItemModelRequest itemModel) {
        // Check root level
        String datetimeString = itemModel.getDatetime();
        if (datetimeString != null) {
            try {
                return OffsetDateTime.parse(datetimeString);
            } catch (DateTimeParseException e) {
                logger.error("Invalid datetime format at root level: {}", datetimeString, e);
            }
        }

        // Check properties
        Map<String, Object> properties = itemModel.getProperties();
        if (properties != null) {
            for (String field : datetimeFields) {
                Object datetimeObject = properties.get(field);
                if (datetimeObject != null) {
                    if (datetimeObject instanceof String) {
                        try {
                            return OffsetDateTime.parse((String) datetimeObject);
                        } catch (DateTimeParseException e) {
                            logger.error("Invalid datetime format in properties for field {}: {}", field, datetimeObject, e);
                        }
                    } else if (datetimeObject instanceof OffsetDateTime) {
                        return (OffsetDateTime) datetimeObject;
                    }
                }
            }
        }

        return null;
    }

    public ItemDto getItem(final String id) {
        final ItemId itemId = getItemId(id);

        final ItemPrimaryKey itemPrimaryKey = new ItemPrimaryKey();
        itemPrimaryKey.setId(id);
        itemPrimaryKey.setPartition_id(itemId.getPartition_id());
        final Item item = itemDao.findById(itemPrimaryKey)
                .orElseThrow(() -> new RuntimeException("No data found"));
        final ItemDto itemDto = convertItemToDto(item);
        return itemDto;
    }

    public ItemId getItemId(final String id) {
        final ItemId itemId = itemIdDao.findById(id)
                .orElseThrow(() -> new RuntimeException("No data found for selected id"));
        return itemId;
    }

    private ItemId createItemId(final Item it) {
        final String id = it.getId().getId();
        final Instant datetime = it.getDatetime();
        final String partition_id = it.getId().getPartition_id();
        final ItemId itemId = new ItemId();
        itemId.setDatetime(datetime);
        itemId.setId(id);
        itemId.setPartition_id(partition_id);
        return itemId;
    }

    private ItemDto convertItemToDto(final Item item) {
        return ItemDto.builder()
                .id(item.getId().getId())
                .partition_id(item.getId().getPartition_id())
                .collection(item.getCollection())
                .additional_attributes(item.getAdditional_attributes())
                .build();
    }

    private ItemModelRequest parseNewGeoJson(String geoJson) throws JsonProcessingException {
        return objectMapper.readValue(geoJson, ItemModelRequest.class);
    }

    public ImageResponse getPartitions(
            ItemModelRequest request,
            Optional<OffsetDateTime> minDate,
            Optional<OffsetDateTime> maxDate,
            List<String> objectTypeFilter,
            String whereClause,
            Object bindVars,
            Boolean useCentroid,
            Boolean filterObjectsByPolygon,
            Boolean includeObjects) {
        if (maxDate.isEmpty() && minDate.isPresent()) maxDate = Optional.of(minDate.get().withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999_999_999));

        List<String> partitions = switch (request.getGeometry().getGeometryType()) {
            case "Point" ->
                    getPointPartitions(request, minDate, maxDate, objectTypeFilter, whereClause, bindVars, useCentroid, filterObjectsByPolygon);
            case "Polygon" ->
                    getPolygonPartitions(request, minDate, maxDate, objectTypeFilter, whereClause, bindVars, useCentroid, filterObjectsByPolygon);
            default -> throw new IllegalStateException("Unexpected value: " + request.getGeometry().getGeometryType());
        };
        Optional<List<Item>> items = includeObjects
                ? Optional.of(partitions.stream()
                .flatMap(partition -> itemDao.findItemByPartitionId(partition).stream())
                .toList())
                : Optional.empty();

        return new ImageResponse(partitions, partitions.size(), items);
    }

    private List<String> getPointPartitions(
            ItemModelRequest request,
            Optional<OffsetDateTime> minDate,
            Optional<OffsetDateTime> maxDate,
            List<String> objectTypeFilter,
            String whereClause,
            Object bindVars,
            Boolean useCentroid,
            Boolean filterObjectsByPolygon) {
        final int geoResolution = 6;
        final GeoTimePartition.TimeResolution timeResolution = GeoTimePartition.TimeResolution.valueOf("MONTH");

        Geometry geometry = request.getGeometry();
        Point point = geometry.getFactory().createPoint(geometry.getCoordinate());
        return Collections.singletonList(minDate.isPresent()
                ? new GeoTimePartition(geoResolution, timeResolution).getGeoTimePartitionForPoint(point, minDate.get())
                : new GeoPartition(geoResolution).getGeoPartitionForPoint(point));
    }

    private List<String> getPolygonPartitions(
            ItemModelRequest request,
            Optional<OffsetDateTime> minDate,
            Optional<OffsetDateTime> maxDate,
            List<String> objectTypeFilter,
            String whereClause,
            Object bindVars,
            Boolean useCentroid,
            Boolean filterObjectsByPolygon) {
        final int geoResolution = 6;
        final GeoTimePartition.TimeResolution timeResolution = GeoTimePartition.TimeResolution.valueOf("MONTH");

        Geometry geometry = request.getGeometry();
        Polygon polygon = geometry.getFactory().createPolygon(geometry.getCoordinates());
        return (maxDate.isPresent() && minDate.isPresent()) ? new GeoTimePartition(geoResolution, timeResolution)
                .getGeoTimePartitions(polygon, minDate.get(), maxDate.get()) : new GeoPartition(geoResolution).getGeoPartitions(polygon);

    }

}
