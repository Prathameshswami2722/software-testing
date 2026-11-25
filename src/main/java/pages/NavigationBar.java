package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.UiActions;

/** Shared navigation from the PDF, with explicit waits. */
public class NavigationBar extends UiActions {
    public NavigationBar(WebDriver driver) { super(driver); }
    private void open(String module) {
        click(By.xpath("//span[normalize-space()='" + module + "']"));
        ready();
    }
    public void openPIM() { open("PIM"); }
    public void openAdmin() { open("Admin"); }
    public void openLeave() { open("Leave"); }
    public void openDirectory() { open("Directory"); }
    public void openRecruitment() { open("Recruitment"); }
    public void openMyInfo() { open("My Info"); }
}
