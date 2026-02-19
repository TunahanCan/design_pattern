package com.can.behavirol.visitor;

public class Industry implements GeoNode {

    private final String name;
    private final String sector;

    public Industry(String name, String sector) {
        this.name = name;
        this.sector = sector;
    }

    public String getSector() {
        return sector;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void accept(GeoNodeVisitor visitor) {
        visitor.visitIndustry(this);
    }
}
