package com.jiverjinxDocs.tests;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.jiverjinxDocs.base.BaseTest;
import com.jiverjinxDocs.pages.SideNavPage;

public class SideNavRedirectTest extends BaseTest {

    @Test
    public void verifyStudentAppOverviewSideNav() {
        runSideNavAudit("https://docs.jiverjinx.com/getting-started/student-app-overview/",
                        "Student App Overview");
    }

    @Test
    public void verifyWebPortalOverviewSideNav() {
        runSideNavAudit("https://docs.jiverjinx.com/getting-started/web-portal-overview/",
                        "Web Portal Overview");
    }

    private void runSideNavAudit(String startUrl, String testLabel) {
        SoftAssert softAssert = new SoftAssert();
        SideNavPage page = new SideNavPage(driver);

        // Navigate to the overview page
        driver.get(startUrl);

        // Collect all hrefs + texts BEFORE navigating away — avoids StaleElementReferenceException
        List<String> hrefs     = new ArrayList<>();
        List<String> linkTexts = new ArrayList<>();

        for (var link : page.getSideNavLinks()) {
            String href = link.getAttribute("href");
            String text = link.getText().trim();
            // Skip anchor-only links (e.g. "#_top") and blank text (section headers)
            if (href != null && !href.endsWith("#_top") && !text.isEmpty()) {
                hrefs.add(href);
                linkTexts.add(text);
            }
        }

        System.out.println("\n" + "=".repeat(170));
        System.out.println("TEST: " + testLabel + " | Start URL: " + startUrl);
        System.out.println("=".repeat(170));
        System.out.println(String.format("%-4s | %-40s | %-55s | %-55s | %-40s | %-10s",
            "#", "LINK TEXT", "URL (HREF)", "FINAL REDIRECTED URL", "H1 HEADER", "RESULT"));
        System.out.println("-".repeat(170));

        for (int i = 0; i < hrefs.size(); i++) {
            String linkText    = linkTexts.get(i);
            String intendedUrl = hrefs.get(i);

            driver.get(intendedUrl);

            String finalUrl     = driver.getCurrentUrl();
            String actualHeader = page.getLandingPageHeader();
            boolean isMatch     = actualHeader.equalsIgnoreCase(linkText);
            String status       = isMatch ? "PASS ✅" : "FAIL ❌";

            System.out.println(String.format("%-4s | %-40s | %-55s | %-55s | %-40s | %-10s",
                (i + 1),
                truncate(linkText, 39),
                truncate(intendedUrl, 54),
                truncate(finalUrl, 54),
                truncate(actualHeader, 39),
                status));

            softAssert.assertTrue(isMatch,
                "[" + testLabel + "] [" + (i+1) + "] MISMATCH — " +
                "Link: \"" + linkText + "\" | H1: \"" + actualHeader + "\" | URL: " + finalUrl);
        }

        System.out.println("=".repeat(170));
        System.out.printf("Total Links Tested: %d%n%n", hrefs.size());
        softAssert.assertAll();
    }

    private String truncate(String str, int size) {
        if (str == null) return "N/A";
        return (str.length() > size) ? str.substring(0, size - 3) + "..." : str;
    }
}