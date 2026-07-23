package com.can.creational.abstractfactory;

/**
 * Yüksek kontrast ailesinin checkbox varyantı.
 */
public final class HighContrastCheckbox implements Checkbox {

    @Override
    public String render() {
        return "Yüksek kontrastlı, büyük işaretli checkbox";
    }
}
