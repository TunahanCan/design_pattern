package com.can.structural.decorator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class DecoratorPatternDemoTest {

    @Test
    void shouldSendViaEmailSmsAndSlack() {
        Notifier notifier = new EmailNotifier(List.of("ops@company.com"));
        notifier = new SmsDecorator(notifier, "+90 555 111 22 33");
        notifier = new SlackDecorator(notifier, "#kritik-alarm");

        String result = notifier.send("Alarm");

        assertTrue(result.contains("Email -> [ops@company.com]"));
        assertTrue(result.contains("SMS -> +90 555 111 22 33"));
        assertTrue(result.contains("Slack -> #kritik-alarm"));
    }

    @Test
    void shouldAllowDifferentDecoratorStack() {
        Notifier notifier = new FacebookDecorator(
            new EmailNotifier(List.of("marketing@company.com")),
            "company.page"
        );

        String result = notifier.send("Kampanya");

        assertTrue(result.contains("Email -> [marketing@company.com]"));
        assertTrue(result.contains("Facebook -> company.page"));
    }
}
