package com.yerkkee.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.yerkkee.config.TestConfig;
import lombok.Getter;
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

    @Getter
    private Page page;

    @BeforeClass
    public void launchBrowser() {
        log.info("=== Launching browser ===");
        playwright = Playwright.create();

        // Tell Playwright to look for data-test attribute (SauceDemo uses this)
        playwright.selectors().setTestIdAttribute("data-test");

        String browserName = TestConfig.get("browser");
        boolean headless = TestConfig.getBoolean("headless");
        double slowMo = TestConfig.getInt("slow.mo");

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo);

        browser = switch (browserName.toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(options);
            case "webkit" -> playwright.webkit().launch(options);
            case "chromium" -> playwright.chromium().launch(options);
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browserName);
        };

        log.info("Browser launched: {} (headless={})", browserName, headless);
    }

    @BeforeMethod
    public void createContextAndPage() {
        log.info("--- Creating new context and page ---");

        int width = TestConfig.getInt("viewport.width");
        int height = TestConfig.getInt("viewport.height");

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(width, height));

        context.setDefaultTimeout(TestConfig.getInt("default.timeout"));
        context.setDefaultNavigationTimeout(TestConfig.getInt("navigation.timeout"));

        page = context.newPage();
    }

    @AfterMethod
    public void closeContext() {
        log.info("--- Closing context ---");
        if (context != null) {
            context.close();
        }
    }

    @AfterClass
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