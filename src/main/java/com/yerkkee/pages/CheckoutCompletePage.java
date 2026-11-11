package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.yerkkee.pages.components.HeaderComponent;
import io.qameta.allure.Step;

/**
 * "Checkout: Complete!" page ("/checkout-complete.html").
 */
public class CheckoutCompletePage extends BasePage {

    private static final String PATH = "/checkout-complete.html";

    private final HeaderComponent header;
    private final Locator completeHeader;
    private final Locator completeText;
    private final Locator backHomeButton;

    public CheckoutCompletePage(Page page) {
        super(page);
        header = new HeaderComponent(page);
        completeHeader = page.getByTestId("complete-header");
        completeText = page.getByTestId("complete-text");
        backHomeButton = page.getByTestId("back-to-products");
    }

    @Step("Wait until order complete page is opened")
    public CheckoutCompletePage waitUntilOpened() {
        waitForPage(PATH, completeHeader);
        return this;
    }

    public HeaderComponent header() {
        return header;
    }

    @Step("Get confirmation header")
    public String getConfirmationHeader() {
        return completeHeader.innerText().trim();
    }

    @Step("Get confirmation text")
    public String getConfirmationText() {
        return completeText.innerText().trim();
    }

    @Step("Back to products")
    public InventoryPage backHome() {
        backHomeButton.click();
        return new InventoryPage(page).waitUntilOpened();
    }
}
