package com.can.behavirol.mediator;

public abstract class Component {
    protected final Mediator mediator;

    protected Component(Mediator mediator) {
        this.mediator = mediator;
    }
}
