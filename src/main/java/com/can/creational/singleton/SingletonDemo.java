package com.can.creational.singleton;

public class SingletonDemo {

    static class AppConfig {
        private static volatile AppConfig instance;
        private final String apiBaseUrl;

        private AppConfig() {
            this.apiBaseUrl = "https://api.example.com";
        }

        public static AppConfig getInstance() {
            if (instance == null) {
                synchronized (AppConfig.class) {
                    if (instance == null) {
                        instance = new AppConfig();
                    }
                }
            }
            return instance;
        }

        public String getApiBaseUrl() {
            return apiBaseUrl;
        }
    }

    public static void run() {
        System.out.println("5) Singleton");

        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();

        System.out.println("Config URL: " + config1.getApiBaseUrl());
        System.out.println("Aynı instance mı? " + (config1 == config2));
        System.out.println();
    }
}
