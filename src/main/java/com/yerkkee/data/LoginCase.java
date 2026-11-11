package com.yerkkee.data;

/**
 * One row of a negative-login DataProvider.
 *
 * <p>{@link #toString()} returns only the description on purpose: Allure records DataProvider
 * arguments as test parameters via toString(), so the password never reaches the report.
 */
public record LoginCase(String description, String username, String password, String expectedError) {

    @Override
    public String toString() {
        return description;
    }
}
