package com.can.behavirol.visitor;

public class SightSeeing implements GeoNode {

    private final String name;
    private final int annualVisitors;

    public SightSeeing(String name, int annualVisitors) {
        this.name = requireText(name, "name");
        if (annualVisitors < 0) {
            throw new IllegalArgumentException("annualVisitors cannot be negative");
        }
        this.annualVisitors = annualVisitors;
    }

    public int getAnnualVisitors() {
        return annualVisitors;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void accept(GeoNodeVisitor visitor) {
        visitor.visitSightSeeing(this);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
