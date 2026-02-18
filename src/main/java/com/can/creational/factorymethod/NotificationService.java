package com.can.creational.factorymethod;

import java.util.Map;

public class NotificationService {

    private final Map<NotificationChannel, NotificationCreator> creators;

    public NotificationService(Map<NotificationChannel, NotificationCreator> creators) {
        this.creators = Map.copyOf(creators);
    }

    public void send(NotificationChannel channel, NotificationRequest request) {
        NotificationCreator creator = creators.get(channel);
        if (creator == null) {
            throw new IllegalArgumentException("No creator found for channel: " + channel);
        }
        creator.notifyUser(request);
    }
}
