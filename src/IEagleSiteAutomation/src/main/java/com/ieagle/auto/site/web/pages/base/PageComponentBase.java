package com.ieagle.auto.site.web.pages.base;

import org.openqa.selenium.WebDriver;

import com.ieagle.auto.site.reporting.TestReport;

public abstract class PageComponentBase extends POMBase {
	
	public PageComponentBase(WebDriver driver, TestReport testReport)
	{
		super(driver,testReport);
	}
}
