package com.can.creational.abstractfactory;

public final class DarkCheckbox implements Checkbox {

    @Override
    public String render() {
        return "Koyu tema checkbox";
    }
}
