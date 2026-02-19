package com.can.behavirol.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store implements Publisher {

    private final Map<String, List<Subscriber>> subscribersByProduct = new HashMap<>();

    @Override
    public void subscribe(String productName, Subscriber subscriber) {
        subscribersByProduct
                .computeIfAbsent(productName, ignored -> new ArrayList<>())
                .add(subscriber);
    }

    @Override
    public void unsubscribe(String productName, Subscriber subscriber) {
        List<Subscriber> subscribers = subscribersByProduct.get(productName);
        if (subscribers == null) {
            return;
        }

        subscribers.remove(subscriber);
        if (subscribers.isEmpty()) {
            subscribersByProduct.remove(productName);
        }
    }

    @Override
    public void notifySubscribers(String productName, String message) {
        List<Subscriber> subscribers = subscribersByProduct.get(productName);
        if (subscribers == null) {
            System.out.println("Bu ürün için abone bulunmuyor: " + productName);
            return;
        }

        for (Subscriber subscriber : subscribers) {
            subscriber.update(productName, message);
        }
    }

    public void restockProduct(String productName, int stock) {
        String message = String.format("%s tekrar stokta! Adet: %d", productName, stock);
        notifySubscribers(productName, message);
    }
}
