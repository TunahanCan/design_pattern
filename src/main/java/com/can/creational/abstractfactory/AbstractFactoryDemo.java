package com.can.creational.abstractfactory;

public class AbstractFactoryDemo {

    public static void main(String[] args) {
        run();
    }


    public static void run() {
        System.out.println("2) Abstract Factory");

        System.out.println("Temel örnek — standart tema aileleri:");
        renderScreen(Theme.LIGHT);
        renderScreen(Theme.DARK);

        System.out.println("Daha gerçekçi örnek — erişilebilirlik ailesi:");
        renderScreen(Theme.HIGH_CONTRAST);

        System.out.println();
    }

    private static void renderScreen(Theme theme) {
        GuiFactory factory = GuiFactoryProvider.forTheme(theme);
        UiScreen screen = new UiScreen(factory);
        System.out.println(theme.name() + " UI: " + screen.draw());
    }
}
