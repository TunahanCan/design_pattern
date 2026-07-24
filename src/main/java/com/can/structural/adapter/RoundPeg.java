package com.can.structural.adapter;

public class RoundPeg implements RoundPegShape {

    private final double radius;

    public RoundPeg(double radius) {
        if (!Double.isFinite(radius) || radius <= 0) {
            throw new IllegalArgumentException("radius must be positive and finite");
        }
        this.radius = radius;
    }

    @Override
    public double getRadius() {
        return radius;
    }
}
