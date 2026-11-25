package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.Config;
import utils.UiActions;

/** LoginPage supplied in the PDF, with explicit waits and configurable credentials. */
public class LoginPage extends UiActions {
    private final By txtUsername = By.name("username");
    private final By txtPassword = By.name("password");
    private final By btnLogin = By.xpath("//button[@type='submit']");
    public LoginPage(WebDriver driver) { super(driver); }
    public void loginAsAdmin() {
        visible(txtUsername).sendKeys(Config.username());
        visible(txtPassword).sendKeys(Config.password());
        click(btnLogin);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        ready();
    }
}
