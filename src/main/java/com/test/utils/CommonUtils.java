package com.test.utils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

public class CommonUtils {

	public static void clickElement(WebElement element) {
	    element.click();
	}
	public static void sendKeysToElement(WebElement element, String text) {
	    element.clear();
	    element.sendKeys(text);
	}
	public static String getElementText(WebElement element) {
	    return element.getText();
	}
	public static void scrollToElement(WebDriver driver, WebElement element) {
	    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}
	public static void waitForElementClickable(WebDriver driver, WebElement element, Duration timeoutInSeconds) {
	    WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
	    wait.until(ExpectedConditions.elementToBeClickable(element));
	}
	public static void waitForElementVisible(WebDriver driver, WebElement element, Duration timeoutInSeconds) {
	    WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
	    wait.until(ExpectedConditions.visibilityOf(element));
	}
	public static void setImplicitWait(WebDriver driver, int timeoutInSeconds) {
	    driver.manage().timeouts().implicitlyWait(timeoutInSeconds, TimeUnit.SECONDS);
	}
	public static void waitForElementPresence(WebDriver driver, By locator, Duration timeoutInSeconds) {
	    WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
	    wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}
	public static void waitForElementToBeClickable(WebDriver driver, By locator, Duration timeoutInSeconds) {
	    WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
	    wait.until(ExpectedConditions.elementToBeClickable(locator));
	}
	public static void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
