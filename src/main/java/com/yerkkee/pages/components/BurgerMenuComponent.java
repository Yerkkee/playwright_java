package com.yerkkee.pages.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.yerkkee.pages.InventoryPage;
import com.yerkkee.pages.LoginPage;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Side menu opened from the header. Playwright waits for links to stop moving
 * (the menu slides in), so no explicit animation wait is needed.
 */
public class BurgerMenuComponent {

    private final Page page;
    private final Locator allItemsLink;
    private final Locator logoutLink;
    private final Locator resetAppStateLink;

    public BurgerMenuComponent(Page page) {
        this.page = page;
        this.allItemsLink = page.getByTestId("inventory-sidebar-link");
        this.logoutLink = page.getByTestId("logout-sidebar-link");
        this.resetAppStateLink = page.getByTestId("reset-sidebar-link");
    }

    @Step("Wait until burger menu is opened")
    public BurgerMenuComponent waitUntilOpened() {
        assertThat(logoutLink).isVisible();
        return this;
    }

    @Step("Logout")
    public LoginPage logout() {
        logoutLink.click();
        return new LoginPage(page).waitUntilOpened();
    }

    @Step("Go to All Items")
    public InventoryPage goToAllItems() {
        allItemsLink.click();
        return new InventoryPage(page).waitUntilOpened();
    }

    /** Clears the cart and app state stored in the browser. */
    @Step("Reset app state")
    public BurgerMenuComponent resetAppState() {
        resetAppStateLink.click();
        return this;
    }
}
