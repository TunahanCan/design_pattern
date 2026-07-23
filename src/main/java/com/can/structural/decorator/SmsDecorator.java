package com.can.structural.decorator;

public class SmsDecorator extends BaseNotifierDecorator {

    private final String phone;

    public SmsDecorator(Notifier wrappee, String phone) {
        super(wrappee);
        this.phone = Notifier.requireText(phone, "phone");
    }

    @Override
    public String send(String message) {
        String validatedMessage = Notifier.requireText(message, "message");
        return super.send(validatedMessage) + System.lineSeparator()
            + "SMS -> " + phone + " | mesaj=" + validatedMessage;
    }
}
