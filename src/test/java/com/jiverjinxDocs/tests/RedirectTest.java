package com.jiverjinxDocs.tests;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.jiverjinxDocs.base.BaseTest;
import com.jiverjinxDocs.pages.DocsPage;
import com.jiverjinxDocs.pages.SideNavPage;
import com.jiverjinxDocs.utils.ExcelReader;
import com.jiverjinxDocs.utils.ExcelReader.TestRow;
import com.jiverjinxDocs.utils.ExtentTestManager;

public class RedirectTest extends BaseTest {

    // ── Test 1: Home page Guide Buttons (H1, H2) ──────────────────────────────
    @Test
    public void verifyHomePageGuideButtons() {
        ExtentTest test = ExtentTestManager.getTest();
        test.assignCategory("Home Page");
        test.info("Verifying guide button redirects on home page");
        System.out.println("\n🔎 [TEST START] verifyHomePageGuideButtons");

        DocsPage docs = new DocsPage(driver);
        SoftAssert softAssert = new SoftAssert();

        List<TestRow> buttonRows = ExcelReader.getHomePageButtonRows(
            ExcelReader.readTestData()
        );

        List<WebElement> buttons  = docs.getGuideButtons();
        List<String>     btnHrefs = new ArrayList<>();
        for (WebElement btn : buttons) btnHrefs.add(btn.getAttribute("href"));

        for (TestRow row : buttonRows) {
            String expectedUrl = row.expectedUrl;

            driver.get(expectedUrl);
            String finalUrl     = driver.getCurrentUrl();
            String actualHeader = docs.getLandingPageHeader().trim();
            boolean urlMatch    = finalUrl.contains(
                expectedUrl.replace("https://docs.jiverjinx.com", ""));

            logRow(test, row.testId, row.section, row.linkName,
                   expectedUrl, finalUrl, actualHeader, urlMatch);

            softAssert.assertTrue(urlMatch,
                "[ID=" + row.testId + "] Expected URL: " + expectedUrl
                + " | Final: " + finalUrl);

            driver.get(prop.getProperty("docs.url"));
        }

        softAssert.assertAll();
        System.out.println("✅ [TEST END] verifyHomePageGuideButtons");
    }

    // ── Test 2: Home page body links ──────────────────────────────────────────
    @Test
    public void verifyHomePageLinks() {
        ExtentTest test = ExtentTestManager.getTest();
        test.assignCategory("Home Page");
        test.info("Verifying body links on home page (li strong a)");
        System.out.println("\n🔎 [TEST START] verifyHomePageLinks");

        DocsPage docs = new DocsPage(driver);
        SoftAssert softAssert = new SoftAssert();
        int linkCount = docs.getActionableLinks().size();

        test.info("Total links found on page: " + linkCount);
        System.out.println("📋 Total links found on page: " + linkCount);

        for (int i = 0; i < linkCount; i++) {
            WebElement currentLink = docs.getActionableLinks().get(i);
            String linkText    = currentLink.getText().trim();
            String intendedUrl = currentLink.getAttribute("href");

            currentLink.click();

            String finalUrl     = driver.getCurrentUrl();
            String actualHeader = docs.getLandingPageHeader().trim();
            boolean isMatch     = actualHeader.equalsIgnoreCase(linkText);

            logRow(test, String.valueOf(i + 1), "Home Page Body",
                   linkText, intendedUrl, finalUrl, actualHeader, isMatch);

            softAssert.assertTrue(isMatch,
                "Mismatch — Link: \"" + linkText
                + "\" | H1: \"" + actualHeader + "\" | URL: " + finalUrl);

            driver.navigate().back();
        }

        softAssert.assertAll();
        System.out.println("✅ [TEST END] verifyHomePageLinks");
    }

    // ── Test 3: Student App Overview side nav ─────────────────────────────────
    @Test
    public void verifyStudentAppOverviewSideNav() {
        ExtentTestManager.getTest().assignCategory("Side Nav");
        System.out.println("\n🔎 [TEST START] verifyStudentAppOverviewSideNav");
        runExcelDrivenAudit(
            "https://docs.jiverjinx.com/getting-started/student-app-overview/",
            "Student App Overview"
        );
    }

    // ── Test 4: Web Portal Overview side nav ──────────────────────────────────
    @Test
    public void verifyWebPortalOverviewSideNav() {
        ExtentTestManager.getTest().assignCategory("Side Nav");
        System.out.println("\n🔎 [TEST START] verifyWebPortalOverviewSideNav");
        runExcelDrivenAudit(
            "https://docs.jiverjinx.com/getting-started/web-portal-overview/",
            "Web Portal Overview"
        );
    }

    // ── Shared: Excel-driven side nav audit ───────────────────────────────────
    private void runExcelDrivenAudit(String startUrl, String testLabel) {
        ExtentTest test = ExtentTestManager.getTest();
        test.info("Start URL: " + startUrl);
        System.out.println("🌐 Start URL: " + startUrl);

        SoftAssert softAssert = new SoftAssert();
        SideNavPage page = new SideNavPage(driver);

        List<TestRow> rows = ExcelReader.getSideNavRows(ExcelReader.readTestData());
        test.info("Total rows loaded from Excel: " + rows.size());
        System.out.println("📋 Total rows loaded from Excel: " + rows.size());

        for (TestRow row : rows) {
            driver.get(row.expectedUrl);

            String finalUrl     = driver.getCurrentUrl();
            String actualHeader = page.getLandingPageHeader();
            boolean isMatch     = actualHeader.equalsIgnoreCase(row.linkName);

            logRow(test, row.testId, row.section, row.linkName,
                   row.expectedUrl, finalUrl, actualHeader, isMatch);

            softAssert.assertTrue(isMatch,
                "[" + testLabel + "] [ID=" + row.testId + "] [" + row.section + "] " +
                "Link: \"" + row.linkName + "\" | H1: \"" + actualHeader
                + "\" | URL: " + finalUrl);
        }

        test.info("Total tested: " + rows.size());
        System.out.println("📊 Total tested: " + rows.size());
        System.out.println("✅ [TEST END] " + testLabel);
        softAssert.assertAll();
    }

    // ── Shared: log one row + print to console ────────────────────────────────
    private void logRow(ExtentTest test,
                        String testId, String section,
                        String linkName, String expectedUrl,
                        String finalUrl, String actualHeader,
                        boolean passed) {

        // ── Console print ─────────────────────────────────────────────────────
        String resultIcon = passed ? "✅ PASS" : "❌ FAIL";
        System.out.printf("  %s | [ID=%-5s] | %-30s | %-20s | H1: %s%n",
            resultIcon,
            testId,
            section,
            linkName,
            actualHeader
        );
        if (!passed) {
            System.out.printf("       Expected : %s%n", expectedUrl);
            System.out.printf("       Final URL: %s%n", finalUrl);
        }

        // ── Extent report ─────────────────────────────────────────────────────
        String statusBadge = passed
            ? "<span style='color:green;font-weight:bold;'>&#10004; PASS</span>"
            : "<span style='color:red;font-weight:bold;'>&#10008; FAIL</span>";

        String row = "<tr>"
            + "<td>" + testId + "</td>"
            + "<td>" + section + "</td>"
            + "<td>" + linkName + "</td>"
            + "<td><a href='" + expectedUrl + "' target='_blank'>"
                + shorten(expectedUrl, 60) + "</a></td>"
            + "<td><a href='" + finalUrl + "' target='_blank'>"
                + shorten(finalUrl, 60) + "</a></td>"
            + "<td>" + actualHeader + "</td>"
            + "<td>" + statusBadge + "</td>"
            + "</tr>";

        String current = test.getModel().getDescription() == null
            ? "" : test.getModel().getDescription();
        test.getModel().setDescription(current + row);

        Status status = passed ? Status.PASS : Status.FAIL;
        test.log(status,
            "[" + testId + "] " + section + " › " + linkName
            + (passed ? "" : " | H1 found: \"" + actualHeader + "\""));
    }

    // ── AfterMethod: flush full HTML table into report ────────────────────────
    @org.testng.annotations.AfterMethod(alwaysRun = true)
    public void appendTableToReport(org.testng.ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();
        if (test == null) return;

        String rows = test.getModel().getDescription();
        if (rows != null && !rows.isEmpty()) {
            String table = "<table border='1' cellpadding='5' cellspacing='0' "
                + "style='border-collapse:collapse; width:100%; font-size:12px;'>"
                + "<thead style='background:#1F4E79; color:white;'>"
                + "<tr>"
                + "<th>ID</th><th>Section</th><th>Link Name</th>"
                + "<th>Expected URL</th><th>Final URL</th>"
                + "<th>H1 Header</th><th>Result</th>"
                + "</tr></thead>"
                + "<tbody>" + rows + "</tbody>"
                + "</table>";
            test.info(table);
        }
    }

    private String shorten(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }
}