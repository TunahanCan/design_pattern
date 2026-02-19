package com.can.behavirol.observer;

public interface Publisher {
    void subscribe(String productName, Subscriber subscriber);

    void unsubscribe(String productName, Subscriber subscriber);

    void notifySubscribers(String productName, String message);
}
