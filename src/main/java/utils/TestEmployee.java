package utils;

import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.UUID;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/** Creates/deletes only this test's employee via the authenticated demo API.
 * All business behavior is exercised through page objects in the UI. */
public final class TestEmployee {
    private final WebDriver driver;
    private Integer employeeNumber;
    private final String token = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    public TestEmployee(WebDriver driver) { this.driver = driver; }

    @SuppressWarnings("unchecked")
    private Map<String, Object> request(String method, Map<String, Object> payload) {
        Object result = ((JavascriptExecutor) driver).executeAsyncScript("""
            const method = arguments[0], payload = arguments[1], done = arguments[arguments.length - 1];
            fetch('/web/index.php/api/v2/pim/employees', {
                method, credentials: 'same-origin',
                headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                body: JSON.stringify(payload)
            }).then(async r => done({status: r.status, body: await r.json()}))
              .catch(e => done({status: 0, error: String(e)}));
            """, method, payload);
        Map<String, Object> response = (Map<String, Object>) result;
        int status = ((Number) response.get("status")).intValue();
        if (status < 200 || status >= 300) throw new IllegalStateException("Employee fixture " + method + " failed: " + response);
        return (Map<String, Object>) response.get("body");
    }
    @SuppressWarnings("unchecked")
    public void create() {
        Map<String, Object> response = request("POST", Map.of(
            "firstName", "Team12", "middleName", "Automation", "lastName", "Test" + token,
            "employeeId", "T12" + token.substring(0, 7)));
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        employeeNumber = ((Number) data.get("empNumber")).intValue();
        try {
            Path directory = Path.of("target", "test-employees");
            Files.createDirectories(directory);
            Files.writeString(directory.resolve(employeeNumber + ".txt"), "Team12 Automation Test" + token);
        } catch (IOException e) {
            delete();
            throw new IllegalStateException("Cannot track test employee for cleanup", e);
        }
        System.out.println("Created test employee: empNumber=" + employeeNumber + ", name=Team12 Automation Test" + token);
    }
    public int number() {
        if (employeeNumber == null) throw new IllegalStateException("Test employee has not been created");
        return employeeNumber;
    }
    public void delete() {
        if (employeeNumber != null) {
            driver.get(Config.baseUrl() + "/web/index.php/dashboard/index");
            new UiActions(driver).ready();
            request("DELETE", Map.of("ids", new int[]{employeeNumber}));
            System.out.println("Deleted test employee: empNumber=" + employeeNumber);
            try { Files.deleteIfExists(Path.of("target", "test-employees", employeeNumber + ".txt")); }
            catch (IOException e) { throw new IllegalStateException("Cannot remove employee tracking file", e); }
            employeeNumber = null;
        }
    }
}
