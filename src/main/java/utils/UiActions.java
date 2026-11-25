package utils;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Framework interactions, not an additional business page object. */
public class UiActions {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    public UiActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.timeout()));
    }
    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    protected void click(By locator) {
        ready();
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }
    public void ready() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".oxd-form-loader")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".oxd-loading-spinner")));
    }
    protected By input(String label) {
        return By.xpath("//div[contains(@class,'oxd-input-group')][.//label[normalize-space()='" + label + "']]//input");
    }
    protected void fill(String label, String value) {
        WebElement field = visible(input(label));
        field.sendKeys(Keys.chord(System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL, "a"));
        field.sendKeys(Keys.BACK_SPACE);
        if (!value.isEmpty()) field.sendKeys(value);
        field.sendKeys(Keys.TAB);
    }
    protected String value(String label) { return visible(input(label)).getDomProperty("value"); }
    protected void select(String label, String option) {
        String group = "//div[contains(@class,'oxd-input-group')][.//label[normalize-space()='" + label + "']]";
        By listbox = By.xpath(group + "//div[@role='listbox']");
        // Tabbing from the postal-code input can already open this dropdown.
        if (elements(listbox).stream().noneMatch(WebElement::isDisplayed)) {
            click(By.xpath(group + "//div[contains(@class,'oxd-select-text-input')]"));
        }
        click(By.xpath(group + "//div[@role='option']//span[normalize-space()='" + option + "']"));
        wait.until(d -> selected(label).equals(option));
    }
    protected String selected(String label) {
        return visible(By.xpath("//div[contains(@class,'oxd-input-group')][.//label[normalize-space()='" + label + "']]//div[contains(@class,'oxd-select-text-input')]" )).getText();
    }
    protected void saved() {
        visible(By.xpath("//div[contains(@class,'oxd-toast')]//*[contains(normalize-space(),'Successfully Saved') or contains(normalize-space(),'Successfully Updated')]"));
        ready();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".oxd-toast")));
    }
    protected void openEmployeeTab(String tab, String heading) {
        click(By.xpath("//a[normalize-space()='" + tab + "']"));
        visible(By.xpath("//h6[normalize-space()='" + heading + "']"));
        ready();
    }
    public void reload() { driver.navigate().refresh(); ready(); }
    protected List<WebElement> elements(By locator) { return driver.findElements(locator); }
}
