package com.can.creational.abstractfactory;

public class AbstractFactoryDemo {

    public static void run() {
        System.out.println("2) Abstract Factory");

        renderScreen(Theme.LIGHT);
        renderScreen(Theme.DARK);

        System.out.println();
    }

    private static void renderScreen(Theme theme) {
        GuiFactory factory = GuiFactoryProvider.forTheme(theme);
        UiScreen screen = new UiScreen(factory);

        System.out.println(theme.name() + " UI: " + screen.draw());
    }
}
