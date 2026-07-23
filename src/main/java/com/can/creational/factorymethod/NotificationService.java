package com.can.creational.factorymethod;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NotificationService {

    private final Map<NotificationChannel, NotificationCreator> creators;

    public NotificationService(Map<NotificationChannel, NotificationCreator> creators) {
        Objects.requireNonNull(creators, "creators cannot be null");
        creators.forEach((channel, creator) -> {
            Objects.requireNonNull(channel, "creator channel cannot be null");
            Objects.requireNonNull(creator, "creator cannot be null");
            NotificationChannel declaredChannel = creator.declaredChannel();
            if (declaredChannel != null && declaredChannel != channel) {
                throw new IllegalArgumentException(
                        "Creator registered for " + channel + " declares " + declaredChannel
                );
            }
        });
        this.creators = Map.copyOf(creators);
    }

    public void send(NotificationChannel channel, NotificationRequest request) {
        Objects.requireNonNull(channel, "channel cannot be null");
        Objects.requireNonNull(request, "request cannot be null");
        NotificationCreator creator = creators.get(channel);
        if (creator == null) {
            throw new IllegalArgumentException("No creator found for channel: " + channel);
        }
        creator.notifyUserForChannel(channel, request);
    }

    /**
     * Gerçek hayattaki kampanya/iş kuyruğu kullanımına küçük bir örnek:
     * her iş kendi kanalına ve alıcısına sahiptir, service ise sırayı koruyarak yönlendirir.
     */
    public void sendAll(List<NotificationJob> jobs) {
        List.copyOf(Objects.requireNonNull(jobs, "jobs cannot be null"))
                .forEach(job -> send(job.channel(), job.request()));
    }
}
