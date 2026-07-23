package com.can.behavirol.strategy;

public class StandardDeliveryStrategy implements DeliveryStrategy {

    @Override
    public DeliveryQuote quote(Shipment shipment) {
        long additionalItemCount = (long) shipment.itemCount() - 1;
        long fee = shipment.premiumCustomer()
                ? 0L
                : Math.addExact(
                        4_990L,
                        Math.multiplyExact(additionalItemCount, 500L)
                );
        int estimatedDays = shipment.sameCity() ? 2 : 4;
        return new DeliveryQuote("Standart teslimat", fee, estimatedDays);
    }
}
