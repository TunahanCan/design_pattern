package com.can.structural.flyweight;

public final class TreeType {

    private final String name;
    private final String color;
    private final String texture;

    public TreeType(String name, String color, String texture) {
        this.name = requireText(name, "name");
        this.color = requireText(color, "color");
        this.texture = requireText(texture, "texture");
    }

    public String draw(int x, int y) {
        return "%s (renk=%s, texture=%s) -> x=%d, y=%d".formatted(name, color, texture, x, y);
    }

    public String signature() {
        return "%s|%s|%s".formatted(name, color, texture);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
