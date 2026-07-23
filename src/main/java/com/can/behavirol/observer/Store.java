package com.can.behavirol.observer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Store implements Publisher {

    private final Map<String, Map<Subscriber, SubscriberRegistration>> subscribersByProduct =
            new HashMap<>();

    @Override
    public void subscribe(String productName, Subscriber subscriber) {
        SubscriberRegistration registration = registrationsFor(productName)
                .computeIfAbsent(subscriber, ignored -> new SubscriberRegistration());
        registration.directSubscription = true;
    }

    public Subscription subscribeWithHandle(String productName, Subscriber subscriber) {
        SubscriberRegistration registration = registrationsFor(productName)
                .computeIfAbsent(subscriber, ignored -> new SubscriberRegistration());
        registration.openHandleCount++;

        AtomicBoolean active = new AtomicBoolean(true);
        return () -> {
            if (active.compareAndSet(true, false)) {
                releaseHandle(productName, subscriber);
            }
        };
    }

    @Override
    public void unsubscribe(String productName, Subscriber subscriber) {
        Map<Subscriber, SubscriberRegistration> registrations =
                subscribersByProduct.get(productName);
        if (registrations == null) {
            return;
        }

        SubscriberRegistration registration = registrations.get(subscriber);
        if (registration == null) {
            return;
        }

        registration.directSubscription = false;
        removeRegistrationIfInactive(productName, subscriber, registrations, registration);
    }

    @Override
    public void notifySubscribers(String productName, String message) {
        Map<Subscriber, SubscriberRegistration> registrations =
                subscribersByProduct.get(productName);
        if (registrations == null) {
            System.out.println("Bu ürün için abone bulunmuyor: " + productName);
            return;
        }

        for (Subscriber subscriber : List.copyOf(registrations.keySet())) {
            subscriber.update(productName, message);
        }
    }

    public void restockProduct(String productName, int stock) {
        String message = String.format("%s tekrar stokta! Adet: %d", productName, stock);
        notifySubscribers(productName, message);
    }

    private Map<Subscriber, SubscriberRegistration> registrationsFor(String productName) {
        return subscribersByProduct.computeIfAbsent(
                productName,
                ignored -> new LinkedHashMap<>()
        );
    }

    private void releaseHandle(String productName, Subscriber subscriber) {
        Map<Subscriber, SubscriberRegistration> registrations =
                subscribersByProduct.get(productName);
        if (registrations == null) {
            return;
        }

        SubscriberRegistration registration = registrations.get(subscriber);
        if (registration == null || registration.openHandleCount == 0) {
            return;
        }

        registration.openHandleCount--;
        removeRegistrationIfInactive(productName, subscriber, registrations, registration);
    }

    private void removeRegistrationIfInactive(
            String productName,
            Subscriber subscriber,
            Map<Subscriber, SubscriberRegistration> registrations,
            SubscriberRegistration registration
    ) {
        if (registration.directSubscription || registration.openHandleCount > 0) {
            return;
        }

        registrations.remove(subscriber);
        if (registrations.isEmpty()) {
            subscribersByProduct.remove(productName);
        }
    }

    private static final class SubscriberRegistration {
        private boolean directSubscription;
        private int openHandleCount;
    }
}
