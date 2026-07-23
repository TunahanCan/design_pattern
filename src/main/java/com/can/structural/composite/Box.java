package com.can.structural.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Box implements OrderComponent {

    private final String name;
    private final BigDecimal packagingCost;
    private final List<OrderComponent> children = new ArrayList<>();

    public Box(String name, double packagingCost) {
        this(name, BigDecimal.valueOf(packagingCost));
    }

    public Box(String name, BigDecimal packagingCost) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        Objects.requireNonNull(packagingCost, "packagingCost cannot be null");
        if (packagingCost.signum() < 0) {
            throw new IllegalArgumentException("packagingCost cannot be negative");
        }
        this.name = name.trim();
        this.packagingCost = packagingCost;
    }

    public void add(OrderComponent component) {
        Objects.requireNonNull(component, "component cannot be null");
        if (component == this) {
            throw new IllegalArgumentException("A box cannot contain itself");
        }
        if (component instanceof Box childBox && childBox.contains(this)) {
            throw new IllegalArgumentException("Adding this box would create a cycle");
        }
        children.add(component);
    }

    public void remove(OrderComponent component) {
        children.remove(component);
    }

    public List<OrderComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<OrderComponent> getChildrenSnapshot() {
        return List.copyOf(children);
    }

    @Override
    public BigDecimal getPriceAmount() {
        BigDecimal total = packagingCost;
        for (OrderComponent child : children) {
            total = total.add(child.getPriceAmount());
        }
        return total;
    }

    @Override
    public double getPrice() {
        return getPriceAmount().doubleValue();
    }

    @Override
    public String getName() {
        return name;
    }

    private boolean contains(OrderComponent searchedComponent) {
        if (this == searchedComponent) {
            return true;
        }
        for (OrderComponent child : children) {
            if (child == searchedComponent) {
                return true;
            }
            if (child instanceof Box childBox && childBox.contains(searchedComponent)) {
                return true;
            }
        }
        return false;
    }
}
