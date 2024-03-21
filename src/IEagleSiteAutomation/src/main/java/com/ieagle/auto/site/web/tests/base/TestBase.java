package com.ieagle.auto.site.web.tests.base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.ieagle.auto.site.reporting.ExtentManager;
import com.ieagle.auto.site.reporting.TestReport;
import com.ieagle.auto.site.util.DataDrivenManager;
import com.ieagle.auto.site.util.WebDriverUtil;

import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class TestBase {
	private static final String EXTENT_REPORT_FILE_PATH = "AutomationReport.html";
	private static final String MASTER_TEST_DATA_FILE_PATH = "MasterTestDataFile.xlsx";
	private static final String MASTER_TEST_DATA_FILE_SHEET_NAME = "TestDataSheet";

	private WebDriver driver;

	protected static ExtentReports extentReport;
	protected static ThreadLocal<ExtentTest> erTestThread = new ThreadLocal<ExtentTest>();
	protected ExtentTest erTest; // For TestNG Test Method
	protected TestReport testReport; // For TestNG Test Method

	@BeforeSuite
	public void automationBeforeSuite() throws Exception {

	}

	@BeforeSuite()
	public void extentReportBeforeSuite() throws FileNotFoundException, IOException {
		extentReport = ExtentManager.createInstance(EXTENT_REPORT_FILE_PATH);
		ExtentSparkReporter sparkReporter = new ExtentSparkReporter(EXTENT_REPORT_FILE_PATH);
		extentReport.attachReporter(sparkReporter);
	}

	@DataProvider
	public Object[][] masterTestDataFileDataProvider(Method method) throws Exception {
		DataDrivenManager ddm = new DataDrivenManager(MASTER_TEST_DATA_FILE_PATH);
		return ddm.getTestCaseDataSets(MASTER_TEST_DATA_FILE_SHEET_NAME, getClass_MethodFormatName(method));
	}

	@BeforeMethod
	public void testSetup() throws Exception {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--blink-settings=imagesEnabled=false");
		options.addArguments("--disable-cache");
		options.addArguments("--disable-network-throttling");
		options.addArguments("--log-level=3");
		options.addArguments("--disable-gpu");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--disk-cache-size=0");

		try {

			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);
			// WebDriverManager.firefoxdriver().setup();
			// driver = new FirefoxDriver();
		} catch (Exception e) {
			testReport.log(Status.INFO,
					"Unable to start new browser session. Running killChromeDriverExe() & trying again");
			WebDriverUtil.killChromeDriverExe();
			WebDriverManager.chromedriver().clearDriverCache().setup();
			driver = new ChromeDriver(options);
		}
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		// driver.manage().window().maximize(); // Comment as we are using chrome option start-maximized
	}

	@AfterMethod
	public void teardown(ITestResult result) throws Exception {
		try {
			driver.quit();
		} catch (Exception e) {
			testReport.log(Status.INFO, "Unable to stop the browser session. Running killChromeDriverExe()");
			WebDriverUtil.killChromeDriverExe();
		}
	}

	@BeforeMethod
	public synchronized void extentReportBeforeMethod(Method method) {
		erTest = extentReport.createTest(getClass_MethodFormatName(method));
		erTestThread.set(erTest);
		testReport = new TestReport(erTest);
	}

	private String getClass_MethodFormatName(Method method) {
		return method.getDeclaringClass().getSimpleName() + "_" + method.getName();
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

	public synchronized void addExtentTestAutomationFailed(String testName, Throwable t) {
		erTest = extentReport.createTest(testName);
		erTestThread.set(erTest);
		erTest.log(Status.FAIL, t);
		extentReport.flush();
	}
}
