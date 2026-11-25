package utils;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/** Shared starter-kit utility; Selenium Manager resolves the Chrome driver. */
public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private DriverFactory() { }
    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            ChromeOptions options = new ChromeOptions();
            if (Boolean.parseBoolean(System.getProperty("headless", "true"))) options.addArguments("--headless=new");
            options.addArguments("--window-size=1440,1000", "--disable-notifications");
            String binary = System.getProperty("chromeBinary");
            if (binary != null && !binary.isBlank()) options.setBinary(binary);
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ZERO);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(45));
            DRIVER.set(driver);
        }
        return DRIVER.get();
    }
    public static WebDriver currentDriver() { return DRIVER.get(); }
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try { if (driver != null) driver.quit(); } finally { DRIVER.remove(); }
    }
}
