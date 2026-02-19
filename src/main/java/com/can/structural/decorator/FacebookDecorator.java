package com.can.structural.decorator;

public class FacebookDecorator extends BaseNotifierDecorator {

    private final String account;

    public FacebookDecorator(Notifier wrappee, String account) {
        super(wrappee);
        this.account = account;
    }

    @Override
    public String send(String message) {
        return super.send(message) + System.lineSeparator()
            + "Facebook -> " + account + " | mesaj=" + message;
    }
}
