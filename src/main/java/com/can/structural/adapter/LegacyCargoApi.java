package com.can.structural.adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LegacyCargoApi {

    private static final BigDecimal WEIGHT_FEE_IN_KURUS_PER_KILOGRAM =
        BigDecimal.valueOf(1_250);

    public LegacyQuote calculate(String postalCode, double weightKilograms) {
        long baseFeeInKurus = postalCode.startsWith("34") ? 5_000 : 6_500;
        long weightFeeInKurus = BigDecimal.valueOf(weightKilograms)
            .multiply(WEIGHT_FEE_IN_KURUS_PER_KILOGRAM)
            .setScale(0, RoundingMode.CEILING)
            .longValueExact();
        int estimatedHours = postalCode.startsWith("34") ? 24 : 50;
        return new LegacyQuote(baseFeeInKurus + weightFeeInKurus, estimatedHours);
    }

    public record LegacyQuote(long feeInKurus, int estimatedHours) {
    }
}
