package com.can.behavirol.chainofresponsibility;

import java.util.HashSet;
import java.util.Set;

public class RequestCache {
    private final Set<String> signatures = new HashSet<>();

    public boolean has(String signature) {
        return signatures.contains(signature);
    }

    public void put(String signature) {
        signatures.add(signature);
    }

    public static String signatureOf(OrderRequest request) {
        return request.getUsername() + "|" + request.getOperation() + "|" + request.getPayload();
    }
}
