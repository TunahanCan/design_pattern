package com.can.structural.adapter;

public interface ShippingService {

    DeliveryQuote quote(Parcel parcel);
}
