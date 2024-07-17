package com.datastax.oss.cass_stac.util;

import com.datastax.oss.cass_stac.model.ItemModelRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoJsonParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(GeoJsonParser.class);

    public static ItemModelRequest parseGeoJson(String geoJson) throws IOException {
        logger.debug("Parsing GeoJSON.");
        JsonNode rootNode = objectMapper.readTree(geoJson);

        return objectMapper.treeToValue(rootNode, ItemModelRequest.class);
    }
}
