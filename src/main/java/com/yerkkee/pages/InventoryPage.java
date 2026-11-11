package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.yerkkee.data.Product;
import com.yerkkee.data.SortOption;
import com.yerkkee.pages.components.HeaderComponent;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * Products page ("/inventory.html").
 */
@Slf4j
public class InventoryPage extends BasePage {

    private static final String PATH = "/inventory.html";

    private final HeaderComponent header;
    private final Locator title;
    private final Locator sortDropdown;
    private final Locator activeSortOption;
    private final Locator items;
    private final Locator itemNames;
    private final Locator itemPrices;

    public InventoryPage(Page page) {
        super(page);
        header = new HeaderComponent(page);
        title = page.getByTestId("title");
        sortDropdown = page.getByTestId("product-sort-container");
        activeSortOption = page.getByTestId("active-option");
        items = page.getByTestId("inventory-item");
        itemNames = page.getByTestId("inventory-item-name");
        itemPrices = page.getByTestId("inventory-item-price");
    }

    /** Opens the page directly; requires an authenticated session (see BaseTest#openInventoryAs). */
    @Step("Open inventory page")
    public InventoryPage open() {
        navigate(PATH);
        return waitUntilOpened();
    }

    @Step("Wait until inventory page is opened")
    public InventoryPage waitUntilOpened() {
        waitForPage(PATH, sortDropdown);
        return this;
    }

    public HeaderComponent header() {
        return header;
    }

    @Step("Get page title")
    public String getPageTitle() {
        return title.innerText().trim();
    }

    @Step("Add to cart: {products}")
    public InventoryPage addToCart(Product... products) {
        for (Product product : products) {
            log.info("Adding to cart: {}", product);
            itemByName(items, product).getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Add to cart")).click();
        }
        return this;
    }

    @Step("Remove from cart: {product}")
    public InventoryPage removeFromCart(Product product) {
        log.info("Removing from cart: {}", product);
        itemByName(items, product).getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Remove")).click();
        return this;
    }

    @Step("Sort products by: {option}")
    public InventoryPage sortBy(SortOption option) {
        sortDropdown.selectOption(option.getValue());
        return this;
    }

    @Step("Get selected sort option")
    public String getSelectedSortLabel() {
        return activeSortOption.innerText().trim();
    }

    @Step("Get product names")
    public List<String> getProductNames() {
        return textsOf(itemNames);
    }

    @Step("Get product prices")
    public List<BigDecimal> getProductPrices() {
        return pricesOf(itemPrices);
    }

    @Step("Get product count")
    public int getProductCount() {
        return items.count();
    }
}
