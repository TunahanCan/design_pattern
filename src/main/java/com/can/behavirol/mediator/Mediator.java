package com.can.behavirol.mediator;

public interface Mediator {
    void notify(Component sender, String event);
}
