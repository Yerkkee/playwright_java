package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.yerkkee.config.TestConfig;
import com.yerkkee.data.Product;
import com.yerkkee.utils.Prices;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Parent class for all Page Objects.
 *
 * <p>Conventions:
 * <ul>
 *   <li>locators are private fields built with getByTestId / getByRole only;</li>
 *   <li>actions that move the user to another page return that page object (fluent navigation);</li>
 *   <li>getters return plain data (String, List, BigDecimal) - the test asserts with AssertJ;</li>
 *   <li>{@code waitUntilOpened()} uses Playwright's auto-retrying assertThat, so no sleeps are needed;</li>
 *   <li>the raw Page never leaves the page object.</li>
 * </ul>
 */
@Slf4j
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    @Step("Get current URL")
    public String getCurrentUrl() {
        return page.url();
    }

    /** Opens a path relative to base.url, e.g. "/inventory.html". */
    protected void navigate(String path) {
        String url = TestConfig.baseUrl() + path;
        log.info("Navigating to: {}", url);
        page.navigate(url);
    }

    /**
     * Waits until the browser is on {@code path} and {@code marker} is visible.
     * Playwright's assertThat retries until default.timeout, so slow pages do not need sleeps.
     */
    protected void waitForPage(String path, Locator marker) {
        // Not Pattern.quote(): Playwright evaluates the regex in the browser (JavaScript),
        // which does not understand Java's \Q...\E quoting.
        String escapedPath = path.replaceAll("[.*+?^${}()|\\[\\]\\\\]", "\\\\$0");
        assertThat(page).hasURL(Pattern.compile(".*" + escapedPath + "$"));
        assertThat(marker).isVisible();
        log.debug("{} is opened", getClass().getSimpleName());
    }

    /**
     * Product row/card whose name matches exactly - independent of SauceDemo's generated ids
     * like "add-to-cart-sauce-labs-backpack".
     */
    protected Locator itemByName(Locator items, Product product) {
        return items.filter(new Locator.FilterOptions().setHas(
                page.getByTestId("inventory-item-name")
                        .getByText(product.getDisplayName(), new Locator.GetByTextOptions().setExact(true))));
    }

    /** Visible texts of all matched elements, in DOM order. */
    protected static List<String> textsOf(Locator elements) {
        return elements.allInnerTexts().stream().map(String::trim).toList();
    }

    /** Money values of all matched elements, in DOM order. */
    protected static List<BigDecimal> pricesOf(Locator elements) {
        return textsOf(elements).stream().map(Prices::parse).toList();
    }
}
