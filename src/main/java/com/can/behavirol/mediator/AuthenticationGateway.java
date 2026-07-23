package com.can.behavirol.mediator;

/**
 * Mediator'ın UI koordinasyonu ile gerçek kimlik servisinin yan etkisini ayırır.
 */
public interface AuthenticationGateway {

    String login(String username, String password);

    String register(String username, String password, String email);
}
