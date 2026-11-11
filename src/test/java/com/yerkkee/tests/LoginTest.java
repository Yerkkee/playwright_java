package com.yerkkee.tests;

import com.yerkkee.base.BaseTest;
import com.yerkkee.data.TestUser;
import com.yerkkee.pages.InventoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Successful login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Standard user logs in with valid credentials and lands on the Products page")
    public void successfulLogin() {
        InventoryPage inventoryPage = openLoginPage().loginAs(TestUser.STANDARD);

        assertThat(inventoryPage.getPageTitle()).isEqualTo("Products");
    }
}
