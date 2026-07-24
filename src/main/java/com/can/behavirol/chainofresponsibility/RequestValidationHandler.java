package com.can.behavirol.chainofresponsibility;

/**
 * Zincirin geri kalanının güvenebileceği asgari request invariant'larını kurar.
 */
public class RequestValidationHandler extends BaseOrderRequestHandler {

    @Override
    public boolean handle(OrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }

        String invalidField = firstInvalidField(request);
        if (invalidField != null) {
            request.completeAs(
                    OrderRequestOutcome.REJECTED,
                    "Zorunlu istek alanı eksik: " + invalidField + "."
            );
            return false;
        }

        return checkNext(request);
    }

    private static String firstInvalidField(OrderRequest request) {
        if (isBlank(request.getUsername())) {
            return "username";
        }
        if (isBlank(request.getPassword())) {
            return "password";
        }
        if (isBlank(request.getIpAddress())) {
            return "ipAddress";
        }
        if (request.getOperation() == null) {
            return "operation";
        }
        if (isBlank(request.getPayload())) {
            return "payload";
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
