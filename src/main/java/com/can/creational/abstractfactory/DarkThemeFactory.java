package com.can.creational.abstractfactory;

public final class DarkThemeFactory implements GuiFactory {

    @Override
    public Button createButton() {
        return new DarkButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new DarkCheckbox();
    }
}
