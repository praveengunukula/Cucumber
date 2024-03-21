package com.ieagle.auto.site.web.tests;

import java.util.List;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.ieagle.auto.site.web.tests.base.TestBase;

public class CheckoutPageTest extends TestBase {
	
	@Test
	public void testPageTitle()
	{

	}
	
	@Test(dataProvider = "masterTestDataFileDataProvider")
	public void testGetDataProviderData(String username, String password)
	{
		testReport.log(Status.INFO, username + "," + password);
	}
}
