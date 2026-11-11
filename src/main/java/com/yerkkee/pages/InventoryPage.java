package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryPage extends BasePage {

    private final Locator inventoryList;
    private final Locator pageTitle;
    private final Locator shoppingCart;
    private final Locator menuButton;
    private final Locator logoutLink;

    public InventoryPage(Page page) {
        super(page);
        inventoryList = page.locator(".inventory_list");
        pageTitle = page.getByTestId("title");
        shoppingCart = page.locator(".shopping_cart_link");
        menuButton = page.locator("#react-burger-menu-btn");
        logoutLink = page.locator("#logout_sidebar_link");
    }

    @Step("Check that inventory page is loaded")
    public boolean isLoaded() {
        try {
            inventoryList.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE));
            return true;
        } catch (Exception e) {
            log.warn("Inventory page did not load: {}", e.getMessage());
            return false;
        }
    }

    @Step("Get page title")
    public String getPageTitle() {
        return pageTitle.textContent();
    }

    @Step("Get product count")
    public int getProductCount() {
        return inventoryList.locator(".inventory_item").count();
    }

    @Step("Check that shopping cart is visible")
    public boolean isCartVisible() {
        return shoppingCart.isVisible();
    }

    @Step("Logout")
    public void logout() {
        log.info("Logging out");
        menuButton.click();
        logoutLink.click();
    }
}