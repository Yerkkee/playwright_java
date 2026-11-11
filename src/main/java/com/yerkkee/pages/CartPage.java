package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.yerkkee.data.Product;
import com.yerkkee.pages.components.HeaderComponent;
import io.qameta.allure.Step;

import java.math.BigDecimal;
import java.util.List;

/**
 * "Your Cart" page ("/cart.html").
 */
public class CartPage extends BasePage {

    private static final String PATH = "/cart.html";

    private final HeaderComponent header;
    private final Locator cartList;
    private final Locator items;
    private final Locator itemNames;
    private final Locator itemPrices;
    private final Locator continueShoppingButton;
    private final Locator checkoutButton;

    public CartPage(Page page) {
        super(page);
        header = new HeaderComponent(page);
        cartList = page.getByTestId("cart-list");
        items = page.getByTestId("inventory-item");
        itemNames = page.getByTestId("inventory-item-name");
        itemPrices = page.getByTestId("inventory-item-price");
        continueShoppingButton = page.getByTestId("continue-shopping");
        checkoutButton = page.getByTestId("checkout");
    }

    @Step("Wait until cart page is opened")
    public CartPage waitUntilOpened() {
        waitForPage(PATH, cartList);
        return this;
    }

    public HeaderComponent header() {
        return header;
    }

    @Step("Get cart item names")
    public List<String> getItemNames() {
        return textsOf(itemNames);
    }

    @Step("Get cart item prices")
    public List<BigDecimal> getItemPrices() {
        return pricesOf(itemPrices);
    }

    @Step("Get cart item count")
    public int getItemCount() {
        return items.count();
    }

    @Step("Remove from cart: {product}")
    public CartPage removeItem(Product product) {
        itemByName(items, product).getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Remove")).click();
        return this;
    }

    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        continueShoppingButton.click();
        return new InventoryPage(page).waitUntilOpened();
    }

    @Step("Proceed to checkout")
    public CheckoutStepOnePage checkout() {
        checkoutButton.click();
        return new CheckoutStepOnePage(page).waitUntilOpened();
    }
}
