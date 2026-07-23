package com.can.creational.abstractfactory;

import java.util.Objects;

public final class UiScreen {

    private final Button button;
    private final Checkbox checkbox;

    public UiScreen(GuiFactory factory) {
        GuiFactory validatedFactory = Objects.requireNonNull(factory, "factory cannot be null");
        this.button = Objects.requireNonNull(
                validatedFactory.createButton(),
                "factory must create a button"
        );
        this.checkbox = Objects.requireNonNull(
                validatedFactory.createCheckbox(),
                "factory must create a checkbox"
        );
    }

    public String draw() {
        return button.render() + " + " + checkbox.render();
    }
}
