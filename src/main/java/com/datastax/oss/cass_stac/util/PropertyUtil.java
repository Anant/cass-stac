package com.datastax.oss.cass_stac.util;

import com.datastax.oss.cass_stac.config.ConfigManager;
import com.datastax.oss.cass_stac.model.PropertyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    private static final String MAP_TEXT_KEY = "indexed_text";
    private static final String MAP_NUMBER_KEY = "indexed_number";
    private static final String MAP_TIMESTAMP_KEY = "indexed_timestamp";
    private static final String MAP_BOOLEAN_KEY = "indexed_boolean";
    private final Map<String, String> indexedTextProps = new HashMap<>();
    private final Map<String, Number> indexedNumberProps = new HashMap<>();
    private final Map<String, OffsetDateTime> indexedTimestampProps = new HashMap<>();
    private final Map<String, Boolean> indexedBooleanProps = new HashMap<>();

    public PropertyUtil(Map<String, String> propertyIndexMap, PropertyObject propertyObject) {
        propertyObject.getProperties().forEach((key, value) -> {
            String indexColumn = propertyIndexMap.get(key);
            if (indexColumn != null) {
                try {
                    switch (indexColumn) {
                        case MAP_TEXT_KEY:
                            if (value instanceof String)
                                indexedTextProps.put(key, (String) value);
                            break;
                        case MAP_NUMBER_KEY:
                            if (value instanceof Number)
                                indexedNumberProps.put(key, (Number) value);
                            break;
                        case MAP_BOOLEAN_KEY:
                            if (value instanceof Boolean)
                                indexedBooleanProps.put(key, (Boolean) value);
                            break;
                        case MAP_TIMESTAMP_KEY:
                            if (value instanceof OffsetDateTime)
                                indexedTimestampProps.put(key, (OffsetDateTime) value);
                            break;
                    }
                } catch (ClassCastException e) {
                    logger.error("Type mismatch for property " + key + ", expected type based on configuration.");
                    throw e;
                }
            }
        });
    }

    public static Map<String, String> getPropertyMap(String configPrefix) {
        ConfigManager configManager = ConfigManager.getInstance();
        List<String> textIndices = configManager.getPropertyAsList(configPrefix + ".text");
        List<String> numberIndices = configManager.getPropertyAsList(configPrefix + ".number");
        List<String> booleanIndices = configManager.getPropertyAsList(configPrefix + ".boolean");
        List<String> timestampIndices = configManager.getPropertyAsList(configPrefix + ".timestamp");

        Map<String, String> propertyIndexMap = new HashMap<>();
        if (null != textIndices)
            textIndices.forEach(prop -> propertyIndexMap.put(prop, MAP_TEXT_KEY));
        if (null != numberIndices)
            numberIndices.forEach(prop -> propertyIndexMap.put(prop, MAP_NUMBER_KEY));
        if (null != booleanIndices)
            booleanIndices.forEach(prop -> propertyIndexMap.put(prop, MAP_BOOLEAN_KEY));
        if (null != timestampIndices)
            timestampIndices.forEach(prop -> propertyIndexMap.put(prop, MAP_TIMESTAMP_KEY));

        return propertyIndexMap;
    }

    public Map<String, String> getIndexedTextProps() {
        return this.indexedTextProps;
    }

    public Map<String, Number> getIndexedNumberProps() {
        return this.indexedNumberProps;
    }

    public Map<String, OffsetDateTime> getIndexedTimestampProps() {
        return this.indexedTimestampProps;
    }

    public Map<String, Boolean> getIndexedBooleanProps() {
        return this.indexedBooleanProps;
    }

}