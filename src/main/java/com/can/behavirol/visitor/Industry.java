package com.can.behavirol.visitor;

public class Industry implements GeoNode {

    private final String name;
    private final String sector;

    public Industry(String name, String sector) {
        this.name = requireText(name, "name");
        this.sector = requireText(sector, "sector");
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

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
