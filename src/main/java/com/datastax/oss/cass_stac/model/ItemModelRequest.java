package com.datastax.oss.cass_stac.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;

public class ItemModelRequest extends GeoJsonItemRequest {

    private static final Logger logger = LoggerFactory.getLogger(ItemModelRequest.class);

    @JsonProperty("collection")
    private String collection;

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
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        ItemModelRequest item = (ItemModelRequest) o;
        return Objects.equals(collection, item.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), collection);
    }

    @Override
    public String toString() {
        return "ItemModelRequest{" +
                "collection='" + collection + '\'' +
                ", id='" + getId() + '\'' +
                ", geometry=" + getGeometry() +
                ", properties=" + getProperties() +
                ", additionalAttributes=" + getAdditionalAttributes() +
                '}';
    }
}
