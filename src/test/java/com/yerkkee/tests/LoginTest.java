package com.yerkkee.tests;

import com.yerkkee.base.BaseTest;
import com.yerkkee.data.LoginCase;
import com.yerkkee.data.Messages;
import com.yerkkee.data.TestUser;
import com.yerkkee.pages.InventoryPage;
import com.yerkkee.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Successful login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Standard user logs in with valid credentials and lands on the Products page")
    public void standardUserCanLogin() {
        InventoryPage inventoryPage = openLoginPage().loginAs(TestUser.STANDARD);

        assertThat(inventoryPage.getPageTitle()).isEqualTo(Messages.PRODUCTS_TITLE);
        assertThat(inventoryPage.getCurrentUrl()).endsWith("/inventory.html");
    }

    @Test(groups = "regression")
    @Story("Locked out user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Locked out user gets a clear error and stays on the login page")
    public void lockedOutUserCannotLogin() {
        LoginPage loginPage = openLoginPage().loginExpectingError(TestUser.LOCKED_OUT);

        assertThat(loginPage.getErrorMessage()).isEqualTo(Messages.LOCKED_OUT);
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        String validUser = TestUser.STANDARD.getUsername();
        String validPassword = TestUser.STANDARD.getPassword();
        return new Object[][]{
                {new LoginCase("valid user, wrong password", validUser, "wrong_password", Messages.INVALID_CREDENTIALS)},
                {new LoginCase("unknown user, valid password", "unknown_user", validPassword, Messages.INVALID_CREDENTIALS)},
                {new LoginCase("username in wrong case", validUser.toUpperCase(), validPassword, Messages.INVALID_CREDENTIALS)},
                {new LoginCase("password with trailing space", validUser, validPassword + " ", Messages.INVALID_CREDENTIALS)},
        };
    }

    @Test(groups = "regression", dataProvider = "invalidCredentials")
    @Story("Invalid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Wrong username/password combinations are rejected with the same generic error")
    public void invalidCredentialsAreRejected(LoginCase loginCase) {
        LoginPage loginPage = openLoginPage()
                .loginExpectingError(loginCase.username(), loginCase.password());

        assertThat(loginPage.getErrorMessage()).isEqualTo(loginCase.expectedError());
    }

    @DataProvider(name = "emptyFields")
    public Object[][] emptyFields() {
        String validUser = TestUser.STANDARD.getUsername();
        String validPassword = TestUser.STANDARD.getPassword();
        return new Object[][]{
                {new LoginCase("both fields empty", "", "", Messages.USERNAME_REQUIRED)},
                {new LoginCase("empty username", "", validPassword, Messages.USERNAME_REQUIRED)},
                {new LoginCase("empty password", validUser, "", Messages.PASSWORD_REQUIRED)},
        };
    }

    @Test(groups = "regression", dataProvider = "emptyFields")
    @Story("Required fields")
    @Severity(SeverityLevel.NORMAL)
    @Description("Submitting the form with an empty required field shows which field is missing")
    public void emptyRequiredFieldShowsError(LoginCase loginCase) {
        LoginPage loginPage = openLoginPage()
                .loginExpectingError(loginCase.username(), loginCase.password());

        assertThat(loginPage.getErrorMessage()).isEqualTo(loginCase.expectedError());
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Logout")
    @Severity(SeverityLevel.CRITICAL)
    @Description("After logout the session is gone: Products page is no longer reachable without logging in again")
    public void logoutEndsSession() {
        LoginPage loginPage = openLoginPage()
                .loginAs(TestUser.STANDARD)
                .header()
                .openMenu()
                .logout();

        assertThat(loginPage.getCurrentUrl()).doesNotContain("inventory");
        assertThat(openInventoryWithoutSession().getErrorMessage()).isEqualTo(Messages.INVENTORY_REQUIRES_LOGIN);
    }

    @Test(groups = "regression")
    @Story("Access control")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Opening the Products page by URL without logging in redirects to login with an error")
    public void inventoryRequiresLogin() {
        LoginPage loginPage = openInventoryWithoutSession();

        assertThat(loginPage.getErrorMessage()).isEqualTo(Messages.INVENTORY_REQUIRES_LOGIN);
    }
}
