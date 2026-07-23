package com.can.creational.abstractfactory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Abstract Factory — bir tema seçildiğinde uyumlu ürün ailesi oluşturulur")
class AbstractFactoryDemoTest {

    @Nested
    @DisplayName("Tema factory seçimini belirler")
    class FactorySelection {

        @Test
        @DisplayName("LIGHT, DARK ve HIGH_CONTRAST kendi concrete factory'lerine çözülür")
        void themesResolveToTheirConcreteFactories() {
            // Arrange & Act
            GuiFactory lightFactory = GuiFactoryProvider.forTheme(Theme.LIGHT);
            GuiFactory darkFactory = GuiFactoryProvider.forTheme(Theme.DARK);
            GuiFactory highContrastFactory = GuiFactoryProvider.forTheme(Theme.HIGH_CONTRAST);

            // Assert
            assertAll(
                    () -> assertInstanceOf(LightThemeFactory.class, lightFactory),
                    () -> assertInstanceOf(DarkThemeFactory.class, darkFactory),
                    () -> assertInstanceOf(HighContrastThemeFactory.class, highContrastFactory)
            );
        }

        @Test
        @DisplayName("Null tema composition sınırında açık mesajla reddedilir")
        void nullThemeIsRejectedAtTheCompositionBoundary() {
            // Arrange & Act
            NullPointerException error = assertThrows(
                    NullPointerException.class,
                    () -> GuiFactoryProvider.forTheme(null)
            );

            // Assert
            assertEquals("theme cannot be null", error.getMessage());
        }
    }

    @Nested
    @DisplayName("Concrete factory aynı aileden product'lar üretir")
    class CompatibleProductFamilies {

        @Test
        @DisplayName("Light factory açık tema button ve checkbox üretir")
        void lightFactoryCreatesTheCompleteLightFamily() {
            // Arrange
            GuiFactory factory = new LightThemeFactory();

            // Act
            Button button = factory.createButton();
            Checkbox checkbox = factory.createCheckbox();

            // Assert
            assertAll(
                    () -> assertInstanceOf(LightButton.class, button),
                    () -> assertEquals("Açık tema butonu", button.render()),
                    () -> assertInstanceOf(LightCheckbox.class, checkbox),
                    () -> assertEquals("Açık tema checkbox", checkbox.render())
            );
        }

        @Test
        @DisplayName("Dark factory koyu tema button ve checkbox üretir")
        void darkFactoryCreatesTheCompleteDarkFamily() {
            // Arrange
            GuiFactory factory = new DarkThemeFactory();

            // Act
            Button button = factory.createButton();
            Checkbox checkbox = factory.createCheckbox();

            // Assert
            assertAll(
                    () -> assertInstanceOf(DarkButton.class, button),
                    () -> assertEquals("Koyu tema butonu", button.render()),
                    () -> assertInstanceOf(DarkCheckbox.class, checkbox),
                    () -> assertEquals("Koyu tema checkbox", checkbox.render())
            );
        }

        @Test
        @DisplayName("High contrast factory erişilebilir button ve checkbox ailesini eksiksiz üretir")
        void highContrastFactoryCreatesTheAccessibleProductFamily() {
            // Arrange
            GuiFactory factory = new HighContrastThemeFactory();

            // Act
            Button button = factory.createButton();
            Checkbox checkbox = factory.createCheckbox();

            // Assert
            assertAll(
                    () -> assertInstanceOf(HighContrastButton.class, button),
                    () -> assertEquals("Yüksek kontrastlı, kalın çerçeveli buton", button.render()),
                    () -> assertInstanceOf(HighContrastCheckbox.class, checkbox),
                    () -> assertEquals(
                            "Yüksek kontrastlı, büyük işaretli checkbox",
                            checkbox.render()
                    )
            );
        }
    }

    @Nested
    @DisplayName("Client yalnız abstract factory sözleşmesini bilir")
    class ClientComposition {

        @Test
        @DisplayName("UiScreen seçilen ailenin iki ürününü birlikte çizer")
        void screenDrawsProductsFromTheSelectedFamily() {
            // Arrange
            UiScreen lightScreen = new UiScreen(GuiFactoryProvider.forTheme(Theme.LIGHT));
            UiScreen darkScreen = new UiScreen(GuiFactoryProvider.forTheme(Theme.DARK));

            // Act
            String lightOutput = lightScreen.draw();
            String darkOutput = darkScreen.draw();

            // Assert
            assertAll(
                    () -> assertEquals("Açık tema butonu + Açık tema checkbox", lightOutput),
                    () -> assertEquals("Koyu tema butonu + Koyu tema checkbox", darkOutput)
            );
        }

        @Test
        @DisplayName("Custom factory client değiştirilmeden yeni bir ürün ailesi sağlayabilir")
        void customFactoryCanSupplyANewFamilyWithoutChangingTheClient() {
            // Arrange
            CountingFactory factory = new CountingFactory();

            // Act
            UiScreen screen = new UiScreen(factory);
            String output = screen.draw();

            // Assert
            assertAll(
                    () -> assertEquals("Test button + Test checkbox", output),
                    () -> assertEquals(1, factory.buttonCreationCount),
                    () -> assertEquals(1, factory.checkboxCreationCount)
            );
        }
    }

    @Nested
    @DisplayName("Composition sınırı eksik aileleri erken reddeder")
    class InvalidFamilyProtection {

        @Test
        @DisplayName("Null factory açık hata mesajıyla reddedilir")
        void nullFactoryIsRejected() {
            // Arrange & Act
            NullPointerException error = assertThrows(
                    NullPointerException.class,
                    () -> new UiScreen(null)
            );

            // Assert
            assertEquals("factory cannot be null", error.getMessage());
        }

        @Test
        @DisplayName("Factory ürünlerden birini üretmezse ekran yarım kurulmaz")
        void incompleteProductFamilyIsRejected() {
            // Arrange
            GuiFactory incompleteFactory = new GuiFactory() {
                @Override
                public Button createButton() {
                    return () -> "Valid button";
                }

                @Override
                public Checkbox createCheckbox() {
                    return null;
                }
            };

            // Act
            NullPointerException error = assertThrows(
                    NullPointerException.class,
                    () -> new UiScreen(incompleteFactory)
            );

            // Assert
            assertEquals("factory must create a checkbox", error.getMessage());
        }
    }

    private static final class CountingFactory implements GuiFactory {
        private int buttonCreationCount;
        private int checkboxCreationCount;

        @Override
        public Button createButton() {
            buttonCreationCount++;
            return () -> "Test button";
        }

        @Override
        public Checkbox createCheckbox() {
            checkboxCreationCount++;
            return () -> "Test checkbox";
        }
    }
}
