package utils;

import com.aventstack.extentreports.ExtentTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.*;

/** Records test results and configuration failures, capturing before driver teardown. */
public class ExtentTestListener implements ITestListener, IConfigurationListener {
    private final ThreadLocal<ExtentTest> current = new ThreadLocal<>();
    @Override public void onStart(ITestContext context) { ExtentManager.getReport(); }
    @Override public void onTestStart(ITestResult result) {
        current.set(ExtentManager.getReport().createTest(result.getMethod().getMethodName()
            + " " + Arrays.toString(result.getParameters()), result.getMethod().getDescription()));
    }
    private ExtentTest entry(ITestResult result) {
        if (current.get() == null) current.set(ExtentManager.getReport().createTest(result.getName()));
        return current.get();
    }
    private void screenshot(ExtentTest entry) {
        if (!(DriverFactory.currentDriver() instanceof TakesScreenshot camera)) return;
        try {
            String file = UUID.randomUUID() + ".png";
            Files.write(Path.of("target", "extent-report", "screenshots", file), camera.getScreenshotAs(OutputType.BYTES));
            entry.addScreenCaptureFromPath("screenshots/" + file);
        } catch (Exception e) { entry.warning("Screenshot unavailable: " + e.getMessage()); }
    }
    @Override public void onTestSuccess(ITestResult result) {
        ExtentTest entry = entry(result);
        entry.pass("All assertions passed"); screenshot(entry); current.remove();
        System.out.println("PASS: " + result.getMethod().getMethodName() + " " + Arrays.toString(result.getParameters()));
        ExtentManager.getReport().flush();
    }
    @Override public void onTestFailure(ITestResult result) {
        ExtentTest entry = entry(result);
        entry.fail(result.getThrowable()); screenshot(entry); current.remove();
        System.out.println("FAIL: " + result.getMethod().getMethodName() + " - " + result.getThrowable());
        ExtentManager.getReport().flush();
    }
    @Override public void onTestSkipped(ITestResult result) {
        ExtentTest entry = entry(result);
        if (result.getThrowable() != null) entry.skip(result.getThrowable()); else entry.skip("Skipped");
        current.remove();
    }
    @Override public void onConfigurationFailure(ITestResult result) {
        ExtentTest entry = ExtentManager.getReport().createTest("Configuration: " + result.getName());
        entry.fail(result.getThrowable()); screenshot(entry);
        ExtentManager.getReport().flush();
    }
    @Override public void onFinish(ITestContext context) { ExtentManager.getReport().flush(); }
}
