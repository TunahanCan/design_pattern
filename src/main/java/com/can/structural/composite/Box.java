package com.can.structural.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Box implements OrderComponent {
    private final String name;
    private final double packagingCost;
    private final List<OrderComponent> children = new ArrayList<>();

    public Box(String name, double packagingCost) {
        this.name = name;
        this.packagingCost = packagingCost;
    }

    public void add(OrderComponent component) {
        children.add(component);
    }

    public void remove(OrderComponent component) {
        children.remove(component);
    }

    public List<OrderComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public double getPrice() {
        double total = packagingCost;
        for (OrderComponent child : children) {
            total += child.getPrice();
        }
        return total;
    }

    @Override
    public String getName() {
        return name;
    }
}
