package com.test.utils;

import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.indianeagle.auto.fareresearch.exceptions.KayakElementClickException;
import com.indianeagle.auto.fareresearch.exceptions.KayakSearchPageException;

public class WebDriverUtil {
	
	public static String getTextByHandlingNullPointers(WebElement parent, By childLocator)
	{
		if(parent == null)
		{  return "";  }
		
		return getTextIfElementPresentInParent(parent,childLocator);
	}
	
	public static void clickIfElementPresent(WebDriver driver, By by)
	{
		if(isElementPresent(driver,by))
		{
			waitAndClickElement(driver,by);
		}
	}
	
	public static void clickIfElementPresentByJSE(WebDriver driver, String cssSelector)
	{
		if(isElementPresentByJSE(driver,cssSelector))
		{
			waitAndClickElement(driver,By.cssSelector(cssSelector));
		}
	}
	
	private static void waitAndClickElement(WebDriver driver, By by)
	{
		WebDriverWait waitForCilckable = new WebDriverWait(driver, Duration.ofSeconds(10));
		waitForCilckable.until(ExpectedConditions.elementToBeClickable(by));
		//BrowserWaitUtil.randomeUserActionWait();
		moveToAndClickElement(driver, driver.findElement(by),500);
	}
	
	public static String getTextIfElementPresent(WebDriver driver, By by)
	{
		if(isElementPresent(driver,by))
		{
			return driver.findElement(by).getText().trim();
		}
		else
		{
			return "";
		}
	}
	
	public static String getTextIfElementPresentInParent(WebElement parent, By childLocator)
	{
		if(isElementPresentInParent(parent,childLocator))
		{
			return parent.findElement(childLocator).getText().trim();
		}
		else
		{
			return "";
		}
	}
	
	public static boolean isElementPresent(WebDriver driver, By byLocator)
	{
		try
		{
			driver.findElement(byLocator);
			return true;
		}
		catch (NoSuchElementException e)
		{ return false; }
	}
	
	public static void scrollToPageTop(WebDriver driver)
	{
		((JavascriptExecutor) driver).executeScript(" window.scrollTo(0, 0)");
	}
	
	public static boolean isElementPresentByJSE(WebDriver driver, String cssSelector)
	{
		
		String jseQuery = "return document.querySelector(\"" + cssSelector + "\");";
		Object queryResult = ((JavascriptExecutor) driver).executeScript(jseQuery);
		if(queryResult==null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public static boolean isElementPresentInParent(WebElement parent, By childLocator)
	{
		try
		{
			parent.findElement(childLocator);
			return true;
		}
		catch (NoSuchElementException e)
		{ return false; }
	}
	
	public static void moveToElement(WebDriver driver, WebElement element )
	{
		new Actions(driver).scrollToElement(element).
							moveToElement(element,0,0).
							build().
							perform();
	}

	public static void moveToAndClickElement(WebDriver driver, WebElement element, int waitMilliSecs )
	{
		final int maxWaitMilliSecs = 3000;
		if(waitMilliSecs>maxWaitMilliSecs)
			throw new IllegalArgumentException(
					String.format("waitMilliSecs = %s. Wait time above %s milli seconds is not allowed.", waitMilliSecs, maxWaitMilliSecs));
		
		for(int i=0;i<3;i++)
		{
			try	{
				BrowserWaitUtil.waitForMilliSeconds(waitMilliSecs);
				moveToElement(driver, element);
				element.click();
				return;
			}
			catch(Exception e){
				if(i==2){
					String exMsg = "Element or its locator may have changed. Can not find or click the element: ";
					KayakElementClickException ex = new KayakElementClickException
								(exMsg + element.toString() + " due to " +  
								e.getClass().getSimpleName() + " " + e.getMessage(), e);
					throw ex;
				}
				scrollToPageTop(driver);
			}
		}
	}
	
	
	public static void killChromeDriverExe() throws IOException
	{
		Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
		BrowserWaitUtil.waitForMilliSeconds(1000);
		Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
		BrowserWaitUtil.waitForMilliSeconds(1000);
	}
}
