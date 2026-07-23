package com.can.structural.adapter;

import java.util.Objects;

public class RoundHole {

    private final double radius;

    public RoundHole(double radius) {
        requirePositiveFinite(radius, "radius");
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public boolean fits(RoundPeg peg) {
        Objects.requireNonNull(peg, "peg cannot be null");
        return this.radius >= peg.getRadius();
    }

    private static void requirePositiveFinite(double value, String fieldName) {
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive and finite");
        }
    }
}
