package com.can.behavirol.mediator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MediatorPatternDemoTest {

    @Test
    void shouldSwitchBetweenLoginAndRegisterUsingMediator() {
        AuthenticationDialog dialog = new AuthenticationDialog();

        assertEquals("Giriş Yap", dialog.getTitle());
        assertFalse(dialog.getEmail().isVisible());

        dialog.getLoginModeCheckbox().setChecked(false);

        assertEquals("Kayıt Ol", dialog.getTitle());
        assertTrue(dialog.getEmail().isVisible());
    }

    @Test
    void shouldValidateAndHandleSubmitByMode() {
        AuthenticationDialog dialog = new AuthenticationDialog();

        dialog.getOkButton().click();
        assertEquals("Giriş için kullanıcı adı ve parola zorunlu.", dialog.getResultMessage());

        dialog.getUsername().enterText("can");
        dialog.getPassword().enterText("1234");
        dialog.getOkButton().click();
        assertEquals("Kullanıcı giriş yaptı: can", dialog.getResultMessage());

        dialog.getLoginModeCheckbox().setChecked(false);
        dialog.getUsername().enterText("ayse");
        dialog.getPassword().enterText("qwerty");
        dialog.getEmail().enterText("ayse@example.com");
        dialog.getOkButton().click();
        assertEquals("Yeni kullanıcı kaydedildi: ayse", dialog.getResultMessage());
    }
}
