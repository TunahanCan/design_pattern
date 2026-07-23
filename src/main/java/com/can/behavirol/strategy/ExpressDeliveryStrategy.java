package com.can.behavirol.strategy;

public class ExpressDeliveryStrategy implements DeliveryStrategy {

    @Override
    public DeliveryQuote quote(Shipment shipment) {
        long additionalItemCount = (long) shipment.itemCount() - 1;
        long fee = Math.addExact(
                8_990L,
                Math.multiplyExact(additionalItemCount, 1_000L)
        );
        int estimatedDays = shipment.sameCity() ? 1 : 2;
        return new DeliveryQuote("Ekspres teslimat", fee, estimatedDays);
    }
}
