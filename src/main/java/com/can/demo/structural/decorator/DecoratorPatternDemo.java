package com.can.demo.structural.decorator;

import com.can.structural.decorator.EmailNotifier;
import com.can.structural.decorator.FacebookDecorator;
import com.can.structural.decorator.Notifier;
import com.can.structural.decorator.PriorityDecorator;
import com.can.structural.decorator.SlackDecorator;
import com.can.structural.decorator.SmsDecorator;
import java.util.List;

/**
 * Executable composition root for the Decorator example.
 *
 * <p>It selects decorator order; reusable notification components stay in
 * {@code com.can.structural.decorator}.</p>
 */
public final class DecoratorPatternDemo {

    private DecoratorPatternDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("4) Decorator");

        Notifier notifier = new EmailNotifier(List.of("ops@company.com", "owner@company.com"));
        notifier = new SmsDecorator(notifier, "+90 555 111 22 33");
        notifier = new SlackDecorator(notifier, "#kritik-alarm");
        notifier = new PriorityDecorator(notifier, "p1");

        String result = notifier.send("Sunucu CPU kullanımı %95 oldu!");
        System.out.println(result);

        Notifier socialNotifier = new FacebookDecorator(new EmailNotifier(List.of("marketing@company.com")), "company.page");
        System.out.println();
        System.out.println("Alternatif stack:");
        System.out.println(socialNotifier.send("Yeni kampanya yayında."));
        System.out.println();
    }
}
