package com.can.structural.decorator;

import java.util.List;

public class EmailNotifier implements Notifier {

    private final List<String> recipients;

    public EmailNotifier(List<String> recipients) {
        this.recipients = List.copyOf(recipients);
    }

    @Override
    public String send(String message) {
        return "Email -> " + recipients + " | mesaj=" + message;
    }
}
