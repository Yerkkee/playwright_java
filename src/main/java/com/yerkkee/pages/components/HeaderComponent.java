package com.yerkkee.pages.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.yerkkee.pages.CartPage;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Top bar shown on every page after login: burger menu button, cart link and cart badge.
 * A component (not a page) because it is reused by Inventory, Cart and Checkout pages.
 */
public class HeaderComponent {

    private final Page page;
    private final Locator menuButton;
    private final Locator cartLink;
    private final Locator cartBadge;

    public HeaderComponent(Page page) {
        this.page = page;
        // The burger button has no data-test attribute, so it is found by its accessible name
        this.menuButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Open Menu"));
        this.cartLink = page.getByTestId("shopping-cart-link");
        this.cartBadge = page.getByTestId("shopping-cart-badge");
    }

    @Step("Open burger menu")
    public BurgerMenuComponent openMenu() {
        menuButton.click();
        return new BurgerMenuComponent(page).waitUntilOpened();
    }

    @Step("Open cart")
    public CartPage openCart() {
        cartLink.click();
        return new CartPage(page).waitUntilOpened();
    }

    /**
     * Number on the cart badge; 0 when the badge is not shown (empty cart).
     * Reads the current state - call it after the action that changes the cart has completed.
     */
    @Step("Get cart badge count")
    public int getCartBadgeCount() {
        return cartBadge.isVisible() ? Integer.parseInt(cartBadge.innerText().trim()) : 0;
    }

    /** Waiting check: retries until the badge shows {@code expected} (or disappears for 0). */
    @Step("Cart badge should show {expected}")
    public HeaderComponent shouldShowCartBadge(int expected) {
        if (expected == 0) {
            assertThat(cartBadge).isHidden();
        } else {
            assertThat(cartBadge).hasText(String.valueOf(expected));
        }
        return this;
    }
}
