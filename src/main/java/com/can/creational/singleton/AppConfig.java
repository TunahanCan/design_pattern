package com.can.creational.singleton;

import java.util.Objects;

/**
 * Uygulama genelinde tek bir konfigürasyon nesnesi sağlar.
 *
 * Double-Checked Locking + volatile kombinasyonu ile thread-safe lazy initialization uygular.
 */
public final class AppConfig {

    private static volatile AppConfig instance;

    private final String apiBaseUrl;
    private final int connectionTimeoutMs;

    private AppConfig() {
        this.apiBaseUrl = "https://api.example.com";
        this.connectionTimeoutMs = 3_000;
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public String describe() {
        return "AppConfig{"
                + "apiBaseUrl='" + apiBaseUrl + '\''
                + ", connectionTimeoutMs=" + connectionTimeoutMs
                + '}';
    }

    /**
     * Test izolasyonu için gerekli olmadıkça kullanılmamalıdır.
     */
    static void resetForTests() {
        synchronized (AppConfig.class) {
            instance = null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AppConfig that)) {
            return false;
        }
        return connectionTimeoutMs == that.connectionTimeoutMs
                && Objects.equals(apiBaseUrl, that.apiBaseUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiBaseUrl, connectionTimeoutMs);
    }
}
