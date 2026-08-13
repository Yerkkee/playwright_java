package com.yerkkee.pages;

import com.microsoft.playwright.Page;
import com.yerkkee.config.TestConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Parent class for all Page Objects.
 * Holds the Playwright Page instance and common actions.
 *
 * Subclasses should:
 *   - Define page-specific locators as private fields
 *   - Expose page-specific actions as public methods
 *   - NOT expose the raw Page object outside
 */
@Slf4j
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    /**
     * Navigate to a URL and wait for the page to be fully loaded.
     */
    public void navigate(String url) {
        log.info("Navigating to: {}", url);
        page.navigate(url);
    }

    /**
     * Navigate to the base URL from config.
     */
    public void openBaseUrl() {
        navigate(TestConfig.get("base.url"));
    }

    /**
     * Get current page URL.
     */
    public String getCurrentUrl() {
        return page.url();
    }

    /**
     * Get current page title.
     */
    public String getTitle() {
        return page.title();
    }
}