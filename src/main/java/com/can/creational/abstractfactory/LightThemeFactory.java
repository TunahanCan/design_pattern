package com.can.creational.abstractfactory;

public final class LightThemeFactory implements GuiFactory {

    @Override
    public Button createButton() {
        return new LightButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new LightCheckbox();
    }
}
