package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.yerkkee.data.TestUser;
import io.qameta.allure.Allure;
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

    @Step("Open login page")
    public LoginPage open() {
        openBaseUrl();
        return this;
    }

    @Step("Enter username: {username}")
    public void enterUsername(String username) {
        log.info("Entering username: {}", username);
        usernameField.fill(username);
    }

    /**
     * Deliberately NOT annotated with @Step: Allure writes every @Step argument into
     * allure-results/*.json, and even {@code @Param(mode = MASKED)} only hides it in the UI -
     * the raw value stays in the JSON (which CI uploads as an artifact).
     * A lambda step is still shown in the report but records no arguments.
     */
    public void enterPassword(String password) {
        Allure.step("Enter password: ******", () -> {
            log.info("Entering password: [MASKED]");
            passwordField.fill(password);
        });
    }

    @Step("Click Login button")
    public void clickLogin() {
        log.info("Clicking Login button");
        loginButton.click();
    }

    @Step("Login as {user.username}")
    public InventoryPage loginAs(TestUser user) {
        log.info("Logging in as user: {}", user.getUsername());
        enterUsername(user.getUsername());
        enterPassword(user.getPassword());
        clickLogin();
        return new InventoryPage(page);
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