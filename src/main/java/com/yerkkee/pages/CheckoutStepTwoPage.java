package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.yerkkee.utils.Prices;
import io.qameta.allure.Step;

import java.math.BigDecimal;
import java.util.List;

/**
 * "Checkout: Overview" page ("/checkout-step-two.html"): items, Item total, Tax, Total.
 */
public class CheckoutStepTwoPage extends BasePage {

    private static final String PATH = "/checkout-step-two.html";

    private final Locator itemNames;
    private final Locator itemPrices;
    private final Locator subtotalLabel;
    private final Locator taxLabel;
    private final Locator totalLabel;
    private final Locator finishButton;
    private final Locator cancelButton;

    public CheckoutStepTwoPage(Page page) {
        super(page);
        itemNames = page.getByTestId("inventory-item-name");
        itemPrices = page.getByTestId("inventory-item-price");
        subtotalLabel = page.getByTestId("subtotal-label");
        taxLabel = page.getByTestId("tax-label");
        totalLabel = page.getByTestId("total-label");
        finishButton = page.getByTestId("finish");
        cancelButton = page.getByTestId("cancel");
    }

    @Step("Wait until checkout overview page is opened")
    public CheckoutStepTwoPage waitUntilOpened() {
        waitForPage(PATH, finishButton);
        return this;
    }

    @Step("Get overview item names")
    public List<String> getItemNames() {
        return textsOf(itemNames);
    }

    @Step("Get overview item prices")
    public List<BigDecimal> getItemPrices() {
        return pricesOf(itemPrices);
    }

    /** Value of "Item total: $X" (sum before tax). */
    @Step("Get item total")
    public BigDecimal getItemTotal() {
        return Prices.parse(subtotalLabel.innerText());
    }

    @Step("Get tax")
    public BigDecimal getTax() {
        return Prices.parse(taxLabel.innerText());
    }

    @Step("Get total")
    public BigDecimal getTotal() {
        return Prices.parse(totalLabel.innerText());
    }

    @Step("Finish order")
    public CheckoutCompletePage finish() {
        finishButton.click();
        return new CheckoutCompletePage(page).waitUntilOpened();
    }

    @Step("Cancel order")
    public InventoryPage cancel() {
        cancelButton.click();
        return new InventoryPage(page).waitUntilOpened();
    }
}
