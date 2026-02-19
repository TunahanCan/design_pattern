package com.can.behavirol.chainofresponsibility;

public class CacheHandler extends BaseOrderRequestHandler {
    private final RequestCache cache;

    public CacheHandler(RequestCache cache) {
        this.cache = cache;
    }

    @Override
    public boolean handle(OrderRequest request) {
        String signature = RequestCache.signatureOf(request);
        if (cache.has(signature)) {
            System.out.println("[Cache] Uygun cache bulundu, işlem kısa devre yapıldı.");
            return false;
        }

        boolean passed = checkNext(request);
        if (passed) {
            cache.put(signature);
        }
        return passed;
    }
}
