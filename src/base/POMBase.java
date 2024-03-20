package com.test.web.pages.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.indianeagle.auto.fareresearch.reporting.TestReport;

public abstract class POMBase {
	
	protected final WebDriver driver;
	protected final TestReport testReport;
	
	public POMBase(WebDriver driver, TestReport testReport)
	{
		this.driver = driver;
		this.testReport = testReport;
		PageFactory.initElements(driver, this);
	}
}
