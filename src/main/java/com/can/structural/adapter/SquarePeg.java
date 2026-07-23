package com.can.structural.adapter;

public class SquarePeg {

    private final double width;

    public SquarePeg(double width) {
        if (!Double.isFinite(width) || width <= 0) {
            throw new IllegalArgumentException("width must be positive and finite");
        }
        this.width = width;
    }

    public double getWidth() {
        return width;
    }
}
