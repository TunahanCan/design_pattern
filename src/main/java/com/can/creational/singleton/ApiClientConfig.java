package com.can.creational.singleton;

/**
 * ApiClient'ın global AppConfig sınıfına değil, ihtiyaç duyduğu dar sözleşmeye
 * bağımlı kalmasını sağlar.
 */
public interface ApiClientConfig {
    String getApiBaseUrl();

    int getConnectionTimeoutMs();
}
