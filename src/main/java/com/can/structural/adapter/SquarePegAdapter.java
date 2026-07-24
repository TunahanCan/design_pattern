package com.can.structural.adapter;

import java.util.Objects;

public class SquarePegAdapter implements RoundPegShape {

    private final SquarePeg squarePeg;

    public SquarePegAdapter(SquarePeg squarePeg) {
        this.squarePeg = Objects.requireNonNull(squarePeg, "squarePeg cannot be null");
    }

    @Override
    public double getRadius() {
        return squarePeg.getWidth() / Math.sqrt(2);
    }
}
