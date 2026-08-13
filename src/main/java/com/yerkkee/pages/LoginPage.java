package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginPage extends BasePage {

    private final Locator usernameField;
    private final Locator passwordField;
    private final Locator loginButton;

    public LoginPage(Page page) {
        super(page);
        usernameField = page.getByTestId("username");
        passwordField = page.getByTestId("password");
        loginButton = page.getByTestId("login-button");
    }

    public void enterUsername(String username) {
        log.info("Entering username: {}", username);
        usernameField.fill(username);
    }

    public void enterPassword(String password) {
        log.info("Entering password: [MASKED]");
        passwordField.fill(password);
    }

    public void clickLogin() {
        log.info("Clicking Login button");
        loginButton.click();
    }

    public void loginAs(String username, String password) {
        log.info("Logging in as user: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
}