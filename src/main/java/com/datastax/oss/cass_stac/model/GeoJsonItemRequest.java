package com.datastax.oss.cass_stac.model;

import com.datastax.oss.cass_stac.config.ConfigManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.JtsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class GeoJsonItemRequest extends PropertyObject {
    private static final Logger logger = LoggerFactory.getLogger(GeoJsonItemRequest.class);

    @JsonProperty("type")
    private static final String TYPE = "Feature";

    static {
        objectMapper.registerModule(new JtsModule(ConfigManager.getInstance().getIntProperty("geojson.coordinateDecimalPlaces", 16)));
    }

    @JsonProperty("id")
    private String id;

    @JsonProperty("geometry")
    private Geometry geometry;

    public GeoJsonItemRequest() {
        super();
    }

    public GeoJsonItemRequest(Geometry geometry, String propertiesString, String additionalAttributes) throws JsonProcessingException {
        super(propertiesString, additionalAttributes);
        this.geometry = geometry;
    }

    public GeoJsonItemRequest(Geometry geometry, Map<String, Object> properties) {
        super();
        this.geometry = geometry;
        setProperties(properties);
    }

    public GeoJsonItemRequest(Geometry geometry) {
        this(geometry, null);
    }

    public String getType() {
        return TYPE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    protected void setGeometry(JsonNode geometryJson) throws JsonProcessingException {
        this.geometry = geometryJson == null ? null : objectMapper.treeToValue(geometryJson, Geometry.class);
    }

    @Override
    protected ObjectNode toObjectNode() {
        ObjectNode node = super.toObjectNode();
        node.put("id", id);
        node.set("geometry", objectMapper.valueToTree(geometry));
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        if (getClass() != o.getClass()) return false;
        GeoJsonItemRequest that = (GeoJsonItemRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(geometry, that.geometry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, geometry);
    }

    @Override
    public String toString() {
        return "GeoJsonItemRequest{" +
                "id='" + id + '\'' +
                ", geometry=" + geometry +
                ", properties=" + getProperties() +
                ", additionalAttributes=" + getAdditionalAttributes() +
                '}';
    }
}
