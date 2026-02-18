package com.can.creational.abstractfactory;

public final class UiScreen {

    private final Button button;
    private final Checkbox checkbox;

    public UiScreen(GuiFactory factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }

    public String draw() {
        return button.render() + " + " + checkbox.render();
    }
}
