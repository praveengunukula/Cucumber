package com.ieagle.auto.site.web.tests;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.ieagle.auto.site.web.tests.base.TestBase;

public class HomePageTest extends TestBase {
	
	@Test
	public void testPageTitle()
	{
		
	}
	
	@Test(dataProvider = "masterTestDataFileDataProvider")
	public void testGetDataProviderData(String tripType, String fromAirport, String toAirport)
	{
		testReport.log(Status.INFO, tripType + "," + fromAirport + "," + toAirport);
	}
}
