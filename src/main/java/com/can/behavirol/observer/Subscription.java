package com.can.behavirol.observer;

/**
 * Aboneliğin sahibi tarafından güvenle kapatılabilen yaşam döngüsü tutamacı.
 * Her handle yalnız kendi registration referansını serbest bırakır.
 */
@FunctionalInterface
public interface Subscription extends AutoCloseable {

    @Override
    void close();
}
