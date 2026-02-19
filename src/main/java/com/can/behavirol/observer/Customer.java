package com.can.behavirol.observer;

public class Customer implements Subscriber {

    private final String name;
    private final String channel;

    public Customer(String name, String channel) {
        this.name = name;
        this.channel = channel;
    }

    @Override
    public void update(String productName, String message) {
        System.out.printf("[%s] %s bildirimi aldı -> %s (%s)%n", channel, name, message, productName);
    }
}
