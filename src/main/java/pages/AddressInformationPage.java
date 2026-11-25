package pages;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.UiActions;

/** Business page object 2: address section within the Contact Details screen. */
public class AddressInformationPage extends UiActions {
    public AddressInformationPage(WebDriver driver) { super(driver); }
    public void fillAddress(String street1, String street2, String city, String state, String postalCode, String country) {
        fill("Street 1", street1);
        fill("Street 2", street2);
        fill("City", city);
        fill("State/Province", state);
        fill("Zip/Postal Code", postalCode);
        select("Country", country);
    }
    public void save() {
        click(By.xpath("//form[.//label[normalize-space()='Street 1']]//button[@type='submit']"));
        saved();
    }
    public Map<String, String> readAddress() {
        ready();
        Map<String, String> values = new LinkedHashMap<>();
        for (String field : new String[]{"Street 1", "Street 2", "City", "State/Province", "Zip/Postal Code"}) values.put(field, value(field));
        values.put("Country", selected("Country"));
        return values;
    }
}
