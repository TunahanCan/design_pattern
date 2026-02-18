package com.can.creational.abstractfactory;

public final class GuiFactoryProvider {

    private GuiFactoryProvider() {
    }

    public static GuiFactory forTheme(Theme theme) {
        return switch (theme) {
            case LIGHT -> new LightThemeFactory();
            case DARK -> new DarkThemeFactory();
        };
    }
}
