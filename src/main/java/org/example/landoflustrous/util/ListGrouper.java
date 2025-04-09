package org.example.landoflustrous.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ListGrouper {

    public static <T, K extends Comparable<K>> List<List<T>> groupAndFilter(List<T> list, Function<T, K> classifier) {
        Map<K, List<T>> map = new HashMap<>();
        for (T element : list) {
            K key = classifier.apply(element);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(element);
        }

        List<List<T>> result = new ArrayList<>();
        for (List<T> groupedElements : map.values()) {
            if (groupedElements.size() > 1) {
                result.add(groupedElements);
            }
        }

        return result;
    }
}
