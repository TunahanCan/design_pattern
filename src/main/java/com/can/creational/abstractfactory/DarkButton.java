package com.can.creational.abstractfactory;

public final class DarkButton implements Button {

    @Override
    public String render() {
        return "Koyu tema butonu";
    }
}
