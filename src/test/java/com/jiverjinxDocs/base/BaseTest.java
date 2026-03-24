package com.jiverjinxDocs.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.jiverjinxDocs.utils.ExtentManager;
import com.jiverjinxDocs.utils.ExtentTestManager;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    public WebDriver driver;
    public Properties prop;
    public WebDriverWait wait;

    private static ExtentReports extent;

    // ── Suite-level: create report once ──────────────────────────────────────
    @BeforeSuite
    public void initReport() {
        extent = ExtentManager.getInstance();
    }

    // ── Method-level: start browser + create ExtentTest node ─────────────────
    @BeforeMethod
    public void setUp(java.lang.reflect.Method method) {
        prop = loadProperties();
        String browserName = prop.getProperty("browser", "chrome").trim().toLowerCase();
        boolean isHeadless = Boolean.parseBoolean(prop.getProperty("headless", "false").trim());

        driver = createDriver(browserName, isHeadless);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(prop.getProperty("docs.url"));

        // Create a test node in the report named after the @Test method
        ExtentTest test = extent.createTest(method.getName());
        ExtentTestManager.setTest(test);
    }

    // ── Method-level: quit browser + mark test PASS/FAIL in report ───────────
    @AfterMethod
    public void tearDown(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();

        switch (result.getStatus()) {
            case ITestResult.SUCCESS -> test.log(Status.PASS, "Test passed");
            case ITestResult.FAILURE -> {
                test.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
                test.log(Status.FAIL, "<pre>" + result.getThrowable() + "</pre>");
            }
            case ITestResult.SKIP -> test.log(Status.SKIP, "Test skipped");
        }

        ExtentTestManager.removeTest();

        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // ── Suite-level: flush HTML report + generate PDF ────────────────────────
    @AfterSuite
    public void flushReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("\n📊 HTML Report: " + ExtentManager.getHtmlReportPath());

            // Convert HTML → PDF (same timestamp, same run)
            ExtentManager.generatePdfReport();
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    private Properties loadProperties() {
        Properties properties = new Properties();
        String path = System.getProperty("user.dir") + "/src/test/resources/config.properties";
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("❌ Cannot load config.properties at: " + path, e);
        }
        return properties;
    }

    private WebDriver createDriver(String browserName, boolean isHeadless) {
        switch (browserName) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions cOpts = new ChromeOptions();
                if (isHeadless) cOpts.addArguments("--headless=new", "--window-size=1920,1080");
                return new ChromeDriver(cOpts);
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions fOpts = new FirefoxOptions();
                if (isHeadless) fOpts.addArguments("--headless");
                return new FirefoxDriver(fOpts);
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions eOpts = new EdgeOptions();
                if (isHeadless) eOpts.addArguments("--headless=new");
                return new EdgeDriver(eOpts);
            default:
                throw new IllegalArgumentException("❌ Unsupported browser: " + browserName);
        }
    }
}