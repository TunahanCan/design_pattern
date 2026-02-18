package com.can.creational.factorymethod;

public record NotificationRequest(String recipient, String title, String message) {

    public NotificationRequest {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("recipient cannot be blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message cannot be blank");
        }
    }
}
