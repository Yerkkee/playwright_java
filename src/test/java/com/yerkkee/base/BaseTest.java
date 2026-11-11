package com.yerkkee.base;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Cookie;
import com.yerkkee.config.TestConfig;
import com.yerkkee.data.TestUser;
import com.yerkkee.pages.InventoryPage;
import com.yerkkee.pages.LoginPage;
import io.qameta.allure.Step;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.net.URI;
import java.util.List;

/**
 * Base class for all UI tests.
 *
 * <p>Playwright objects live in {@link PlaywrightManager} (ThreadLocal), so tests can run with
 * {@code parallel="methods"}. Each test gets a fresh BrowserContext + Page.
 *
 * <p>{@code alwaysRun = true} is required: without it TestNG skips these methods
 * when a suite filters by groups (e.g. smoke.xml), and tests would get no browser.
 */
public abstract class BaseTest {

    private static final String SESSION_COOKIE = "session-username";

    @BeforeMethod(alwaysRun = true)
    public void startBrowserSession() {
        PlaywrightManager.startSession();
    }

    /**
     * Runs after the listener: on failure the listener has already saved trace/video
     * and closed the context, so this call is a no-op. On success it discards trace and video.
     */
    @AfterMethod(alwaysRun = true)
    public void closeBrowserSession() {
        PlaywrightManager.closeSession(false);
    }

    @AfterSuite(alwaysRun = true)
    public void closeBrowsers() {
        PlaywrightManager.closeAll();
    }

    /**
     * Entry point for tests: opens the application and returns the first page object,
     * so tests never deal with the raw Playwright Page.
     */
    protected LoginPage openLoginPage() {
        return new LoginPage(PlaywrightManager.page()).open();
    }

    /**
     * Opens the Products page already authenticated, skipping the login form.
     * SauceDemo keeps the session in the "session-username" cookie, so setting it is enough.
     * UI login is covered by LoginTest; other tests use this shortcut - faster and less flaky.
     */
    @Step("Open inventory as {user.username} (session cookie, UI login skipped)")
    protected InventoryPage openInventoryAs(TestUser user) {
        Page page = PlaywrightManager.page();
        page.context().addCookies(List.of(new Cookie(SESSION_COOKIE, user.getUsername())
                .setDomain(URI.create(TestConfig.baseUrl()).getHost())
                .setPath("/")));
        return new InventoryPage(page).open();
    }
}
