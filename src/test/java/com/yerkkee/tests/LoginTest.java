package com.yerkkee.tests;

import com.yerkkee.base.BaseTest;
import com.yerkkee.pages.LoginPage;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void successfulLogin() {
        LoginPage loginPage = new LoginPage(getPage());
        loginPage.openBaseUrl();
        loginPage.loginAs("standard_user", "secret_sauce");
        // проверка что залогинились
    }
}
