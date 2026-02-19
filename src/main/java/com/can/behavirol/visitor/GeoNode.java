package com.can.behavirol.visitor;

public interface GeoNode {
    String getName();
    void accept(GeoNodeVisitor visitor);
}
