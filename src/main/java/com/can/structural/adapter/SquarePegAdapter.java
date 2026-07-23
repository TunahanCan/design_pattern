package com.can.structural.adapter;

import java.util.Objects;

public class SquarePegAdapter extends RoundPeg {

    private final SquarePeg squarePeg;

    public SquarePegAdapter(SquarePeg squarePeg) {
        super(0);
        this.squarePeg = Objects.requireNonNull(squarePeg, "squarePeg cannot be null");
    }

    @Override
    public double getRadius() {
        return squarePeg.getWidth() / Math.sqrt(2);
    }
}
