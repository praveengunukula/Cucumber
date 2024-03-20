package com.test.web.tests.base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.indianeagle.auto.fareresearch.domain.MavenProfile;
import com.indianeagle.auto.fareresearch.reporting.ExtentManager;
import com.indianeagle.auto.fareresearch.reporting.TestReport;

public abstract class TestBase {

	
	
	protected static ExtentReports extentReport;
	protected static ThreadLocal<ExtentTest> erTestThread = new ThreadLocal<ExtentTest>();
	protected ExtentTest erTest;  // For TestNG Test Method
	protected TestReport testReport;  // For TestNG Test Method
	
	@BeforeSuite()
	public void suiteSetup() throws FileNotFoundException, IOException
	{
		extentReport = ExtentManager.createInstance(EXTENT_REPORT_FILE_PATH );
		ExtentSparkReporter sparkReporter = new ExtentSparkReporter(EXTENT_REPORT_FILE_PATH);
		extentReport.attachReporter(sparkReporter);
	}
		
	    @BeforeMethod
	    public synchronized void extentReportBeforeMethod(Method method) {
	        erTest =  extentReport.createTest(method.getName());
	        erTestThread.set(erTest);
	        testReport = new TestReport(erTest);
	    }

	    @AfterMethod
	    public synchronized void extentReportAfterMethod(ITestResult result) throws IOException {
	        if (result.getStatus() != ITestResult.SUCCESS)
	        	// Fail the erTest when TestNG test result is not success
//	            erTest.fail(result.getThrowable(), 
//									MediaEntityBuilder
//									.createScreenCaptureFromPath(getTestFailureScreenshot(result))
//									.build());
	        	erTest.fail(result.getThrowable());	         
	        else
	        	// Pass the erTest when TestNG test result is success
	           erTest.pass("TEST PASSED");
	        
	        extentReport.flush();
	    }
	    
	    public synchronized void addAutomationReportFailedTest(String testName, Throwable t) {
	        addAutomationReportTest(testName, Status.FAIL, t);
	    }
	    
	    public synchronized void addAutomationReportWarningTest(String testName, Throwable t) {
	        addAutomationReportTest(testName, Status.WARNING, t);
	    }
	    
	    public synchronized void addAutomationReportInfoTest(String testName, Throwable t) {
	        addAutomationReportTest(testName, Status.INFO, t);
	    }
	    
	    private  synchronized void addAutomationReportTest(String testName, Status status, Throwable t) {
	    	System.out.println(status + ": " + testName + " || EXCEPTION: " + t);
	    	erTest =  extentReport.createTest(testName);
	        erTestThread.set(erTest);
	        erTest.log(status, t);
	        extentReport.flush();
	    }
	    
	    
//		public String getTestFailureScreenshot(ITestResult result) throws IOException
//		{
//			String testFailureScreenshotPath = null;
//			if (result.getStatus() == ITestResult.FAILURE)
//			{
//				// Gives path like TestFailureScreenshots\com.company.tests.HomePageTest.testPageTitle.png
//				testFailureScreenshotPath = "TestFailureScreenshots/" 
//														+ this.getClass().getName() // full class name - com.company.tests.HomePageTest
//														+ "." 
//														+ result.getName() // test method name - testPageTitle
//														+ ".png";
//				
//				// Files, Paths classes are provided by java.nio.file package
//				// Create the directory if doesn't exist
//				if(Files.notExists(Paths.get("TestFailureScreenshots")))
//				{
//					Files.createDirectory(Paths.get("TestFailureScreenshots"));
//				}
//				
//				// Delete the old file if exists
//				Files.deleteIfExists(Paths.get(testFailureScreenshotPath));
//				
//				// Create new test failure screenshot file
//				WebDriverUtil.getScreenshot(driver, testFailureScreenshotPath);
//			}
//			return testFailureScreenshotPath;
//		}

	    @AfterSuite
	    public void afterSuite()
	    {
	    	if(extentReport!=null)
	    		extentReport.flush();
	    }
}
