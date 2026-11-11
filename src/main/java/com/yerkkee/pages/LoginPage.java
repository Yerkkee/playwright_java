package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginPage extends BasePage {

    private final Locator usernameField;
    private final Locator passwordField;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        super(page);
        usernameField = page.getByTestId("username");
        passwordField = page.getByTestId("password");
        loginButton = page.getByTestId("login-button");
        errorMessage = page.getByTestId("error");
    }

    @Step("Enter username: {username}")
    public void enterUsername(String username) {
        log.info("Entering username: {}", username);
        usernameField.fill(username);
    }

    @Step("Enter password")
    public void enterPassword(String password) {
        log.info("Entering password: [MASKED]");
        passwordField.fill(password);
    }

    @Step("Click Login button")
    public void clickLogin() {
        log.info("Clicking Login button");
        loginButton.click();
    }

    @Step("Login as {username}")
    public void loginAs(String username, String password) {
        log.info("Logging in as user: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    @Step("Get error message")
    public String getErrorMessage() {
        errorMessage.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        return errorMessage.textContent();
    }

    public boolean isErrorVisible() {
        return errorMessage.isVisible();
    }
}