package com.can.structural.decorator;

import java.util.Objects;

public abstract class BaseNotifierDecorator implements Notifier {

    protected final Notifier wrappee;

    protected BaseNotifierDecorator(Notifier wrappee) {
        this.wrappee = Objects.requireNonNull(wrappee, "wrappee cannot be null");
    }

    @Override
    public String send(String message) {
        return wrappee.send(message);
    }
}
