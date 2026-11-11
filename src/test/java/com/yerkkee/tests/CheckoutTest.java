package com.yerkkee.tests;

import com.yerkkee.base.BaseTest;
import com.yerkkee.data.CheckoutInfo;
import com.yerkkee.data.Messages;
import com.yerkkee.data.Product;
import com.yerkkee.data.TestUser;
import com.yerkkee.pages.CheckoutCompletePage;
import com.yerkkee.pages.CheckoutStepOnePage;
import com.yerkkee.pages.CheckoutStepTwoPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Checkout")
public class CheckoutTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Purchase")
    @Severity(SeverityLevel.BLOCKER)
    @Description("User buys two products end to end and sees the order confirmation; the cart is emptied")
    public void completePurchase() {
        CheckoutCompletePage completePage = openInventoryAs(TestUser.STANDARD)
                .addToCart(Product.BACKPACK, Product.BIKE_LIGHT)
                .header()
                .openCart()
                .checkout()
                .fillInformation(CheckoutInfo.valid())
                .continueToOverview()
                .finish();

        assertThat(completePage.getConfirmationHeader()).isEqualTo(Messages.ORDER_COMPLETE);
        completePage.header().shouldShowCartBadge(0);
    }

    @DataProvider(name = "missingFields")
    public Object[][] missingFields() {
        CheckoutInfo valid = CheckoutInfo.valid();
        return new Object[][]{
                {valid.withFirstName(""), Messages.FIRST_NAME_REQUIRED},
                {valid.withLastName(""), Messages.LAST_NAME_REQUIRED},
                {valid.withPostalCode(""), Messages.POSTAL_CODE_REQUIRED},
        };
    }

    @Test(groups = "regression", dataProvider = "missingFields")
    @Story("Checkout validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Each required field of the checkout form is validated and the user cannot continue")
    public void requiredFieldsAreValidated(CheckoutInfo info, String expectedError) {
        CheckoutStepOnePage stepOne = openInventoryAs(TestUser.STANDARD)
                .addToCart(Product.ONESIE)
                .header()
                .openCart()
                .checkout()
                .fillInformation(info)
                .continueExpectingError();

        assertThat(stepOne.getErrorMessage()).isEqualTo(expectedError);
        assertThat(stepOne.getCurrentUrl()).endsWith("/checkout-step-one.html");
    }

    @Test(groups = "regression")
    @Story("Order totals")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Item total equals the sum of the added products' prices, and Total = Item total + Tax")
    public void orderTotalsAreCalculatedCorrectly() {
        Product[] products = {Product.BACKPACK, Product.BOLT_T_SHIRT, Product.ONESIE};
        BigDecimal expectedItemTotal = Arrays.stream(products)
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CheckoutStepTwoPage overview = openInventoryAs(TestUser.STANDARD)
                .addToCart(products)
                .header()
                .openCart()
                .checkout()
                .fillInformation(CheckoutInfo.valid())
                .continueToOverview();

        assertThat(overview.getItemNames()).containsExactlyInAnyOrder(
                Arrays.stream(products).map(Product::getDisplayName).toArray(String[]::new));
        // isEqualByComparingTo: BigDecimal.equals would treat 39.90 and 39.9 as different
        assertThat(overview.getItemTotal())
                .isEqualByComparingTo(expectedItemTotal)
                .isEqualByComparingTo(sum(overview.getItemPrices().toArray(BigDecimal[]::new)));
        assertThat(overview.getTax()).isPositive();
        assertThat(overview.getTotal()).isEqualByComparingTo(overview.getItemTotal().add(overview.getTax()));
    }

    private static BigDecimal sum(BigDecimal... values) {
        return Arrays.stream(values).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
