package com.datastax.oss.cass_stac.util;

import com.datastax.oss.cass_stac.entity.Item;
import com.datastax.oss.cass_stac.entity.SortBy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortUtils {

    public static List<Item> sortItems(List<Item> items, List<SortBy> sortBy) {
        Comparator<Item> comparator = Comparator.comparing(item -> 0); // Default comparator

        for (SortBy criteria : sortBy) {
            Comparator<Item> currentComparator = getComparatorForField(criteria.getField());

            if ("desc".equalsIgnoreCase(criteria.getDirection())) {
                currentComparator = currentComparator.reversed();
            }

            comparator = comparator.thenComparing(currentComparator);
        }

        return items.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    private static Comparator<Item> getComparatorForField(String field) {
        if (field.contains(".")) {
            String[] fields = field.split("\\.");
            return switch (fields[0]) {
                case  "properties" ->  Comparator.comparing(Item::getProperties);
                case "additional_attributes" -> Comparator.comparing(Item::getAdditional_attributes);
                default -> throw new IllegalArgumentException("Invalid sort field: " + field);
            };
        }else{
            return switch (field) {
                // TODO compare Ids
                case "collection" -> Comparator.comparing(Item::getCollection);
                case "datetime" -> Comparator.comparing(Item::getDatetime);
                case "properties" -> Comparator.comparing(Item::getProperties);
                case "additional_attributes" -> Comparator.comparing(Item::getAdditional_attributes);
                default -> throw new IllegalArgumentException("Invalid sort field: " + field);
            };
        }
    }

    public List<SortBy> parseSortBy(String sortBy) {
        String[] sortParams = sortBy.split(",");
        List<SortBy> sort = new ArrayList<>(List.of());

        for (String param : sortParams) {
            String direction = "asc";
            String field = param;

            if (param.startsWith("-")) {
                direction = "desc";
                field = param.substring(1);
            } else if (param.startsWith("+")) {
                field = param.substring(1);
            }

            sort.add(new SortBy(direction, field));
        }

        return sort;
    }

}
