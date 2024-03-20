package com.test.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;

public class TestReport {
	
	private ExtentTest extentTest;
	
	public TestReport(ExtentTest extentTest) {
		this.extentTest = extentTest;
	}
	
	public void log(Status status, String details)
	{
		extentTest.log(status, details);
		System.out.println(status.name() + ": " + details);
	}
	
	public void log(Status status, Throwable t)
	{
		extentTest.log(status, t);
		System.out.println(status.name() + ": " + t);
	}
	
	public void log(Status status, String details, Media media)
	{
		extentTest.log(status, details,media);
		System.out.println(status.name() + ": " + details);
		System.out.println(status.name() + " Media: " + media);
	}
	
	public void log(Status status, Throwable t, Media media)
	{
		extentTest.log(status, t, media);
		System.out.println(status.name() + ": " + t);
		System.out.println(status.name() + " Media: " + media);
	}
	
	@Deprecated
	public void tempLog(Status status, String details){
		log(status,details);
	}
	
	@Deprecated
	public void tempLog(Status status, Throwable t){
		log(status,t);
	}
}
