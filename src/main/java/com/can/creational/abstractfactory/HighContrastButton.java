package com.can.creational.abstractfactory;

/**
 * Düşük görme yetisine sahip kullanıcılar için yüksek kontrastlı button varyantı.
 */
public final class HighContrastButton implements Button {

    @Override
    public String render() {
        return "Yüksek kontrastlı, kalın çerçeveli buton";
    }
}
