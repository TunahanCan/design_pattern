package com.can.structural.flyweight;

public final class TreeType {

    private final String name;
    private final String color;
    private final String texture;

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }

    public String draw(int x, int y) {
        return "%s (renk=%s, texture=%s) -> x=%d, y=%d".formatted(name, color, texture, x, y);
    }

    public String signature() {
        return "%s|%s|%s".formatted(name, color, texture);
    }
}
