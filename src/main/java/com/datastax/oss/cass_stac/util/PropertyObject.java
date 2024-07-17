package com.datastax.oss.cass_stac.util;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyObject {

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonProperty("properties")
    protected JsonNode propertiesJson;
    @JsonIgnore
    protected Map<String, Object> properties;
    @JsonIgnore
    protected Set<String> propertiesDateFields = new HashSet<>();
    @JsonIgnore
    protected Map<String, JsonNode> additionalAttributes = new HashMap<>();

    public PropertyObject() {
    }

    public PropertyObject(String propertiesString, String additionalAttributes) throws JsonProcessingException {
        setPropertiesJsonString(propertiesString);
        setAdditionalAttributes(additionalAttributes);
    }

    public JsonNode getPropertiesJson() {
        return propertiesJson;
    }

    public void setPropertiesJson(JsonNode propertiesJson) {
        this.propertiesJson = propertiesJson;
        this.properties = null;
    }

    public void setPropertiesJsonString(String propertiesString) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(propertiesString);
        setPropertiesJson(jsonNode);
    }

    public Map<String, Object> getProperties() {
        if (properties == null && propertiesJson != null) {
            properties = deserializeProperties(propertiesJson, propertiesDateFields);
        }
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
        this.propertiesJson = objectMapper.valueToTree(properties);
    }

    public String getPropertiesAsString() throws JsonProcessingException {
        return objectMapper.writeValueAsString(propertiesJson);
    }

    public <T> T getProperty(String property) {
        return (T) getProperties().get(property);
    }

    public <T> T getProperty(String property, T defaultValue) {
        T propVal = (T) getProperties().get(property);
        return (null==propVal) ? defaultValue : propVal;
    }

    @JsonAnySetter
    public void setAdditionalAttributes(String key, JsonNode value) {
        additionalAttributes.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, JsonNode> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(String additionalAttributes) throws JsonProcessingException {
        this.additionalAttributes = objectMapper.readValue(additionalAttributes, new TypeReference<>() {
        });
    }

    public String getAdditionalAttributesAsString() throws JsonProcessingException {
        return objectMapper.writeValueAsString(additionalAttributes);
    }

    public <T> T getAttribute(String attribute) {
        return (T) convertNode(getAdditionalAttributes().get(attribute));
    }

    protected Map<String, Object> deserializeProperties(JsonNode propertiesJson, Set<String> dateFields) {
        Map<String, Object> properties = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = propertiesJson.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();
            if (dateFields.contains(fieldName) && !fieldValue.isNull()) {
                properties.put(fieldName, OffsetDateTime.parse(fieldValue.asText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            } else {
                properties.put(fieldName, convertNode(fieldValue));
            }
        }
        return properties;
    }

    protected Object convertNode(JsonNode node) {
        if (null == node) return null;
        if (node.isNumber()) {
            return node.numberValue();
        } else if (node.isBoolean()) {
            return node.booleanValue();
        } else if (node.isTextual()) {
            return node.textValue();
        } else {
            return node; // Non-basic types or fallback
        }
    }

    protected ObjectNode toObjectNode() {
        ObjectNode node = objectMapper.createObjectNode();
        node.set("properties", this.propertiesJson);
        additionalAttributes.forEach(node::set);
        return node;
    }

    public JsonNode toJson() {
        return toObjectNode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyObject that = (PropertyObject) o;
        return Objects.equals(propertiesJson, that.propertiesJson) &&
                Objects.equals(additionalAttributes, that.additionalAttributes);
    }
}
