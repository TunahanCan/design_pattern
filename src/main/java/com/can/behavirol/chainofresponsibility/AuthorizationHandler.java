package com.can.behavirol.chainofresponsibility;

public class AuthorizationHandler extends BaseOrderRequestHandler {
    @Override
    public boolean handle(OrderRequest request) {
        if (request.getOperation() == OrderOperation.VIEW_ALL_ORDERS
                && (request.getAuthenticatedUser() == null || !request.getAuthenticatedUser().admin())) {
            request.completeAs(
                    OrderRequestOutcome.REJECTED,
                    "Bu işlem için admin yetkisi gerekli."
            );
            System.out.println("[Authorization] Bu işlem için admin yetkisi gerekli.");
            return false;
        }
        return checkNext(request);
    }
}
