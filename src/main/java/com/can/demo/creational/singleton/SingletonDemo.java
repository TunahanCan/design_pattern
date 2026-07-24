package com.can.demo.creational.singleton;

import com.can.creational.singleton.ApiClient;
import com.can.creational.singleton.AppConfig;

public final class SingletonDemo {

    private SingletonDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("5) Singleton");

        System.out.println("Temel örnek — process içindeki tek config identity'si:");
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();

        System.out.println("Config: " + config1.describe());
        System.out.println("Aynı instance mı? " + (config1 == config2));

        System.out.println(
                "Daha gerçekçi örnek — singleton yalnız composition root'ta çözülür:"
        );
        ApiClient apiClient = new ApiClient(config1);
        System.out.println("Request: " + apiClient.planGet("/health").describe());
        System.out.println();
    }
}
