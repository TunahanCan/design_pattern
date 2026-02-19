package com.can.behavirol.visitor;

public interface GeoNodeVisitor {
    void visitCity(City city);
    void visitIndustry(Industry industry);
    void visitSightSeeing(SightSeeing sightSeeing);
}
