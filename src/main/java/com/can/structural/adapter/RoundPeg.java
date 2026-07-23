package com.can.structural.adapter;

public class RoundPeg {

    private final double radius;

    public RoundPeg(double radius) {
        if (!Double.isFinite(radius) || radius < 0) {
            throw new IllegalArgumentException("radius must be non-negative and finite");
        }
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
}
