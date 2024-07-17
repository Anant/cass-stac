package com.datastax.oss.cass_stac.model;

import com.datastax.oss.cass_stac.config.ConfigManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.n52.jackson.datatype.jts.JtsModule;

import java.util.Map;
import java.util.Objects;

public class GeoJsonFeatureResponse extends PropertyObject {

    @JsonProperty("type")
    private static final String TYPE = "Feature";


    static {
        objectMapper.registerModule(new JtsModule(ConfigManager.getInstance().getIntProperty("geojson.coordinateDecimalPlaces", 16)));
    }

    @JsonProperty("item_id")
    private String item_id;

    @JsonProperty("geometry")
    private String geometry;

    public GeoJsonFeatureResponse() {
        super();
    }

    public GeoJsonFeatureResponse(String geometry, String propertiesString, String additionalAttributes) throws JsonProcessingException {
        super(propertiesString, additionalAttributes);
        this.geometry = geometry;
    }

    public GeoJsonFeatureResponse(String geometry, Map<String, Object> properties) {
        super();
        this.geometry = geometry;
        setProperties(properties);
    }

    public GeoJsonFeatureResponse(String geometry) {
        this(geometry, null);
    }

    public String getType() {
        return TYPE;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getGeometry() {
        return geometry;
    }

    @Override
    protected ObjectNode toObjectNode() {
        ObjectNode node = super.toObjectNode();
        node.put("item_id", item_id);
        node.set("geometry", objectMapper.valueToTree(geometry));
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        if (getClass() != o.getClass()) return false;
        GeoJsonFeatureResponse that = (GeoJsonFeatureResponse) o;
        return Objects.equals(item_id, that.item_id) && Objects.equals(geometry, that.geometry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), item_id, geometry);
    }
}