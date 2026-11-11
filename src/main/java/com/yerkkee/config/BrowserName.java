package com.yerkkee.config;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Browsers supported by Playwright. Parsed from the "browser" config key.
 */
public enum BrowserName {
    CHROMIUM,
    FIREFOX,
    WEBKIT;

    public static BrowserName from(String value) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported browser '" + value + "'. Supported: "
                                + Arrays.stream(values())
                                .map(b -> b.name().toLowerCase())
                                .collect(Collectors.joining(", "))));
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
