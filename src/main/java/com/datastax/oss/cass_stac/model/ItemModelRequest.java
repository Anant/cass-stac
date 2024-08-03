package com.datastax.oss.cass_stac.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.*;

@Setter
@Getter
public class ItemModelRequest extends GeoJsonItemRequest {

    private static final Logger logger = LoggerFactory.getLogger(ItemModelRequest.class);

    @JsonProperty("collection")
    private String collection;

    @JsonProperty("properties")
    private Map<String, Object> properties = new HashMap<>();

    @JsonProperty("datetime")
    private String datetime;

    @JsonProperty("content")
    private Map<String, Object> content;

    public ItemModelRequest() {
        super();
        constructorInit();
    }

    public ItemModelRequest(String id, String collection, Geometry geometry, String propertiesString, String additionalAttributes) throws JsonProcessingException {
        super(geometry, propertiesString, additionalAttributes);
        constructorInit();
        setId(id);
        setCollection(collection);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    private void constructorInit() {
        this.propertiesDateFields = new HashSet<>();
        propertiesDateFields.add("datetime");
        propertiesDateFields.add("start_datetime");
        propertiesDateFields.add("end_datetime");
        propertiesDateFields.add("created");
        propertiesDateFields.add("updated");
    }

    @Override
    protected ObjectNode toObjectNode() {
        ObjectNode node = super.toObjectNode();
        node.put("collection", this.collection);
        node.set("properties", objectMapper.valueToTree(this.properties));
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        ItemModelRequest item = (ItemModelRequest) o;
        return Objects.equals(collection, item.collection) &&
                Objects.equals(properties, item.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), collection, properties);
    }

    @Override
    public String toString() {
        return "ItemModelRequest{" +
                "collection='" + collection + '\'' +
                ", properties=" + properties +
                ", id='" + getId() + '\'' +
                ", geometry=" + getGeometry() +
                ", additionalAttributes=" + getAdditionalAttributes() +
                '}';
    }

    @Getter
    @Setter
    public static class Properties {
        // getters and setters
        @JsonProperty("gsd")
        private Double gsd;
        @JsonProperty("datetime")
        private OffsetDateTime datetime;
        @JsonProperty("platform")
        private String platform;
        @JsonProperty("grid:code")
        private String gridCode;
        @JsonProperty("proj:epsg")
        private Integer projEpsg;
        @JsonProperty("proj:shape")
        private int[] projShape;
        @JsonProperty("eo:cloud_cover")
        private Double eoCloudCover;
        @JsonProperty("proj:transform")
        private double[] projTransform;
        @JsonProperty("other")
        private Map<String, Object> other = new HashMap<>();

        public Object get(String key) {
            return other.get(key);
        }

        @Override
        public String toString() {
            return "Properties{" +
                    "gsd=" + gsd +
                    ", datetime=" + datetime +
                    ", platform='" + platform + '\'' +
                    ", gridCode='" + gridCode + '\'' +
                    ", projEpsg=" + projEpsg +
                    ", projShape=" + Arrays.toString(projShape) +
                    ", eoCloudCover=" + eoCloudCover +
                    ", projTransform=" + Arrays.toString(projTransform) +
                    '}';
        }
    }
}
