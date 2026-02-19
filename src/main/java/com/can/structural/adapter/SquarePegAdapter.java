package com.can.structural.adapter;

public class SquarePegAdapter extends RoundPeg {

    private final SquarePeg squarePeg;

    public SquarePegAdapter(SquarePeg squarePeg) {
        super(0);
        this.squarePeg = squarePeg;
    }

    @Override
    public double getRadius() {
        return (squarePeg.getWidth() * Math.sqrt(2)) / 2;
    }
}
