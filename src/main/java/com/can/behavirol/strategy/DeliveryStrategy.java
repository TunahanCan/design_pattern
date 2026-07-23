package com.can.behavirol.strategy;

public interface DeliveryStrategy {

    DeliveryQuote quote(Shipment shipment);
}
