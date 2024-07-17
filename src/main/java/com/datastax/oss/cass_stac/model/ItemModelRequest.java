package com.datastax.oss.cass_stac.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
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

    public static class Properties {
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

        // getters and setters
        public Double getGsd() {
            return gsd;
        }

        public void setGsd(Double gsd) {
            this.gsd = gsd;
        }

        public OffsetDateTime getDatetime() {
            return datetime;
        }

        public void setDatetime(OffsetDateTime datetime) {
            this.datetime = datetime;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getGridCode() {
            return gridCode;
        }

        public void setGridCode(String gridCode) {
            this.gridCode = gridCode;
        }

        public Integer getProjEpsg() {
            return projEpsg;
        }

        public void setProjEpsg(Integer projEpsg) {
            this.projEpsg = projEpsg;
        }

        public int[] getProjShape() {
            return projShape;
        }

        public void setProjShape(int[] projShape) {
            this.projShape = projShape;
        }

        public Double getEoCloudCover() {
            return eoCloudCover;
        }

        public void setEoCloudCover(Double eoCloudCover) {
            this.eoCloudCover = eoCloudCover;
        }

        public double[] getProjTransform() {
            return projTransform;
        }

        public void setProjTransform(double[] projTransform) {
            this.projTransform = projTransform;
        }

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
