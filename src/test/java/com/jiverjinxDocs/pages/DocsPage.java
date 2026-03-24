package com.jiverjinxDocs.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DocsPage {
    WebDriver driver;
    WebDriverWait wait;

    // Original locator — kept exactly as-is
    private By listHyperlinks = By.cssSelector("li strong a");
    private By pageHeader     = By.id("_top");

    // NEW: "Web Portal Guide" and "Student App Guide" buttons on home page
    private By guideButtons   = By.cssSelector("a.sl-link-button");

    public DocsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Original method — unchanged
    public List<WebElement> getActionableLinks() {
        return driver.findElements(listHyperlinks);
    }

    // NEW: returns the two guide buttons on the home page
    public List<WebElement> getGuideButtons() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(guideButtons));
        return driver.findElements(guideButtons);
    }

    public String getLandingPageHeader() {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(pageHeader)
        ).getText();
    }
}