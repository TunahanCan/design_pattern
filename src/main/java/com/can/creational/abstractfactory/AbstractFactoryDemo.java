package com.can.creational.abstractfactory;

public class AbstractFactoryDemo {

    interface Button {
        String render();
    }

    interface Checkbox {
        String render();
    }

    static class LightButton implements Button {
        @Override
        public String render() {
            return "Açık tema butonu";
        }
    }

    static class DarkButton implements Button {
        @Override
        public String render() {
            return "Koyu tema butonu";
        }
    }

    static class LightCheckbox implements Checkbox {
        @Override
        public String render() {
            return "Açık tema checkbox";
        }
    }

    static class DarkCheckbox implements Checkbox {
        @Override
        public String render() {
            return "Koyu tema checkbox";
        }
    }

    interface GuiFactory {
        Button createButton();
        Checkbox createCheckbox();
    }

    static class LightThemeFactory implements GuiFactory {
        @Override
        public Button createButton() {
            return new LightButton();
        }

        @Override
        public Checkbox createCheckbox() {
            return new LightCheckbox();
        }
    }

    static class DarkThemeFactory implements GuiFactory {
        @Override
        public Button createButton() {
            return new DarkButton();
        }

        @Override
        public Checkbox createCheckbox() {
            return new DarkCheckbox();
        }
    }

    static class UiScreen {
        private final Button button;
        private final Checkbox checkbox;

        UiScreen(GuiFactory factory) {
            this.button = factory.createButton();
            this.checkbox = factory.createCheckbox();
        }

        String draw() {
            return button.render() + " + " + checkbox.render();
        }
    }

    public static void run() {
        System.out.println("2) Abstract Factory");

        UiScreen lightScreen = new UiScreen(new LightThemeFactory());
        UiScreen darkScreen = new UiScreen(new DarkThemeFactory());

        System.out.println("Light UI: " + lightScreen.draw());
        System.out.println("Dark UI: " + darkScreen.draw());
        System.out.println();
    }
}
