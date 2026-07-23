package com.can.creational.factorymethod;

public record NotificationRequest(String recipient, String title, String message) {

    public NotificationRequest {
        recipient = normalize(recipient, "recipient");
        title = normalize(title, "title");
        message = normalize(message, "message");
    }

    private static String normalize(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
}
