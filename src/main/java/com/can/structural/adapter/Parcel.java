package com.can.structural.adapter;

public record Parcel(String destinationPostalCode, int weightGrams) {

    public Parcel {
        if (destinationPostalCode == null || destinationPostalCode.isBlank()) {
            throw new IllegalArgumentException("destinationPostalCode cannot be blank");
        }
        if (weightGrams <= 0) {
            throw new IllegalArgumentException("weightGrams must be positive");
        }
    }
}
