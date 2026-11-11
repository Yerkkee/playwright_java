package com.yerkkee.base;

import com.yerkkee.pages.LoginPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

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
}
