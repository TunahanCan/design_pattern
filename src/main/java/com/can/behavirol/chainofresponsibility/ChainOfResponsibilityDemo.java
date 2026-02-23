package com.can.behavirol.chainofresponsibility;

import java.util.Map;

public class ChainOfResponsibilityDemo
{

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("1) Chain of Responsibility");

        UserRepository userRepository = new UserRepository(Map.of(
                "can", new User("can", "1234", false),
                "admin", new User("admin", "root", true)
        ));

        LoginAttemptService loginAttemptService = new LoginAttemptService(3);
        RequestCache cache = new RequestCache();

        OrderRequestHandler chain = buildChain(userRepository, loginAttemptService, cache);

        chain.handle(new OrderRequest("can", "1234", "10.0.0.1", OrderOperation.CREATE_ORDER,
                "<script>alert('xss')</script> Yeni sipariş"));

        chain.handle(new OrderRequest("can", "1234", "10.0.0.1", OrderOperation.VIEW_ALL_ORDERS,
                "Tüm siparişleri görüntüle"));

        chain.handle(new OrderRequest("admin", "root", "10.0.0.99", OrderOperation.VIEW_ALL_ORDERS,
                "Tüm siparişleri görüntüle"));

        chain.handle(new OrderRequest("admin", "root", "10.0.0.99", OrderOperation.VIEW_ALL_ORDERS,
                "Tüm siparişleri görüntüle"));

        System.out.println();
    }

    static OrderRequestHandler buildChain(UserRepository userRepository,
                                          LoginAttemptService loginAttemptService,
                                          RequestCache cache) {
        OrderRequestHandler bruteForce = new BruteForceProtectionHandler(loginAttemptService);
        OrderRequestHandler authentication = new AuthenticationHandler(userRepository, loginAttemptService);
        OrderRequestHandler sanitization = new DataSanitizationHandler();
        OrderRequestHandler authorization = new AuthorizationHandler();
        OrderRequestHandler cacheHandler = new CacheHandler(cache);
        OrderRequestHandler orderProcessing = new OrderProcessingHandler();

        bruteForce
                .setNext(authentication)
                .setNext(sanitization)
                .setNext(authorization)
                .setNext(cacheHandler)
                .setNext(orderProcessing);

        return bruteForce;
    }
}
