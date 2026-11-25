package tests;

import base.BaseTest;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;
import utils.TestEmployee;

/** Four data-driven scenarios, using exactly the three assigned business page objects. */
@Listeners(utils.ExtentTestListener.class)
public class ContactDetailsTest extends BaseTest {
    private TestEmployee employee;
    private ContactDetailsPage contact;

    @BeforeMethod(alwaysRun = true)
    public void prepareEmployee() {
        if (!loggedIn) throw new org.testng.SkipException("Login setup did not complete; see setup failure");
        new NavigationBar(driver).openPIM();
        employee = new TestEmployee(driver);
        employee.create();
        contact = new ContactDetailsPage(driver);
        contact.open(employee.number());
    }
    @AfterMethod(alwaysRun = true)
    public void removeEmployee() {
        if (employee != null) {
            try { employee.delete(); } finally { employee = null; }
        }
    }
    @DataProvider(name = "contactData")
    public Object[][] contactData() {
        return new Object[][] {
            {"0111234567", "0771234567", "0117654321", "team12.work@example.com", "team12.other@example.com"},
            {"0201234567", "0781234567", "0207654321", "team12.second@example.com", "team12.backup@example.com"}
        };
    }
    @Test(dataProvider = "contactData", description = "TC01: Save telephone/email details and verify all five fields after reload")
    public void saveContactDetails(String home, String mobile, String work, String workEmail, String otherEmail) {
        contact.fillContactDetails(home, mobile, work, workEmail, otherEmail);
        contact.save();
        contact.reload();
        contact.waitUntilLoaded();
        Assert.assertEquals(contact.readContactDetails(), Map.of("Home", home, "Mobile", mobile,
            "Work", work, "Work Email", workEmail, "Other Email", otherEmail), "All contact fields must persist");
    }
    @DataProvider(name = "addressData")
    public Object[][] addressData() {
        return new Object[][] {
            {"12 Test Street", "Suite 2", "Pune", "Maharashtra", "411001", "India"},
            {"24 Sample Road", "Floor 3", "Colombo", "Western", "00100", "Sri Lanka"}
        };
    }
    @Test(dataProvider = "addressData", description = "TC02: Save postal address and verify all six fields after reload")
    public void saveAddress(String street1, String street2, String city, String state, String zip, String country) {
        AddressInformationPage address = new AddressInformationPage(driver);
        address.fillAddress(street1, street2, city, state, zip, country);
        address.save();
        contact.reload();
        contact.waitUntilLoaded();
        Assert.assertEquals(address.readAddress(), Map.of("Street 1", street1, "Street 2", street2,
            "City", city, "State/Province", state, "Zip/Postal Code", zip, "Country", country), "The complete address must persist");
    }
    @DataProvider(name = "invalidEmailData")
    public Object[][] invalidEmailData() {
        return new Object[][] {{"Work Email", "invalid-email"}, {"Other Email", "missing-domain@"}};
    }
    @Test(dataProvider = "invalidEmailData", description = "TC03: Reject invalid email, display validation, and preserve previously saved values")
    public void rejectInvalidEmail(String field, String invalidEmail) {
        contact.fillContactDetails("0111234567", "0771234567", "0117654321", "valid.work@example.com", "valid.other@example.com");
        contact.save();
        Map<String, String> before = contact.readContactDetails();
        contact.setEmail(field, invalidEmail);
        contact.attemptSave();
        Assert.assertEquals(contact.emailError(field), "Expected format: admin@example.com", "Invalid email must display its validation message");
        contact.reload();
        contact.waitUntilLoaded();
        Assert.assertEquals(contact.readContactDetails(), before, "A rejected submission must not change saved contact details");
    }
    @DataProvider(name = "emergencyData")
    public Object[][] emergencyData() {
        return new Object[][] {
            {"Test Contact Alpha", "Sibling", "0111234567", "0771234567", "0117654321"},
            {"Test Contact Beta", "Parent", "0201234567", "0781234567", "0207654321"}
        };
    }
    @Test(dataProvider = "emergencyData", description = "TC04: Add, reload, edit, reload, and delete an emergency contact with persistence assertions")
    public void manageEmergencyContact(String name, String relationship, String home, String mobile, String work) {
        EmergencyContactPage emergency = new EmergencyContactPage(driver);
        emergency.open();
        emergency.add(name, relationship, home, mobile, work);
        emergency.reload();
        Assert.assertEquals(emergency.read(name), List.of(name, relationship, home, mobile, work), "New contact must persist");
        String updatedName = name + " Updated";
        emergency.edit(name, updatedName, "Friend", "0311234567", "0791234567", "0317654321");
        emergency.reload();
        Assert.assertEquals(emergency.read(updatedName), List.of(updatedName, "Friend", "0311234567", "0791234567", "0317654321"), "Edited contact must persist");
        Assert.assertFalse(emergency.contains(name), "Original name must no longer be listed");
        emergency.delete(updatedName);
        emergency.reload();
        Assert.assertFalse(emergency.contains(updatedName), "Deleted contact must remain absent after reload");
    }
}
