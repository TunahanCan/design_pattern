package com.can.behavirol.chainofresponsibility;

import java.util.Objects;

/**
 * Sipariş işleme zincirinin kanonik sırasını yeniden kullanılabilir bir
 * composition factory içinde toplar.
 *
 * <p>En dış composition root concrete repository, rate-limit ve cache
 * bağımlılıklarını seçer; bu factory ise handler topolojisini tek yerde korur.</p>
 */
public final class OrderRequestChainFactory {

    private OrderRequestChainFactory() {
    }

    public static OrderRequestHandler create(UserRepository userRepository,
                                             LoginAttemptService loginAttemptService,
                                             RequestCache cache) {
        Objects.requireNonNull(userRepository, "userRepository cannot be null");
        Objects.requireNonNull(loginAttemptService, "loginAttemptService cannot be null");
        Objects.requireNonNull(cache, "cache cannot be null");

        OrderRequestHandler validation = new RequestValidationHandler();
        OrderRequestHandler bruteForce = new BruteForceProtectionHandler(loginAttemptService);
        OrderRequestHandler authentication = new AuthenticationHandler(
                userRepository,
                loginAttemptService
        );
        OrderRequestHandler sanitization = new DataSanitizationHandler();
        OrderRequestHandler authorization = new AuthorizationHandler();
        OrderRequestHandler cacheHandler = new CacheHandler(cache);
        OrderRequestHandler orderProcessing = new OrderProcessingHandler();

        validation
                .setNext(bruteForce)
                .setNext(authentication)
                .setNext(sanitization)
                .setNext(authorization)
                .setNext(cacheHandler)
                .setNext(orderProcessing);

        return validation;
    }
}
