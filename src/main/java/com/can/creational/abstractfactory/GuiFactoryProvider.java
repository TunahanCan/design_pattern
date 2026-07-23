package com.can.creational.abstractfactory;

import java.util.Objects;

public final class GuiFactoryProvider {

    private GuiFactoryProvider() {
    }

    public static GuiFactory forTheme(Theme theme) {
        return switch (Objects.requireNonNull(theme, "theme cannot be null")) {
            case LIGHT -> new LightThemeFactory();
            case DARK -> new DarkThemeFactory();
            case HIGH_CONTRAST -> new HighContrastThemeFactory();
        };
    }
}
