package pages;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.Config;
import utils.UiActions;

/** Business page object 1: telephone numbers and email addresses. */
public class ContactDetailsPage extends UiActions {
    private final By heading = By.xpath("//h6[normalize-space()='Contact Details']");
    private final By saveButton = By.xpath("//form[.//label[normalize-space()='Work Email']]//button[@type='submit']");
    public ContactDetailsPage(WebDriver driver) { super(driver); }
    public void open(int employeeNumber) {
        driver.get(Config.baseUrl() + "/web/index.php/pim/viewPersonalDetails/empNumber/" + employeeNumber);
        openEmployeeTab("Contact Details", "Contact Details");
    }
    public void fillContactDetails(String home, String mobile, String work, String workEmail, String otherEmail) {
        fill("Home", home);
        fill("Mobile", mobile);
        fill("Work", work);
        fill("Work Email", workEmail);
        fill("Other Email", otherEmail);
    }
    public void save() { click(saveButton); saved(); }
    public Map<String, String> readContactDetails() {
        ready();
        Map<String, String> values = new LinkedHashMap<>();
        for (String field : new String[]{"Home", "Mobile", "Work", "Work Email", "Other Email"}) values.put(field, value(field));
        return values;
    }
    public void setEmail(String label, String email) {
        if (!label.equals("Work Email") && !label.equals("Other Email")) throw new IllegalArgumentException("Unsupported email field: " + label);
        fill(label, email);
    }
    public void attemptSave() { click(saveButton); }
    public String emailError(String label) {
        return visible(By.xpath("//div[contains(@class,'oxd-input-group')][.//label[normalize-space()='" + label + "']]//span[contains(@class,'oxd-input-field-error-message')]" )).getText();
    }
    public void waitUntilLoaded() {
        visible(heading);
        wait.until(ExpectedConditions.visibilityOfElementLocated(input("Work Email")));
        ready();
    }
}
