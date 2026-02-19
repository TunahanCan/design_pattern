package com.can.behavirol.chainofresponsibility;

public class OrderProcessingHandler extends BaseOrderRequestHandler {
    @Override
    public boolean handle(OrderRequest request) {
        System.out.println("[OrderSystem] İşlem başarılı -> " + request.getOperation() + " | payload: " + request.getPayload());
        return checkNext(request);
    }
}
