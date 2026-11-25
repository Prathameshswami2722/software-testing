package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExtentManager {
    private static ExtentReports report;
    private ExtentManager() { }
    public static synchronized ExtentReports getReport() {
        if (report == null) {
            try { Files.createDirectories(Path.of("target", "extent-report", "screenshots")); }
            catch (IOException e) { throw new IllegalStateException("Cannot create report directory", e); }
            ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report/index.html");
            spark.config().setDocumentTitle("Team 12 Test Results");
            spark.config().setReportName("Employee Contact Information - OrangeHRM");
            report = new ExtentReports();
            report.attachReporter(spark);
            report.setSystemInfo("Project", "Team 12 - Employee Contact Information");
            report.setSystemInfo("Application", Config.baseUrl());
            report.setSystemInfo("Java", System.getProperty("java.version"));
            report.setSystemInfo("Headless", System.getProperty("headless", "true"));
        }
        return report;
    }
}
