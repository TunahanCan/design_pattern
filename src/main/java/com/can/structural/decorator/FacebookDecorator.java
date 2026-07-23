package com.can.structural.decorator;

public class FacebookDecorator extends BaseNotifierDecorator {

    private final String account;

    public FacebookDecorator(Notifier wrappee, String account) {
        super(wrappee);
        this.account = Notifier.requireText(account, "Facebook account");
    }

    @Override
    public String send(String message) {
        String validatedMessage = Notifier.requireText(message, "message");
        return super.send(validatedMessage) + System.lineSeparator()
            + "Facebook -> " + account + " | mesaj=" + validatedMessage;
    }
}
