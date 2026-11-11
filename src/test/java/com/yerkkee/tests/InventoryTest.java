package com.yerkkee.tests;

import com.yerkkee.base.BaseTest;
import com.yerkkee.data.Product;
import com.yerkkee.data.SortOption;
import com.yerkkee.data.TestUser;
import com.yerkkee.pages.InventoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Inventory")
public class InventoryTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Catalog")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Products page shows every catalog product with its correct price")
    public void catalogShowsAllProductsWithPrices() {
        InventoryPage inventoryPage = openInventoryAs(TestUser.STANDARD);

        Map<String, BigDecimal> expected = Arrays.stream(Product.values())
                .collect(Collectors.toMap(Product::getDisplayName, Product::getPrice));

        assertThat(namesToPrices(inventoryPage)).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @DataProvider(name = "sortOptions")
    public Object[][] sortOptions() {
        return Arrays.stream(SortOption.values())
                .map(option -> new Object[]{option})
                .toArray(Object[][]::new);
    }

    /**
     * Reads the real order from the page and compares it with a copy sorted in Java.
     * Not a hardcoded expected list: the test stays valid if the catalog changes.
     */
    @Test(groups = "regression", dataProvider = "sortOptions")
    @Story("Sorting")
    @Severity(SeverityLevel.NORMAL)
    @Description("Products are actually reordered according to the selected sort option")
    public void productsAreSorted(SortOption option) {
        InventoryPage inventoryPage = openInventoryAs(TestUser.STANDARD).sortBy(option);

        assertThat(inventoryPage.getSelectedSortLabel()).isEqualTo(option.getLabel());

        switch (option) {
            case NAME_A_TO_Z -> assertSorted(inventoryPage.getProductNames(), Comparator.naturalOrder());
            case NAME_Z_TO_A -> assertSorted(inventoryPage.getProductNames(), Comparator.reverseOrder());
            case PRICE_LOW_TO_HIGH -> assertSorted(inventoryPage.getProductPrices(), Comparator.naturalOrder());
            case PRICE_HIGH_TO_LOW -> assertSorted(inventoryPage.getProductPrices(), Comparator.reverseOrder());
        }
    }

    private static <T> void assertSorted(List<T> actual, Comparator<? super T> order) {
        // A sorted copy of an empty list equals the empty list - guard against a vacuous pass
        assertThat(actual).hasSize(Product.values().length);
        List<T> expected = actual.stream().sorted(order).toList();
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    private static Map<String, BigDecimal> namesToPrices(InventoryPage inventoryPage) {
        List<String> names = inventoryPage.getProductNames();
        List<BigDecimal> prices = inventoryPage.getProductPrices();
        assertThat(prices).hasSameSizeAs(names);
        return IntStream.range(0, names.size()).boxed()
                .collect(Collectors.toMap(names::get, prices::get, (a, b) -> a, LinkedHashMap::new));
    }
}
