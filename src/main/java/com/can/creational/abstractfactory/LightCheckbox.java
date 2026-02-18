package com.can.creational.abstractfactory;

public final class LightCheckbox implements Checkbox {

    @Override
    public String render() {
        return "Açık tema checkbox";
    }
}
