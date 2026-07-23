package com.can.structural.decorator;

import java.util.List;

public class EmailNotifier implements Notifier {

    private final List<String> recipients;

    public EmailNotifier(List<String> recipients) {
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("recipients cannot be empty");
        }
        this.recipients = recipients.stream()
            .map(recipient -> Notifier.requireText(recipient, "email recipient"))
            .toList();
    }

    @Override
    public String send(String message) {
        return "Email -> " + recipients + " | mesaj="
            + Notifier.requireText(message, "message");
    }
}
