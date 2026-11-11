package com.yerkkee.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses money values shown in the UI: "$29.99", "Item total: $39.98", "Tax: $3.20".
 */
public final class Prices {

    private static final Pattern AMOUNT = Pattern.compile("\\$(\\d+(?:\\.\\d{1,2})?)");

    private Prices() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static BigDecimal parse(String text) {
        Matcher matcher = AMOUNT.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No $ amount found in '" + text + "'");
        }
        return new BigDecimal(matcher.group(1));
    }
}
