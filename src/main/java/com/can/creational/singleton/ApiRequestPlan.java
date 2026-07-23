package com.can.creational.singleton;

/**
 * Ağ çağrısı yapmadan önce üretilebilen, test edilebilir istek planı.
 */
public record ApiRequestPlan(String method, String url, int timeoutMs) {

    public String describe() {
        return method + " " + url + " (timeout=" + timeoutMs + "ms)";
    }
}
