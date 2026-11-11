package com.yerkkee.data;

/**
 * Texts the application is expected to show. Kept in one place so a copy change
 * in the app means one edit here, not a hunt through tests.
 */
public final class Messages {

    // Login
    public static final String LOCKED_OUT = "Epic sadface: Sorry, this user has been locked out.";
    public static final String INVALID_CREDENTIALS =
            "Epic sadface: Username and password do not match any user in this service";
    public static final String USERNAME_REQUIRED = "Epic sadface: Username is required";
    public static final String PASSWORD_REQUIRED = "Epic sadface: Password is required";
    public static final String INVENTORY_REQUIRES_LOGIN =
            "Epic sadface: You can only access '/inventory.html' when you are logged in.";

    // Checkout
    public static final String FIRST_NAME_REQUIRED = "Error: First Name is required";
    public static final String LAST_NAME_REQUIRED = "Error: Last Name is required";
    public static final String POSTAL_CODE_REQUIRED = "Error: Postal Code is required";
    public static final String ORDER_COMPLETE = "Thank you for your order!";

    // Page titles
    public static final String PRODUCTS_TITLE = "Products";

    private Messages() {
        throw new UnsupportedOperationException("Constants class");
    }
}
