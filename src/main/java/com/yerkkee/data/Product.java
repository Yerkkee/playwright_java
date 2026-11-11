package com.yerkkee.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * SauceDemo catalog. Prices use BigDecimal: money must never be summed as double
 * (0.1 + 0.2 != 0.3), otherwise "Item total" checks become flaky by a cent.
 */
@Getter
@RequiredArgsConstructor
public enum Product {
    BACKPACK("Sauce Labs Backpack", new BigDecimal("29.99")),
    BIKE_LIGHT("Sauce Labs Bike Light", new BigDecimal("9.99")),
    BOLT_T_SHIRT("Sauce Labs Bolt T-Shirt", new BigDecimal("15.99")),
    FLEECE_JACKET("Sauce Labs Fleece Jacket", new BigDecimal("49.99")),
    ONESIE("Sauce Labs Onesie", new BigDecimal("7.99")),
    RED_T_SHIRT("Test.allTheThings() T-Shirt (Red)", new BigDecimal("15.99"));

    private final String displayName;
    private final BigDecimal price;

    @Override
    public String toString() {
        return displayName;
    }
}
