package com.can.structural.decorator;

public abstract class BaseNotifierDecorator implements Notifier {

    protected final Notifier wrappee;

    protected BaseNotifierDecorator(Notifier wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public String send(String message) {
        return wrappee.send(message);
    }
}
