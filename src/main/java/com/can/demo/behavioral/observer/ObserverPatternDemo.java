package com.can.demo.behavioral.observer;

import com.can.behavirol.observer.Customer;
import com.can.behavirol.observer.Store;
import com.can.behavirol.observer.Subscription;

public final class ObserverPatternDemo {

    private ObserverPatternDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("6) Observer");

        Store store = new Store();

        Customer can = new Customer("Can", "E-Posta");
        Customer ayse = new Customer("Ayşe", "SMS");
        Customer mehmet = new Customer("Mehmet", "Push");

        store.subscribe("iPhone 16", can);
        Subscription ayseSubscription = store.subscribeWithHandle("iPhone 16", ayse);
        store.subscribe("PlayStation 6", mehmet);

        System.out.println("iPhone 16 stok güncellemesi:");
        store.restockProduct("iPhone 16", 12);

        ayseSubscription.close();

        System.out.println("iPhone 16 ikinci stok güncellemesi:");
        store.restockProduct("iPhone 16", 5);

        System.out.println("PlayStation 6 stok güncellemesi:");
        store.restockProduct("PlayStation 6", 3);
        System.out.println();
    }
}
