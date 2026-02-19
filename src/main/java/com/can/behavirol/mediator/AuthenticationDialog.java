package com.can.behavirol.mediator;

public class AuthenticationDialog implements Mediator {
    private final Label title;
    private final Label resultMessage;
    private final Checkbox loginModeCheckbox;
    private final Textbox username;
    private final Textbox password;
    private final Textbox email;
    private final Button okButton;

    public AuthenticationDialog() {
        title = new Label(this, "Giriş Yap");
        resultMessage = new Label(this, "");
        loginModeCheckbox = new Checkbox(this);
        username = new Textbox(this);
        password = new Textbox(this);
        email = new Textbox(this);
        okButton = new Button(this, "Tamam");

        loginModeCheckbox.setChecked(true);
    }

    @Override
    public void notify(Component sender, String event) {
        if (sender == loginModeCheckbox && "check".equals(event)) {
            handleModeSwitch();
        }

        if (sender == okButton && "click".equals(event)) {
            handleSubmit();
        }
    }

    private void handleModeSwitch() {
        if (loginModeCheckbox.isChecked()) {
            title.setText("Giriş Yap");
            email.setVisible(false);
            resultMessage.setText("Giriş ekranı aktif.");
            return;
        }

        title.setText("Kayıt Ol");
        email.setVisible(true);
        resultMessage.setText("Kayıt ekranı aktif.");
    }

    private void handleSubmit() {
        if (loginModeCheckbox.isChecked()) {
            if (username.getText().isBlank() || password.getText().isBlank()) {
                resultMessage.setText("Giriş için kullanıcı adı ve parola zorunlu.");
                return;
            }

            resultMessage.setText("Kullanıcı giriş yaptı: " + username.getText());
            return;
        }

        if (username.getText().isBlank() || password.getText().isBlank() || email.getText().isBlank()) {
            resultMessage.setText("Kayıt için kullanıcı adı, parola ve e-posta zorunlu.");
            return;
        }

        resultMessage.setText("Yeni kullanıcı kaydedildi: " + username.getText());
    }

    public Checkbox getLoginModeCheckbox() {
        return loginModeCheckbox;
    }

    public Textbox getUsername() {
        return username;
    }

    public Textbox getPassword() {
        return password;
    }

    public Textbox getEmail() {
        return email;
    }

    public Button getOkButton() {
        return okButton;
    }

    public String getTitle() {
        return title.getText();
    }

    public String getResultMessage() {
        return resultMessage.getText();
    }
}
