package com.can.creational.singleton;

public class SingletonDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("5) Singleton");

        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();

        System.out.println("Config: " + config1.describe());
        System.out.println("Aynı instance mı? " + (config1 == config2));
        System.out.println();
    }
}
