package com.can.behavirol.strategy;

import java.util.Objects;

public class DeliveryPlanner {

    private DeliveryStrategy strategy;

    public DeliveryPlanner(DeliveryStrategy strategy) {
        setStrategy(strategy);
    }

    public void setStrategy(DeliveryStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public DeliveryQuote quote(Shipment shipment) {
        return strategy.quote(shipment);
    }
}
