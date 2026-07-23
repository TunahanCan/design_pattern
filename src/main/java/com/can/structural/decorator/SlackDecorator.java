package com.can.structural.decorator;

public class SlackDecorator extends BaseNotifierDecorator {

    private final String channel;

    public SlackDecorator(Notifier wrappee, String channel) {
        super(wrappee);
        this.channel = Notifier.requireText(channel, "Slack channel");
    }

    @Override
    public String send(String message) {
        String validatedMessage = Notifier.requireText(message, "message");
        return super.send(validatedMessage) + System.lineSeparator()
            + "Slack -> " + channel + " | mesaj=" + validatedMessage;
    }
}
