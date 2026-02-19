package com.can.behavirol.visitor;

public class City implements GeoNode {

    private final String name;
    private final int population;

    public City(String name, int population) {
        this.name = name;
        this.population = population;
    }

    public int getPopulation() {
        return population;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void accept(GeoNodeVisitor visitor) {
        visitor.visitCity(this);
    }
}
