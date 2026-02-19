package com.can.behavirol.visitor;

public class SightSeeing implements GeoNode {

    private final String name;
    private final int annualVisitors;

    public SightSeeing(String name, int annualVisitors) {
        this.name = name;
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
}
