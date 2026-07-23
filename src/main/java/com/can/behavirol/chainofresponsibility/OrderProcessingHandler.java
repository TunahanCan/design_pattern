package com.can.behavirol.chainofresponsibility;

/**
 * Başarılı request'i sonuçlandıran terminal handler.
 */
public class OrderProcessingHandler extends BaseOrderRequestHandler {
    @Override
    public boolean handle(OrderRequest request) {
        request.completeAs(
                OrderRequestOutcome.PROCESSED,
                "Sipariş isteği başarıyla işlendi."
        );
        System.out.println("[OrderSystem] İşlem başarılı -> " + request.getOperation() + " | payload: " + request.getPayload());
        return true;
    }
}
