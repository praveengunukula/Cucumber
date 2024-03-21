package com.ieagle.auto.site.util;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.ieagle.auto.site.exceptions.WebElementClickException;

public class WebDriverUtil {
	
	
	public static String getTextByHandlingNullPointers(WebElement parent, By childLocator)
	{
		if(parent == null)
		{  return "";  }
		
		return getTextIfElementPresentInParent(parent,childLocator);
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
	
	public static void moveToAndClickElement(WebDriver driver, WebElement element )
	{
		for(int i=0;i<3;i++)
		{
			try
			{
				moveToElement(driver, element);
				element.click();
				return;
			}
			catch(Exception e)
			{
				if(i==2)
				{
					String exMsg = "Element or its locator may have changed. " + 
									 "Can not find or click the element: ";
					WebElementClickException ex = new WebElementClickException
							(exMsg + element.toString() + " due to " +  
								e.getClass().getSimpleName() + " " + e.getMessage(), e);
					throw ex;
				}
				scrollToPageTop(driver);
			}
		}
	}
	
	
	public static void killChromeDriverExe() throws IOException, InterruptedException
	{
		Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
		Thread.sleep(2000);
		Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
		Thread.sleep(2000);
	}
}
