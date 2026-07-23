package com.can.behavirol.visitor;

public class City implements GeoNode {

    private final String name;
    private final int population;

    public City(String name, int population) {
        this.name = requireText(name, "name");
        if (population < 0) {
            throw new IllegalArgumentException("population cannot be negative");
        }
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

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
