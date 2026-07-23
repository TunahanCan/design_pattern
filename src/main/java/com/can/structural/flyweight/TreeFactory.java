package com.can.structural.flyweight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TreeFactory {

    private final Map<TreeTypeKey, TreeType> treeTypes = new ConcurrentHashMap<>();

    public TreeType getTreeType(String name, String color, String texture) {
        TreeTypeKey key = new TreeTypeKey(
            requireText(name, "name"),
            requireText(color, "color"),
            requireText(texture, "texture")
        );
        return treeTypes.computeIfAbsent(
            key,
            ignored -> new TreeType(key.name(), key.color(), key.texture())
        );
    }

    public int getTreeTypeCount() {
        return treeTypes.size();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private record TreeTypeKey(String name, String color, String texture) {
    }
}
