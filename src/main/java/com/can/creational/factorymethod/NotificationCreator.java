package com.can.creational.factorymethod;

import java.util.Objects;

public abstract class NotificationCreator {

    private final NotificationSender sender;
    private final NotificationChannel declaredChannel;

    /**
     * Eski/custom creator implementasyonlarının kaynak uyumluluğunu korur.
     * Kanal metadata'sı verilmediğinde {@link #channel()} gerektiğinde product'tan çıkarır.
     */
    protected NotificationCreator(NotificationSender sender) {
        this.sender = Objects.requireNonNull(sender, "sender cannot be null");
        this.declaredChannel = null;
    }

    /**
     * Kanalı önceden bilen concrete creator'lar için yan etkisiz metadata yolu.
     */
    protected NotificationCreator(NotificationSender sender, NotificationChannel declaredChannel) {
        this.sender = Objects.requireNonNull(sender, "sender cannot be null");
        this.declaredChannel = Objects.requireNonNull(
                declaredChannel,
                "declaredChannel cannot be null"
        );
    }

    protected NotificationSender sender() {
        return sender;
    }

    /**
     * Mevcut custom creator'ları kırmamak için product'tan kanal çıkaran bir fallback sunar.
     * Repo içindeki concrete creator'lar constructor metadata'sı kullandığı için bu çağrı
     * product üretmez.
     */
    public NotificationChannel channel() {
        if (declaredChannel != null) {
            return declaredChannel;
        }
        Notification notification = Objects.requireNonNull(
                createNotification(),
                "creator cannot produce a null notification"
        );
        return Objects.requireNonNull(
                notification.channel(),
                "notification channel cannot be null"
        );
    }

    public abstract Notification createNotification();

    public void notifyUser(NotificationRequest request) {
        createValidateAndSend(null, request);
    }

    void notifyUserForChannel(NotificationChannel selectedChannel, NotificationRequest request) {
        createValidateAndSend(
                Objects.requireNonNull(selectedChannel, "selectedChannel cannot be null"),
                request
        );
    }

    NotificationChannel declaredChannel() {
        return declaredChannel;
    }

    private void createValidateAndSend(
            NotificationChannel selectedChannel,
            NotificationRequest request
    ) {
        NotificationRequest validatedRequest = Objects.requireNonNull(request, "request cannot be null");
        Notification notification = Objects.requireNonNull(
                createNotification(),
                "creator cannot produce a null notification"
        );
        NotificationChannel productChannel = Objects.requireNonNull(
                notification.channel(),
                "notification channel cannot be null"
        );

        if (declaredChannel != null && productChannel != declaredChannel) {
            throw new IllegalStateException(
                    "Creator channel " + declaredChannel + " produced " + productChannel
            );
        }

        if (selectedChannel != null && productChannel != selectedChannel) {
            throw new IllegalStateException(
                    "Creator registered for " + selectedChannel + " produced " + productChannel
            );
        }
        notification.send(validatedRequest);
    }
}
