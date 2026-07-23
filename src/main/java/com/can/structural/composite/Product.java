package com.can.structural.composite;

import java.math.BigDecimal;
import java.util.Objects;

public class Product implements OrderComponent {

    private final String name;
    private final BigDecimal price;

    public Product(String name, double price) {
        this(name, BigDecimal.valueOf(price));
    }

    public Product(String name, BigDecimal price) {
        this.name = requireName(name);
        this.price = requireNonNegative(price, "price");
    }

    @Override
    public BigDecimal getPriceAmount() {
        return price;
    }

    @Override
    public double getPrice() {
        return price.doubleValue();
    }

    @Override
    public String getName() {
        return name;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        return name.trim();
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        if (value.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
        return value;
    }
}
