package com.can.demo.behavioral.chainofresponsibility;

import com.can.behavirol.chainofresponsibility.LoginAttemptService;
import com.can.behavirol.chainofresponsibility.OrderOperation;
import com.can.behavirol.chainofresponsibility.OrderRequest;
import com.can.behavirol.chainofresponsibility.OrderRequestChainFactory;
import com.can.behavirol.chainofresponsibility.OrderRequestHandler;
import com.can.behavirol.chainofresponsibility.RequestCache;
import com.can.behavirol.chainofresponsibility.User;
import com.can.behavirol.chainofresponsibility.UserRepository;

import java.util.Map;

public final class ChainOfResponsibilityDemo {

    private ChainOfResponsibilityDemo() {
    }

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

        OrderRequestHandler chain = OrderRequestChainFactory.create(
                userRepository,
                loginAttemptService,
                cache
        );

        chain.handle(new OrderRequest("can", "1234", "10.0.0.1", OrderOperation.CREATE_ORDER,
                "<script>alert('xss')</script> Yeni sipariş"));

        chain.handle(new OrderRequest("can", "1234", "10.0.0.1", OrderOperation.VIEW_ALL_ORDERS,
                "Tüm siparişleri görüntüle"));

        chain.handle(new OrderRequest("admin", "root", "10.0.0.99", OrderOperation.VIEW_ALL_ORDERS,
                "Tüm siparişleri görüntüle"));

        OrderRequest duplicateRequest = new OrderRequest(
                "admin",
                "root",
                "10.0.0.99",
                OrderOperation.VIEW_ALL_ORDERS,
                "Tüm siparişleri görüntüle"
        );
        chain.handle(duplicateRequest);
        System.out.println(
                "İkinci isteğin sonucu: "
                        + duplicateRequest.getOutcome()
                        + " -> "
                        + duplicateRequest.getOutcomeMessage()
        );

        System.out.println();
    }
}
