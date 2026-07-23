package com.can.structural.adapter;

import java.math.BigDecimal;
import java.util.Objects;

public record DeliveryQuote(String provider, BigDecimal priceTry, int estimatedDays) {

    public DeliveryQuote {
        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("provider cannot be blank");
        }
        Objects.requireNonNull(priceTry, "priceTry cannot be null");
        if (priceTry.signum() < 0) {
            throw new IllegalArgumentException("priceTry cannot be negative");
        }
        if (estimatedDays <= 0) {
            throw new IllegalArgumentException("estimatedDays must be positive");
        }
    }
}
