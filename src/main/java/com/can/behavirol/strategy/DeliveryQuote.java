package com.can.behavirol.strategy;

public record DeliveryQuote(String serviceName, long feeInCents, int estimatedDays) {

    public DeliveryQuote {
        if (feeInCents < 0) {
            throw new IllegalArgumentException("feeInCents cannot be negative");
        }
    }
}
