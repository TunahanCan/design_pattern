package com.can.structural.composite;

import java.math.BigDecimal;

public interface OrderComponent {

    double getPrice();

    default BigDecimal getPriceAmount() {
        return BigDecimal.valueOf(getPrice());
    }

    String getName();
}
