package com.test.web.pages.base;

import org.openqa.selenium.WebDriver;

import com.indianeagle.auto.fareresearch.reporting.TestReport;

public abstract class PageComponentBase extends POMBase {
	
	public PageComponentBase(WebDriver driver, TestReport testReport)
	{
		super(driver,testReport);
	}
}
