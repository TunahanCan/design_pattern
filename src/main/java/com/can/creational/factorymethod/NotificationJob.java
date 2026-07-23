package com.can.creational.factorymethod;

import java.util.Objects;

/**
 * Bir kuyruğa yazılabilecek tek bildirim işini temsil eder.
 */
public record NotificationJob(NotificationChannel channel, NotificationRequest request) {

    public NotificationJob {
        Objects.requireNonNull(channel, "channel cannot be null");
        Objects.requireNonNull(request, "request cannot be null");
    }
}
