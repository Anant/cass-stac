package com.datastax.oss.cass_stac.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Geometry;

import java.util.HashSet;
import java.util.Objects;

public class FeatureModelRequest extends GeoJsonFeatureRequest {

    @JsonProperty("label")
    private String label;

    public FeatureModelRequest() {
        super();
        constructorInit();
    }

    public FeatureModelRequest(String item_id, String label, Geometry geometry, String propertiesString, String additionalAttributes) throws JsonProcessingException {
        super(geometry, propertiesString, additionalAttributes);
        constructorInit();
        setItem_id(item_id);
        setLabel(label);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private void constructorInit() {
        this.propertiesDateFields = new HashSet<>();
        propertiesDateFields.add("datetime");
        propertiesDateFields.add("start_datetime");
        propertiesDateFields.add("end_datetimecreated");
        propertiesDateFields.add("updated");
    }

    protected ObjectNode toObjectNode() {
        ObjectNode node = super.toObjectNode();
        node.put("label", this.label);
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        FeatureModelRequest feature = (FeatureModelRequest) o;
        return Objects.equals(label, feature.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), label);
    }

}