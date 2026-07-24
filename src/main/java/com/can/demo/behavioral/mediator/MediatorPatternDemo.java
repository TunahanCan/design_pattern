package com.can.demo.behavioral.mediator;

import com.can.behavirol.mediator.AuthenticationDialog;

public final class MediatorPatternDemo {

    private MediatorPatternDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("4) Mediator");

        AuthenticationDialog dialog = new AuthenticationDialog(
                new DemoAuthenticationGateway()
        );

        dialog.getUsername().enterText("can");
        dialog.getPassword().enterText("1234");
        dialog.getOkButton().click();
        System.out.println(dialog.getResultMessage());

        dialog.getLoginModeCheckbox().setChecked(false);
        dialog.getUsername().enterText("ayse");
        dialog.getPassword().enterText("qwerty");
        dialog.getEmail().enterText("ayse@example.com");
        dialog.getOkButton().click();
        System.out.println(dialog.getResultMessage());
        System.out.println();
    }
}
