package com.can.behavirol.mediator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Mediator — kimlik doğrulama diyaloğu")
class MediatorPatternDemoTest {

    @Nested
    @DisplayName("Diyalog ilk oluşturulduğunda")
    class InitialState {

        @Test
        @DisplayName("login modu ve ona ait bileşen görünürlüğü hazırlanır")
        void loginModeIsPrepared() {
            // Arrange
            AuthenticationDialog dialog = new AuthenticationDialog();

            // Act
            String title = dialog.getTitle();

            // Assert
            assertAll(
                    () -> assertEquals("Giriş Yap", title),
                    () -> assertTrue(dialog.getLoginModeCheckbox().isChecked()),
                    () -> assertFalse(dialog.getEmail().isVisible()),
                    () -> assertEquals("Giriş ekranı aktif.", dialog.getResultMessage())
            );
        }
    }

    @Nested
    @DisplayName("Checkbox mod değişikliği bildirdiğinde")
    class ModeSwitching {

        @Test
        @DisplayName("register modu başlığı, email görünürlüğünü ve bilgi mesajını değiştirir")
        void switchToRegisterMode() {
            // Arrange
            AuthenticationDialog dialog = new AuthenticationDialog();

            // Act
            dialog.getLoginModeCheckbox().setChecked(false);

            // Assert
            assertAll(
                    () -> assertEquals("Kayıt Ol", dialog.getTitle()),
                    () -> assertTrue(dialog.getEmail().isVisible()),
                    () -> assertEquals("Kayıt ekranı aktif.", dialog.getResultMessage())
            );
        }

        @Test
        @DisplayName("register modundan login moduna geri dönülebilir")
        void switchBackToLoginMode() {
            // Arrange
            AuthenticationDialog dialog = new AuthenticationDialog();
            dialog.getLoginModeCheckbox().setChecked(false);

            // Act
            dialog.getLoginModeCheckbox().setChecked(true);

            // Assert
            assertAll(
                    () -> assertEquals("Giriş Yap", dialog.getTitle()),
                    () -> assertFalse(dialog.getEmail().isVisible()),
                    () -> assertEquals("Giriş ekranı aktif.", dialog.getResultMessage())
            );
        }
    }

    @Nested
    @DisplayName("Login formu gönderildiğinde")
    class LoginSubmission {

        @Test
        @DisplayName("zorunlu alanlar boşsa doğrulama mesajı üretilir")
        void blankCredentialsAreRejected() {
            // Arrange
            AuthenticationDialog dialog = new AuthenticationDialog();

            // Act
            dialog.getOkButton().click();

            // Assert
            assertEquals(
                    "Giriş için kullanıcı adı ve parola zorunlu.",
                    dialog.getResultMessage()
            );
        }

        @Test
        @DisplayName("kullanıcı adı ve parola doluysa login sonucu üretilir")
        void completeCredentialsAreAccepted() {
            // Arrange
            AuthenticationDialog dialog = new AuthenticationDialog();
            dialog.getUsername().enterText("can");
            dialog.getPassword().enterText("1234");

            // Act
            dialog.getOkButton().click();

            // Assert
            assertEquals("Kullanıcı giriş yaptı: can", dialog.getResultMessage());
        }
    }

    @Nested
    @DisplayName("Register formu gönderildiğinde")
    class RegistrationSubmission {

        @Test
        @DisplayName("email eksikse register doğrulama mesajı üretilir")
        void missingEmailIsRejected() {
            // Arrange
            AuthenticationDialog dialog = new AuthenticationDialog();
            dialog.getLoginModeCheckbox().setChecked(false);
            dialog.getUsername().enterText("ayse");
            dialog.getPassword().enterText("qwerty");

            // Act
            dialog.getOkButton().click();

            // Assert
            assertEquals(
                    "Kayıt için kullanıcı adı, parola ve e-posta zorunlu.",
                    dialog.getResultMessage()
            );
        }

        @Test
        @DisplayName("üç alan da doluysa kayıt sonucu üretilir")
        void completeRegistrationIsAccepted() {
            // Arrange
            AuthenticationDialog dialog = new AuthenticationDialog();
            dialog.getLoginModeCheckbox().setChecked(false);
            dialog.getUsername().enterText("ayse");
            dialog.getPassword().enterText("qwerty");
            dialog.getEmail().enterText("ayse@example.com");

            // Act
            dialog.getOkButton().click();

            // Assert
            assertAll(
                    () -> assertEquals("Yeni kullanıcı kaydedildi: ayse", dialog.getResultMessage()),
                    () -> assertTrue(dialog.getEmail().isVisible())
            );
        }
    }

    @Nested
    @DisplayName("Form tamamlandığında dış servis çağrısı gerektiğinde")
    class GatewayBoundary {

        @Test
        @DisplayName("login bileşenleri birbirini çağırmak yerine mediator üzerinden gateway'e yönlenir")
        void loginIsDelegatedThroughTheMediator() {
            // Arrange
            RecordingAuthenticationGateway gateway = new RecordingAuthenticationGateway();
            AuthenticationDialog dialog = new AuthenticationDialog(gateway);
            dialog.getUsername().enterText("can");
            dialog.getPassword().enterText("secret");

            // Act
            dialog.getOkButton().click();

            // Assert
            assertAll(
                    () -> assertEquals("login-ok", dialog.getResultMessage()),
                    () -> assertEquals("can", gateway.loginUsername),
                    () -> assertEquals("secret", gateway.loginPassword),
                    () -> assertEquals(1, gateway.loginCalls)
            );
        }

        @Test
        @DisplayName("eksik alan varsa gateway çağrılmadan UI doğrulaması yapılır")
        void invalidRegistrationDoesNotCrossTheGatewayBoundary() {
            // Arrange
            RecordingAuthenticationGateway gateway = new RecordingAuthenticationGateway();
            AuthenticationDialog dialog = new AuthenticationDialog(gateway);
            dialog.getLoginModeCheckbox().setChecked(false);
            dialog.getUsername().enterText("ayse");
            dialog.getPassword().enterText("secret");

            // Act
            dialog.getOkButton().click();

            // Assert
            assertAll(
                    () -> assertEquals(0, gateway.registrationCalls),
                    () -> assertEquals(
                            "Kayıt için kullanıcı adı, parola ve e-posta zorunlu.",
                            dialog.getResultMessage()
                    )
            );
        }
    }

    private static final class RecordingAuthenticationGateway implements AuthenticationGateway {
        private int loginCalls;
        private int registrationCalls;
        private String loginUsername;
        private String loginPassword;

        @Override
        public String login(String username, String password) {
            loginCalls++;
            loginUsername = username;
            loginPassword = password;
            return "login-ok";
        }

        @Override
        public String register(String username, String password, String email) {
            registrationCalls++;
            return "register-ok";
        }
    }
}
