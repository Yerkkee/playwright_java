package com.yerkkee.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Options of the "Sort products" dropdown: {@code value} is the &lt;option value&gt;,
 * {@code label} is what the user sees.
 */
@Getter
@RequiredArgsConstructor
public enum SortOption {
    NAME_A_TO_Z("az", "Name (A to Z)"),
    NAME_Z_TO_A("za", "Name (Z to A)"),
    PRICE_LOW_TO_HIGH("lohi", "Price (low to high)"),
    PRICE_HIGH_TO_LOW("hilo", "Price (high to low)");

    private final String value;
    private final String label;

    @Override
    public String toString() {
        return label;
    }
}
