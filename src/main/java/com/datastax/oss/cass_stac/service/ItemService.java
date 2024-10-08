package com.datastax.oss.cass_stac.service;

import com.datastax.oss.cass_stac.dao.GeoPartition;
import com.datastax.oss.cass_stac.dao.GeoTimePartition;
import com.datastax.oss.cass_stac.dao.ItemDao;
import com.datastax.oss.cass_stac.dao.ItemIdDao;
import com.datastax.oss.cass_stac.dto.itemfeature.ItemDto;
import com.datastax.oss.cass_stac.entity.*;
import com.datastax.oss.cass_stac.model.AggregateRequest;
import com.datastax.oss.cass_stac.model.ImageResponse;
import com.datastax.oss.cass_stac.model.ItemModelRequest;
import com.datastax.oss.cass_stac.model.ItemModelResponse;
import com.datastax.oss.cass_stac.util.*;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.min;


@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemDao itemDao;
    private final ItemIdDao itemIdDao;
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private static final Map<String, String> propertyIndexMap = PropertyUtil.getPropertyMap("dao.item.property.IndexList");
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Set<String> datetimeFields = new HashSet<>(Arrays.asList("datetime", "start_datetime", "end_datetime", "created", "updated"));
    private final CassandraTemplate cassandraTemplate;

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

    @Async("asyncExecutor")
    public CompletableFuture<ItemModelResponse> getItemsByIdParallel(final String id) {
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
            return CompletableFuture.completedFuture(new ItemModelResponse(id, collection, geometry.toString(), propertiesString, additionalAttributesString));
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
        final GeoTimePartition partitioner = new GeoTimePartition();
        if (itemModel.getContent() != null && itemModel.getContent().getOrDefault("properties", null) != null)
            itemModel.setProperties((Map<String, Object>) itemModel.getContent().get("properties"));
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


    private static List<Float> convertJsonNodeToFloatArray(JsonNode jsonNode) {
        List<Float> floatArray = new ArrayList<>(List.of());
        for (JsonNode node : jsonNode) {
            if (node.isNumber()) {
                floatArray.add(node.floatValue());
            } else {
                throw new IllegalArgumentException("JsonNode contains non-numeric values");
            }
        }

        return floatArray;
    }

    private ItemDto convertItemToDto(final Item item) throws IOException {
        JsonNode bbox = objectMapper.readValue(item.getAdditional_attributes(), JsonNode.class).get("bbox");
        List<Float> floatArray = null;
        if (bbox != null && bbox.isArray()) {
            floatArray = convertJsonNodeToFloatArray(bbox);
        }
        return ItemDto.builder()
                .id(item.getId().getId())
                .partition_id(item.getId().getPartition_id())
                .collection(item.getCollection())
                .additional_attributes(item.getAdditional_attributes())
                .bbox(floatArray)
                .build();
    }

    private ItemModelRequest parseNewGeoJson(String geoJson) throws JsonProcessingException {
        return objectMapper.readValue(geoJson, ItemModelRequest.class);
    }


    public Map<String, String> parseDatetime(String datetime) {
        Map<String, String> result = new HashMap<>();

        if (datetime == null)
            throw new RuntimeException("datetime is required to filter out data");

        if (datetime.contains("/")) {
            String[] parts = datetime.split("/");
            Instant rangeCenter = Instant.parse("2023-01-01T00:00:00.00Z");

            result.put("minDate", parts[0].equals("..") ? rangeCenter.minusSeconds(15770000L).toString() : parts[0]);
            result.put("minOffsetDate", parts[0].equals("..") ? rangeCenter.minusSeconds(15770000L).toString() : parts[0]);
            result.put("maxDate", parts[1].equals("..") ? rangeCenter.plusSeconds(15770000L).toString() : parts[1]);
            result.put("maxOffsetDate", parts[1].equals("..") ? rangeCenter.plusSeconds(15770000L).toString() : parts[1]);
        } else {
            result.put("minDate", datetime);
            result.put("minOffsetDate", datetime);
            result.put("maxDate", datetime);
            result.put("maxOffsetDate", datetime);
        }
        return result;
    }

    public List<String> getPartitions(Geometry intersects, List<String> ids, Map<String, String> dateTimes) {
        final GeoTimePartition partitioner = new GeoTimePartition();
        List<String> partitions = new ArrayList<>();
        if (intersects == null && (ids == null || ids.isEmpty()))
            throw new RuntimeException("Filter on either ids or intersects should be passed to search");
        if (ids == null)
            partitions = switch (intersects.getGeometryType()) {
                case "Point":
                    yield partitioner.getGeoTimePartitionsForPoint(intersects.getCentroid(),
                            OffsetDateTime.parse(dateTimes.get("minOffsetDate")),
                            OffsetDateTime.parse(dateTimes.get("maxOffsetDate")));
                case "Polygon":
                    yield partitioner.getGeoTimePartitions(intersects.getFactory().createPolygon(intersects.getCoordinates()),
                            OffsetDateTime.parse(dateTimes.get("minOffsetDate")),
                            OffsetDateTime.parse(dateTimes.get("maxOffsetDate")));
                default:
                    throw new IllegalStateException("Unexpected value: " + intersects.getGeometryType());
            };
        else if (!ids.isEmpty()) {
            partitions = new ArrayList<>(ids.stream().map(id -> {
                final ItemId itemId = itemIdDao.findById(id).orElse(null);
                if (itemId != null) {
                    return itemId.getPartition_id();
                }
                return null;
            }).filter(Objects::nonNull).toList());
        }
        return partitions.stream().distinct().toList();
    }

    private List<Item> fetchItemsForPartition(String partitionId,
                                              List<Float> bbox,
                                              Instant minDate,
                                              Instant maxDate,
                                              List<String> collectionsArray) {
        List<Item> itemPage;

        Query dbQuery = Query.query(Criteria.where("partition_id").is(partitionId));

        if (collectionsArray != null && !collectionsArray.isEmpty()) {
            dbQuery = dbQuery.and(Criteria.where("collection").in(collectionsArray)).withAllowFiltering();
        }

        dbQuery = dbQuery.and(Criteria.where("datetime").lte(maxDate))
                .and(Criteria.where("datetime").gte(minDate));

        itemPage = cassandraTemplate.select(dbQuery, Item.class);


        return itemPage;
    }

    @Async("asyncExecutor")
    protected CompletableFuture<List<Item>> fetchItemsForPartitionAsync(String partitionId,
                                                                        List<Float> bbox,
                                                                        Instant minDate,
                                                                        Instant maxDate,
                                                                        List<String> collectionsArray) {
        List<Item> itemPage;

        Query dbQuery = Query.query(Criteria.where("partition_id").is(partitionId));

        if (collectionsArray != null && !collectionsArray.isEmpty()) {
            dbQuery = dbQuery.and(Criteria.where("collection").in(collectionsArray)).withAllowFiltering();
        }

        dbQuery = dbQuery.and(Criteria.where("datetime").lte(maxDate))
                .and(Criteria.where("datetime").gte(minDate));

        itemPage = cassandraTemplate.select(dbQuery, Item.class);

        return CompletableFuture.completedFuture(itemPage);
    }

    /**
     * search within all items, items that intersect with bbox or a geometry using intersects
     * date might be used as well as a filter
     * might be fetched using ids only
     * if collection ids is not null, search items in these collections
     *
     * @param bbox
     * @param intersects
     * @param datetime
     * @param limit
     * @param ids
     * @param collectionsArray
     * @param includeCount
     * @param includeIds
     * @param includeObjects
     * @return
     */
    public ItemCollection search(List<Float> bbox,
                                 Geometry intersects,
                                 String datetime,
                                 Integer limit,
                                 List<String> ids,
                                 List<String> collectionsArray,
                                 Map<String, Map<String, Object>> query,
                                 List<SortBy> sort,
                                 Boolean includeCount,
                                 Boolean includeIds,
                                 Boolean includeObjects) throws ExecutionException, InterruptedException {

        limit = limit == null ? 10 : limit;

        Map<String, String> dateTimes = parseDatetime(datetime);
        List<String> partitionIds = getPartitions(intersects, ids, dateTimes);

        List<CompletableFuture<List<Item>>> futures = new ArrayList<>();

        for (String partitionId : partitionIds) {
            logger.info("Fetching items for partitionId: {}", partitionId);
            CompletableFuture<List<Item>> future = fetchItemsForPartitionAsync(partitionId, bbox,
                    Instant.parse(dateTimes.get("minDate")), Instant.parse(dateTimes.get("maxDate")),
                    collectionsArray);
            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<List<Item>> allItemsFuture = allFutures.thenApply(v ->
                futures.stream()
                        .flatMap(future -> future.join().stream())
                        .filter(_item -> {
                            boolean valid = true;
                            if (ids != null) {
                                valid = ids.contains(_item.getId().getId());
                            }

                            if (bbox != null) {
                                ItemDto itemDto;
                                try {
                                    itemDto = convertItemToDto(_item);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                valid = BboxIntersects(itemDto.getBbox(), bbox);
                            }

                            if (query != null) {
                                QueryEvaluator evaluator = new QueryEvaluator();
                                Map<String, Object> additionalAttributes;
                                JsonNode attributes;
                                try {
                                    attributes = objectMapper.readValue(_item.getProperties(), JsonNode.class);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                                additionalAttributes = objectMapper.convertValue(attributes, new TypeReference<>() {
                                });
                                valid = evaluator.evaluate(query, additionalAttributes);
                            }
                            return valid;
                        })
                        .collect(Collectors.toList())
        );

        List<Item> allItems = allItemsFuture.get();

        if (sort != null && !sort.isEmpty()) {
            allItems = SortUtils.sortItems(allItems, sort);
        }

        int numberMatched = allItems.size();
        int numberReturned = min(limit, numberMatched);
        allItems = allItems.subList(0, numberReturned);
        Optional<List<Item>> items = includeObjects ? Optional.of(allItems) : Optional.empty();
        Optional<List<String>> returnPartitions = includeIds ? Optional.of(allItems.stream().map(item -> item.getId().getPartition_id()).toList()) : Optional.empty();
        Optional<Integer> matchedCount = includeCount ? Optional.of(numberMatched) : Optional.empty();
        Optional<Integer> returnedCount = includeCount ? Optional.of(numberReturned) : Optional.empty();

        return new ItemCollection(items, returnPartitions, matchedCount, returnedCount);
    }

    private Boolean BboxIntersects(List<Float> current, List<Float> other) {
        if (other.size() != 4) {
            throw new IllegalArgumentException("The bbox parameter must contain 4 values.");
        }

        float minLng = other.get(0);
        float minLat = other.get(1);
        float maxLng = other.get(2);
        float maxLat = other.get(3);

        return (current.get(0) < maxLng) && (current.get(2) > minLng) &&
                (current.get(1) < maxLat) && (current.get(3) > minLat);
    }

    public ImageResponse getPartitions(
            ItemModelRequest request,
            OffsetDateTime minDate,
            OffsetDateTime maxDate,
            List<String> objectTypeFilter,
            String whereClause,
            Object bindVars,
            Boolean useCentroid,
            Boolean includeCount,
            Boolean includeIds,
            Boolean filterObjectsByPolygon,
            Boolean includeObjects) {

        if (maxDate == null && minDate != null) maxDate = minDate.withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999_999_999);

        List<String> partitions = switch (request.getGeometry().getGeometryType()) {
            case "Point" ->
                    getPointPartitions(request.getGeometry(), minDate, maxDate, objectTypeFilter, whereClause, bindVars, useCentroid, filterObjectsByPolygon);
            case "Polygon" ->
                    getPolygonPartitions(request.getGeometry(), minDate, maxDate, objectTypeFilter, whereClause, bindVars, useCentroid, filterObjectsByPolygon);
            default -> throw new IllegalStateException("Unexpected value: " + request.getGeometry().getGeometryType());
        };
        Optional<List<Item>> items = includeObjects
                ? Optional.of(partitions.stream()
                .flatMap(partition -> itemDao.findItemByPartitionId(partition).stream())
                .toList())
                : Optional.empty();

        Optional<Integer> count = includeCount ? Optional.of(partitions.size()) : Optional.empty();
        Optional<List<String>> returnPartitions = includeIds ? Optional.of(partitions) : Optional.empty();
        return new ImageResponse(returnPartitions, count, items);
    }

    private List<String> getPointPartitions(
            Geometry geometry,
            OffsetDateTime minDate,
            OffsetDateTime maxDate,
            List<String> objectTypeFilter,
            String whereClause,
            Object bindVars,
            Boolean useCentroid,
            Boolean filterObjectsByPolygon) {
        final int geoResolution = 6;
        final GeoTimePartition.TimeResolution timeResolution = GeoTimePartition.TimeResolution.valueOf("MONTH");

        Point point = geometry.getFactory().createPoint(geometry.getCoordinate());
        return Collections.singletonList(minDate != null
                ? new GeoTimePartition(geoResolution, timeResolution).getGeoTimePartitionForPoint(point, minDate)
                : new GeoPartition(geoResolution).getGeoPartitionForPoint(point));
    }

    private List<String> getPolygonPartitions(
            Geometry geometry,
            OffsetDateTime minDate,
            OffsetDateTime maxDate,
            List<String> objectTypeFilter,
            String whereClause,
            Object bindVars,
            Boolean useCentroid,
            Boolean filterObjectsByPolygon) {

        Polygon polygon = geometry.getFactory().createPolygon(geometry.getCoordinates());
        return (maxDate != null && minDate != null) ? new GeoTimePartition()
                .getGeoTimePartitions(polygon, minDate, maxDate) : new GeoPartition().getGeoPartitions(polygon);
    }

    public AggregationCollection agg(
            List<Float> bbox,
            Geometry intersects,
            String datetime,
            List<String> ids,
            List<String> collections,
            Map<String, Map<String, Object>> query,
            List<String> aggregations,
            List<AggregateRequest.Range> ranges
    ) throws ExecutionException, InterruptedException {
        ItemCollection itemCollection = search(bbox, intersects, datetime, MAX_VALUE, ids, collections, query, null, false, false, true);

        List<Aggregation> aggegationList = aggregations.stream().map(aggregationName -> {
            AggregationUtil aggregation = AggregationUtil.valueOf(aggregationName.toUpperCase());
            return aggregation.apply(itemCollection.getFeatures().orElse(null), ranges);
        }).toList();
        return new AggregationCollection(aggegationList);
    }
}
