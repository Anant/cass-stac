package com.datastax.oss.cass_stac.util;

import java.util.Map;


public class QueryEvaluator {

    public boolean evaluate(Map<String, Map<String, String>> query, Map<String, Object> data) {
        return query.entrySet().stream()
                .allMatch(entry -> {
                    String fieldName = entry.getKey();
                    Map<String, String> property = entry.getValue();
                    String operatorName = property.keySet().stream().toList().get(0);
                    String expectedValue = property.values().stream().toList().get(0);
                    Object actualValue = data.getOrDefault(fieldName, null);

                    Operator operator = Operator.valueOf(operatorName.toUpperCase());
                    return actualValue != null && operator.apply(actualValue.toString(), expectedValue);
                });
    }
}
