package com.jiverjinxDocs.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SideNavPage {
    WebDriver driver;
    WebDriverWait wait;

    // Correct locators confirmed from actual site HTML
    private By sideNavLinks = By.cssSelector("#starlight__sidebar ul li > a");
    private By pageHeader   = By.id("_top");

    public SideNavPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public List<WebElement> getSideNavLinks() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(sideNavLinks));
        return driver.findElements(sideNavLinks);
    }

    public String getLandingPageHeader() {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(pageHeader)
        ).getText().trim();
    }
}