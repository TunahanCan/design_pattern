package com.can.structural.decorator;

public class SlackDecorator extends BaseNotifierDecorator {

    private final String channel;

    public SlackDecorator(Notifier wrappee, String channel) {
        super(wrappee);
        this.channel = channel;
    }

    @Override
    public String send(String message) {
        return super.send(message) + System.lineSeparator()
            + "Slack -> " + channel + " | mesaj=" + message;
    }
}
