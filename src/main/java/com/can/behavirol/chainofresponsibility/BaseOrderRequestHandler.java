package com.can.behavirol.chainofresponsibility;

public abstract class BaseOrderRequestHandler implements OrderRequestHandler {

    private OrderRequestHandler next;

    @Override
    public OrderRequestHandler setNext(OrderRequestHandler next) {
        this.next = next;
        return next;
    }

    protected boolean checkNext(OrderRequest request) {
        if (next == null) {
            return true;
        }
        return next.handle(request);
    }
}
