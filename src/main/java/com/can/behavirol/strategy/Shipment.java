package com.can.behavirol.strategy;

public record Shipment(int itemCount, boolean sameCity, boolean premiumCustomer) {

    public Shipment {
        if (itemCount < 1) {
            throw new IllegalArgumentException("Gönderide en az bir ürün olmalıdır.");
        }
    }
}
