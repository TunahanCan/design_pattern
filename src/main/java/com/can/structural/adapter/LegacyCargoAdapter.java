package com.can.structural.adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class LegacyCargoAdapter implements ShippingService {

    private static final BigDecimal KURUS_PER_LIRA = BigDecimal.valueOf(100);

    private final LegacyCargoApi legacyCargoApi;

    public LegacyCargoAdapter(LegacyCargoApi legacyCargoApi) {
        this.legacyCargoApi = Objects.requireNonNull(
            legacyCargoApi,
            "legacyCargoApi cannot be null"
        );
    }

    @Override
    public DeliveryQuote quote(Parcel parcel) {
        Objects.requireNonNull(parcel, "parcel cannot be null");

        double weightKilograms = parcel.weightGrams() / 1_000.0;
        LegacyCargoApi.LegacyQuote legacyQuote = legacyCargoApi.calculate(
            parcel.destinationPostalCode(),
            weightKilograms
        );

        BigDecimal priceTry = BigDecimal.valueOf(legacyQuote.feeInKurus())
            .divide(KURUS_PER_LIRA, 2, RoundingMode.UNNECESSARY);
        int estimatedDays = Math.ceilDiv(legacyQuote.estimatedHours(), 24);

        return new DeliveryQuote("Legacy Cargo", priceTry, estimatedDays);
    }
}
