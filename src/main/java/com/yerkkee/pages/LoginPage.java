package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.yerkkee.data.TestUser;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Login form (root URL).
 *
 * <p>Password handling: methods that take a raw password are NOT annotated with @Step.
 * Allure writes every @Step argument into allure-results/*.json, and even
 * {@code @Param(mode = MASKED)} only hides it in the UI, not in the JSON that CI uploads.
 * Those methods use {@code Allure.step(name, lambda)} instead: still a report step, no arguments recorded.
 */
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
        navigate("/");
        return waitUntilOpened();
    }

    @Step("Wait until login page is opened")
    public LoginPage waitUntilOpened() {
        waitForPage("/", loginButton);
        return this;
    }

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        log.info("Entering username: {}", username);
        usernameField.fill(username);
        return this;
    }

    /** See class javadoc: no @Step on purpose, the password must not reach allure-results. */
    public LoginPage enterPassword(String password) {
        Allure.step("Enter password: ******", () -> {
            log.info("Entering password: [MASKED]");
            passwordField.fill(password);
        });
        return this;
    }

    @Step("Click Login button")
    public void clickLogin() {
        log.info("Clicking Login button");
        loginButton.click();
    }

    /** Happy path: logs in and waits for the Products page. */
    @Step("Login as {user.username}")
    public InventoryPage loginAs(TestUser user) {
        enterUsername(user.getUsername());
        enterPassword(user.getPassword());
        clickLogin();
        return new InventoryPage(page).waitUntilOpened();
    }

    /** Negative path: submits the form and stays on the login page (error is expected). */
    @Step("Login as {user.username} expecting an error")
    public LoginPage loginExpectingError(TestUser user) {
        return loginExpectingError(user.getUsername(), user.getPassword());
    }

    /** Negative path with arbitrary credentials. No @Step: see class javadoc. */
    public LoginPage loginExpectingError(String username, String password) {
        Allure.step("Login as '" + username + "' expecting an error", () -> {
            enterUsername(username);
            enterPassword(password);
            clickLogin();
        });
        return this;
    }

    /** Waits for the error banner and returns its text. */
    @Step("Get error message")
    public String getErrorMessage() {
        assertThat(errorMessage).isVisible();
        return errorMessage.innerText().trim();
    }
}
