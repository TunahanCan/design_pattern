package com.can.structural.decorator;

import java.util.List;

public class DecoratorPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("4) Decorator");

        Notifier notifier = new EmailNotifier(List.of("ops@company.com", "owner@company.com"));
        notifier = new SmsDecorator(notifier, "+90 555 111 22 33");
        notifier = new SlackDecorator(notifier, "#kritik-alarm");

        String result = notifier.send("Sunucu CPU kullanımı %95 oldu!");
        System.out.println(result);

        Notifier socialNotifier = new FacebookDecorator(new EmailNotifier(List.of("marketing@company.com")), "company.page");
        System.out.println();
        System.out.println("Alternatif stack:");
        System.out.println(socialNotifier.send("Yeni kampanya yayında."));
        System.out.println();
    }
}
