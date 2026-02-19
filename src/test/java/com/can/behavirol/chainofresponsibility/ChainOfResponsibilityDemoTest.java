package com.can.behavirol.chainofresponsibility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ChainOfResponsibilityDemoTest {

    @Test
    void shouldAllowCreateOrderForAuthenticatedUser() {
        OrderRequestHandler chain = createChain();

        OrderRequest request = new OrderRequest(
                "can",
                "1234",
                "10.0.0.1",
                OrderOperation.CREATE_ORDER,
                "<b>Yeni Sipariş</b>"
        );

        boolean result = chain.handle(request);

        assertTrue(result);
        assertFalse(request.getPayload().contains("<"));
        assertFalse(request.getPayload().contains(">"));
    }

    @Test
    void shouldRejectViewingAllOrdersForNonAdminUser() {
        OrderRequestHandler chain = createChain();

        boolean result = chain.handle(new OrderRequest(
                "can",
                "1234",
                "10.0.0.1",
                OrderOperation.VIEW_ALL_ORDERS,
                "Hepsini getir"
        ));

        assertFalse(result);
    }

    @Test
    void shouldShortCircuitWhenSameRequestHitsCache() {
        OrderRequestHandler chain = createChain();

        OrderRequest first = new OrderRequest(
                "admin",
                "root",
                "10.0.0.2",
                OrderOperation.VIEW_ALL_ORDERS,
                "Özet"
        );

        OrderRequest second = new OrderRequest(
                "admin",
                "root",
                "10.0.0.2",
                OrderOperation.VIEW_ALL_ORDERS,
                "Özet"
        );

        assertTrue(chain.handle(first));
        assertFalse(chain.handle(second));
    }

    private static OrderRequestHandler createChain() {
        UserRepository userRepository = new UserRepository(Map.of(
                "can", new User("can", "1234", false),
                "admin", new User("admin", "root", true)
        ));

        LoginAttemptService loginAttemptService = new LoginAttemptService(3);
        RequestCache cache = new RequestCache();

        return ChainOfResponsibilityDemo.buildChain(userRepository, loginAttemptService, cache);
    }
}
