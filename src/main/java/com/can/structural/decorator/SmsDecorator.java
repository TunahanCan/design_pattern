package com.can.structural.decorator;

public class SmsDecorator extends BaseNotifierDecorator {

    private final String phone;

    public SmsDecorator(Notifier wrappee, String phone) {
        super(wrappee);
        this.phone = phone;
    }

    @Override
    public String send(String message) {
        return super.send(message) + System.lineSeparator()
            + "SMS -> " + phone + " | mesaj=" + message;
    }
}
