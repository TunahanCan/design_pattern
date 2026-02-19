package com.can.behavirol.chainofresponsibility;

public interface OrderRequestHandler {
    OrderRequestHandler setNext(OrderRequestHandler next);

    boolean handle(OrderRequest request);
}
