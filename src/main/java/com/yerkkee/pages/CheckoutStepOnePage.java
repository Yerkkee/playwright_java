package com.yerkkee.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.yerkkee.data.CheckoutInfo;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * "Checkout: Your Information" form ("/checkout-step-one.html").
 */
public class CheckoutStepOnePage extends BasePage {

    private static final String PATH = "/checkout-step-one.html";

    private final Locator firstNameField;
    private final Locator lastNameField;
    private final Locator postalCodeField;
    private final Locator continueButton;
    private final Locator cancelButton;
    private final Locator errorMessage;

    public CheckoutStepOnePage(Page page) {
        super(page);
        firstNameField = page.getByTestId("firstName");
        lastNameField = page.getByTestId("lastName");
        postalCodeField = page.getByTestId("postalCode");
        continueButton = page.getByTestId("continue");
        cancelButton = page.getByTestId("cancel");
        errorMessage = page.getByTestId("error");
    }

    @Step("Wait until checkout information page is opened")
    public CheckoutStepOnePage waitUntilOpened() {
        waitForPage(PATH, firstNameField);
        return this;
    }

    @Step("Fill checkout information: {info}")
    public CheckoutStepOnePage fillInformation(CheckoutInfo info) {
        firstNameField.fill(info.firstName());
        lastNameField.fill(info.lastName());
        postalCodeField.fill(info.postalCode());
        return this;
    }

    /** Happy path: valid data, moves to the overview. */
    @Step("Continue to overview")
    public CheckoutStepTwoPage continueToOverview() {
        continueButton.click();
        return new CheckoutStepTwoPage(page).waitUntilOpened();
    }

    /** Negative path: invalid data, stays on this page with an error. */
    @Step("Continue expecting a validation error")
    public CheckoutStepOnePage continueExpectingError() {
        continueButton.click();
        return this;
    }

    @Step("Get error message")
    public String getErrorMessage() {
        assertThat(errorMessage).isVisible();
        return errorMessage.innerText().trim();
    }

    @Step("Cancel checkout")
    public CartPage cancel() {
        cancelButton.click();
        return new CartPage(page).waitUntilOpened();
    }
}
