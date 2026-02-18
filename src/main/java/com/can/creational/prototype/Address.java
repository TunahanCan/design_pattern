package com.can.creational.prototype;

public class Address {
    private String city;
    private String country;

    public Address(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public Address(Address source) {
        this.city = source.city;
        this.country = source.country;
    }

    public String city() {
        return city;
    }

    public String country() {
        return country;
    }

    public void moveTo(String city, String country) {
        this.city = city;
        this.country = country;
    }

    @Override
    public String toString() {
        return city + "/" + country;
    }
}
