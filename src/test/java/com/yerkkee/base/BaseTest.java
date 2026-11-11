package com.yerkkee.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.yerkkee.config.BrowserName;
import com.yerkkee.config.TestConfig;
import com.yerkkee.pages.LoginPage;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for all UI tests.
 * Manages Playwright lifecycle: launches browser once per class,
 * creates a fresh context+page for each test method.
 */
@Slf4j
public abstract class BaseTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;

    private Page page;

    @BeforeClass(alwaysRun = true)
    public void launchBrowser() {
        log.info("=== Launching browser ===");
        playwright = Playwright.create();

        // Tell Playwright to look for data-test attribute (SauceDemo uses this)
        playwright.selectors().setTestIdAttribute("data-test");

        BrowserName browserName = TestConfig.browser();
        boolean headless = TestConfig.headless();

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(TestConfig.slowMo());

        browser = switch (browserName) {
            case CHROMIUM -> playwright.chromium().launch(options);
            case FIREFOX -> playwright.firefox().launch(options);
            case WEBKIT -> playwright.webkit().launch(options);
        };

        log.info("Browser launched: {} (headless={})", browserName, headless);
    }

    @BeforeMethod(alwaysRun = true)
    public void createContextAndPage() {
        log.info("--- Creating new context and page ---");

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(TestConfig.viewportWidth(), TestConfig.viewportHeight()));

        context.setDefaultTimeout(TestConfig.defaultTimeout());
        context.setDefaultNavigationTimeout(TestConfig.navigationTimeout());

        page = context.newPage();
    }

    /**
     * Entry point for tests: opens the application and returns the first page object,
     * so tests never deal with the raw Playwright Page.
     */
    protected LoginPage openLoginPage() {
        return new LoginPage(page).open();
    }

    @AfterMethod(alwaysRun = true)
    public void closeContext() {
        log.info("--- Closing context ---");
        if (context != null) {
            context.close();
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        log.info("=== Closing browser ===");
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}