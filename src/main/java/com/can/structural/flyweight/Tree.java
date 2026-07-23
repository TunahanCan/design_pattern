package com.can.structural.flyweight;

import java.util.Objects;

public class Tree {

    private final int x;
    private final int y;
    private final TreeType type;

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    public String draw() {
        return type.draw(x, y);
    }

    public TreeType getType() {
        return type;
    }
}
