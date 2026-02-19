package com.can.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

public class TreeFactory {

    private final Map<String, TreeType> treeTypes = new HashMap<>();

    public TreeType getTreeType(String name, String color, String texture) {
        String key = "%s|%s|%s".formatted(name, color, texture);
        return treeTypes.computeIfAbsent(key, ignored -> new TreeType(name, color, texture));
    }

    public int getTreeTypeCount() {
        return treeTypes.size();
    }
}
