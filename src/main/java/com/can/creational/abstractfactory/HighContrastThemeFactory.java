package com.can.creational.abstractfactory;

/**
 * Aynı erişilebilirlik dilini taşıyan bütün UI bileşenlerini birlikte üretir.
 */
public final class HighContrastThemeFactory implements GuiFactory {

    @Override
    public Button createButton() {
        return new HighContrastButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new HighContrastCheckbox();
    }
}
