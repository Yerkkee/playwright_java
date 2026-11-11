package com.yerkkee.data;

/**
 * Data for the "Checkout: Your Information" form.
 * A record gives immutability, equals/hashCode and a readable toString for Allure step titles.
 */
public record CheckoutInfo(String firstName, String lastName, String postalCode) {

    public static CheckoutInfo valid() {
        return new CheckoutInfo("John", "Doe", "10001");
    }

    public CheckoutInfo withFirstName(String value) {
        return new CheckoutInfo(value, lastName, postalCode);
    }

    public CheckoutInfo withLastName(String value) {
        return new CheckoutInfo(firstName, value, postalCode);
    }

    public CheckoutInfo withPostalCode(String value) {
        return new CheckoutInfo(firstName, lastName, value);
    }
}
