package com.can.creational.prototype;

import java.util.Objects;

public class Address {
    private String city;
    private String country;

    public Address(String city, String country) {
        this.city = normalize(city, "city");
        this.country = normalize(country, "country");
    }

    public Address(Address source) {
        Address validatedSource = Objects.requireNonNull(source, "source address cannot be null");
        this.city = validatedSource.city;
        this.country = validatedSource.country;
    }

    public String city() {
        return city;
    }

    public String country() {
        return country;
    }

    public void moveTo(String city, String country) {
        String normalizedCity = normalize(city, "city");
        String normalizedCountry = normalize(country, "country");
        this.city = normalizedCity;
        this.country = normalizedCountry;
    }

    @Override
    public String toString() {
        return city + "/" + country;
    }

    private static String normalize(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " cannot be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return normalized;
    }
}
