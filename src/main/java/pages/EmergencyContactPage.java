package pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.UiActions;

/** Business page object 3: emergency contact add, read, edit and delete. */
public class EmergencyContactPage extends UiActions {
    public EmergencyContactPage(WebDriver driver) { super(driver); }
    public void open() { openEmployeeTab("Emergency Contacts", "Assigned Emergency Contacts"); }
    private String rowXpath(String name) {
        if (name.contains("'")) throw new IllegalArgumentException("Test contact name must not contain apostrophes");
        return "//div[@role='row'][.//div[@role='cell'][normalize-space()='" + name + "']]";
    }
    private By row(String name) { return By.xpath(rowXpath(name)); }
    private void fillContact(String name, String relationship, String home, String mobile, String work) {
        fill("Name", name);
        fill("Relationship", relationship);
        fill("Home Telephone", home);
        fill("Mobile", mobile);
        fill("Work Telephone", work);
    }
    private void save() {
        click(By.xpath("//form[.//label[normalize-space()='Relationship']]//button[@type='submit']"));
        saved();
        visible(By.xpath("//h6[normalize-space()='Assigned Emergency Contacts']"));
    }
    public void add(String name, String relationship, String home, String mobile, String work) {
        click(By.xpath("//h6[normalize-space()='Assigned Emergency Contacts']/following-sibling::button"));
        fillContact(name, relationship, home, mobile, work);
        save();
        visible(row(name));
    }
    public List<String> read(String name) {
        ready();
        return visible(row(name)).findElements(By.cssSelector("[role='cell']")).stream()
            .map(WebElement::getText).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
    public void edit(String originalName, String name, String relationship, String home, String mobile, String work) {
        click(By.xpath(rowXpath(originalName) + "//button[.//i[contains(@class,'bi-pencil-fill')]]"));
        visible(input("Relationship"));
        ready();
        fillContact(name, relationship, home, mobile, work);
        save();
        visible(row(name));
    }
    public void delete(String name) {
        click(By.xpath(rowXpath(name) + "//button[.//i[contains(@class,'bi-trash')]]"));
        click(By.xpath("//button[normalize-space()='Yes, Delete']"));
        visible(By.xpath("//div[contains(@class,'oxd-toast')]//*[contains(normalize-space(),'Successfully Deleted')]"));
        ready();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(row(name)));
    }
    public boolean contains(String name) {
        visible(By.xpath("//h6[normalize-space()='Assigned Emergency Contacts']"));
        ready();
        return !elements(row(name)).isEmpty();
    }
}
