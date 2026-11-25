package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import pages.LoginPage;
import utils.Config;
import utils.DriverFactory;

/** Starter-kit lifecycle from the PDF. */
public class BaseTest {
    protected WebDriver driver;
    protected boolean loggedIn;
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        loggedIn = false;
        driver = null;
        driver = DriverFactory.getDriver();
        driver.get(Config.baseUrl());
        new LoginPage(driver).loginAsAdmin();
        loggedIn = true;
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try { DriverFactory.quitDriver(); } finally { driver = null; loggedIn = false; }
    }
}
