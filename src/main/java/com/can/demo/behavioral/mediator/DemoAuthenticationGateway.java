package com.can.demo.behavioral.mediator;

import com.can.behavirol.mediator.AuthenticationGateway;

/**
 * Örneği dış sisteme bağlamadan çalıştıran in-memory demo adaptörü.
 */
public class DemoAuthenticationGateway implements AuthenticationGateway {

    @Override
    public String login(String username, String password) {
        return "Kullanıcı giriş yaptı: " + username;
    }

    @Override
    public String register(String username, String password, String email) {
        return "Yeni kullanıcı kaydedildi: " + username;
    }
}
