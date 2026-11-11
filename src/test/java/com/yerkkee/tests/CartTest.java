package com.yerkkee.tests;

import com.yerkkee.base.BaseTest;
import com.yerkkee.data.Product;
import com.yerkkee.data.TestUser;
import com.yerkkee.pages.CartPage;
import com.yerkkee.pages.InventoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Cart")
public class CartTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Add to cart")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Adding a product updates the cart badge and the product appears in the cart")
    public void addedProductAppearsInCart() {
        InventoryPage inventoryPage = openInventoryAs(TestUser.STANDARD)
                .addToCart(Product.BACKPACK);

        inventoryPage.header().shouldShowCartBadge(1);
        CartPage cartPage = inventoryPage.header().openCart();

        assertThat(cartPage.getItemNames()).containsExactly(Product.BACKPACK.getDisplayName());
        assertThat(cartPage.getItemPrices()).containsExactly(Product.BACKPACK.getPrice());
    }

    @Test(groups = "regression")
    @Story("Remove from cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Removing the only product from the Products page hides the cart badge and empties the cart")
    public void removeProductOnInventoryPage() {
        InventoryPage inventoryPage = openInventoryAs(TestUser.STANDARD)
                .addToCart(Product.BIKE_LIGHT)
                .removeFromCart(Product.BIKE_LIGHT);

        inventoryPage.header().shouldShowCartBadge(0);
        assertThat(inventoryPage.header().openCart().getItemCount()).isZero();
    }

    @Test(groups = "regression")
    @Story("Remove from cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Removing one product on the Cart page keeps the others and decrements the badge")
    public void removeProductOnCartPage() {
        CartPage cartPage = openInventoryAs(TestUser.STANDARD)
                .addToCart(Product.BACKPACK, Product.ONESIE)
                .header()
                .openCart()
                .removeItem(Product.ONESIE);

        cartPage.header().shouldShowCartBadge(1);
        assertThat(cartPage.getItemNames()).containsExactly(Product.BACKPACK.getDisplayName());
    }

    @Test(groups = "regression")
    @Story("Add to cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Several products can be added; the cart shows each of them with its price")
    public void multipleProductsInCart() {
        Product[] products = {Product.BACKPACK, Product.FLEECE_JACKET, Product.RED_T_SHIRT};

        InventoryPage inventoryPage = openInventoryAs(TestUser.STANDARD).addToCart(products);
        inventoryPage.header().shouldShowCartBadge(products.length);
        CartPage cartPage = inventoryPage.header().openCart();

        assertThat(cartPage.getItemNames()).containsExactlyInAnyOrder(
                Product.BACKPACK.getDisplayName(),
                Product.FLEECE_JACKET.getDisplayName(),
                Product.RED_T_SHIRT.getDisplayName());
        assertThat(cartPage.getItemPrices()).containsExactlyInAnyOrder(
                Product.BACKPACK.getPrice(),
                Product.FLEECE_JACKET.getPrice(),
                Product.RED_T_SHIRT.getPrice());
    }
}
