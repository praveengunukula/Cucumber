package com.test.web.tests.base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import com.aventstack.extentreports.Status;
import com.indianeagle.auto.fareresearch.domain.Itinerary;
import com.indianeagle.auto.fareresearch.domain.MavenProfile;
import com.indianeagle.auto.fareresearch.enums.CaptchaStatusType;
import com.indianeagle.auto.fareresearch.enums.KayakSearchStatusType;
import com.indianeagle.auto.fareresearch.enums.SearchAPISearchStatusType;
import com.indianeagle.auto.fareresearch.exceptions.BuildReportCSVException;
import com.indianeagle.auto.fareresearch.exceptions.HadoopConfigException;
import com.indianeagle.auto.fareresearch.exceptions.KayakCaptchaException;
import com.indianeagle.auto.fareresearch.exceptions.KayakCaptchaTryAgainLaterException;
import com.indianeagle.auto.fareresearch.exceptions.NewRoutesFRException;
import com.indianeagle.auto.fareresearch.exceptions.OutputToS3ProcessException;
import com.indianeagle.auto.fareresearch.exceptions.SearchCriteriaCSVException;
import com.indianeagle.auto.fareresearch.exceptions.SearchReportCSVException;
import com.indianeagle.auto.fareresearch.searchapi.IESearchAPI;
import com.indianeagle.auto.fareresearch.searchapi.IESearchAPIData;
import com.indianeagle.auto.fareresearch.searchcriteria.NewRouteSearchCriteria;
import com.indianeagle.auto.fareresearch.searchcriteria.RouteSearchCriteriaBase;
import com.indianeagle.auto.fareresearch.searchcriteria.SupportedRouteSearchCriteria;
import com.indianeagle.auto.fareresearch.searchreport.JenkinsBuildReport;
import com.indianeagle.auto.fareresearch.searchreport.NewRouteSearchReport;
import com.indianeagle.auto.fareresearch.searchreport.RouteSearchReportBase;
import com.indianeagle.auto.fareresearch.searchreport.SupportedRouteSearchReport;
import com.indianeagle.auto.fareresearch.util.BrowserUserAgentsUtil;
import com.indianeagle.auto.fareresearch.util.BrowserWaitUtil;
import com.indianeagle.auto.fareresearch.util.CSVFileUtil;
import com.indianeagle.auto.fareresearch.util.CSVToParquet;
import com.indianeagle.auto.fareresearch.util.EmailSenderUtil;
import com.indianeagle.auto.fareresearch.util.S3Util;
import com.indianeagle.auto.fareresearch.util.StringUtil;
import com.indianeagle.auto.fareresearch.util.WebDriverUtil;
import com.indianeagle.auto.fareresearch.web.pages.kayak.KayakSearchResultsPage;
import com.indianeagle.auto.fareresearch.web.pages.kayak.KayakUrlBuilder;

import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class KayakTestBase extends TestBase { 
	
	protected static final boolean NEED_TO_END_EXECUTION_ON_CAPTCHA_TRY_AGAIN_LATER = true;
	protected static final int CAPTCHA_TRY_AGAIN_LATER_EXECUTION_DELAY_MAX_DURATION_MINUTES = 0;
	protected static final int CAPTCHA_TRY_AGAIN_LATER_EXECUTION_DELAY_MINUTES = 15;
	protected static final int CAPTCHA_TRY_AGAIN_LATER_EXECUTION_DELAY_MAX_COUNT = CAPTCHA_TRY_AGAIN_LATER_EXECUTION_DELAY_MAX_DURATION_MINUTES / CAPTCHA_TRY_AGAIN_LATER_EXECUTION_DELAY_MINUTES;
	protected static int captchaTryAgainLaterExecutionDelayCount = 0;
	private static final String SEARCH_CRITERIA_ALL_AIRLINES_CODE = "ALL";
	private static final String EXECUTION_ID = "EXECUTION_ID";
	private static final boolean DISABLE_PER_TEST_BROWSER_EXECUTION = true;

	private static final String HADOOP_HOME_DIRECTORY_PATH = "src/main/resources/hadoop";
	private static final String HADOOP_BIN_DIRECTORY_PATH = "src/main/resources/hadoop/bin";
	private static final String MSVCR100_SOURCE_DLL_PATH = "src/main/resources/hadoop/msvcr100.dll";
	private static final String MSVCR100_DESTINATION_FOLDER = "C:\\Windows\\System32";
	private static final String MSVCR100_DLL_FILE_NAME = "msvcr100.dll";

	private final static String BUILD_REPORT_PARQUET_FOLDER = CSVToParquet.BUILD_REPORT_PARQUET_FOLDER;
	private final static String SEARCH_REPORT_PARQUET_FOLDER = CSVToParquet.SEARCH_REPORT_PARQUET_FOLDER;
	
	public static Class<? extends RouteSearchCriteriaBase> searchCriteriaType;
	public static String searchCriteriaFilePath;
	public static String searchReportFilePath;
	public static String buildReportFilePath;
	
	protected static WebDriver driver;
	protected KayakSearchResultsPage searchPage;
	
	protected RouteSearchCriteriaBase searchCriteria;
	protected Itinerary firstValidItineraray;
	protected IESearchAPIData searchAPIData;
	protected CSVFileUtil<?> csvFileUtil;
	protected  JenkinsBuildReport buildReport;

	private static int totalSearchTestsCount = 0;
	private static int totalFailedTestscount=0;
	private static int totalPassedTestscount=0;
	private static int captchaOccuredCount=0;
	private static int captchaTryAgainLaterOccuredCount = 0;
	private static int browserQuitCounter = 0;
	private static int BROWSER_QUIT_TESTS_COUNT = 5;
	private static Set<String> runTimeErrorDescrption = new HashSet<>();
	
	protected <T extends RouteSearchCriteriaBase> void  beforeSuite(Class<T> searchCriteriaType) throws Exception {
		if(BrowserUserAgentsUtil.needToUpdateUserAgents()){
			addAutomationReportWarningTest("BrowserUserAgentsUtil.java => Update Browser User Agents. They are not latest.", null);	
		}
		if( MavenProfile.IS_KAYAK_24X7_ROUTES_FR_ACTIVE) {
			FileUtils.deleteDirectory(new File(MavenProfile.SRC_MAIN_INPUT_24X7_FR_JOBS_PATH));
			Files.createDirectory(Paths.get(MavenProfile.SRC_MAIN_INPUT_24X7_FR_JOBS_PATH));
		}
		FileUtils.deleteDirectory(new File(MavenProfile.SRC_MAIN_OUTPUT_PATH));
		Files.createDirectory(Paths.get(MavenProfile.SRC_MAIN_OUTPUT_PATH));
		validteAndDoInputOutputFilesSetup(searchCriteriaType);
		csvFileUtil = new CSVFileUtil<>(searchCriteriaType);		
		if (MavenProfile.UPLOAD_OUTPUT_TO_S3) {
			csvFileUtil.appendBuildReportRow(getSearchReportHeader(JenkinsBuildReport.getBuildReportCSVHeader()));
			setMSVCR100SourceDllFile(MSVCR100_SOURCE_DLL_PATH, MSVCR100_DESTINATION_FOLDER, MSVCR100_DLL_FILE_NAME);
			setHadoopConfiguration();
			initilizeJenkinsBuildReport();	
		}
	}
	
	private static <T extends RouteSearchCriteriaBase> void validteAndDoInputOutputFilesSetup(Class<T> searchCriteriaType) {
		if( searchCriteriaType == SupportedRouteSearchCriteria.class ){
			if( MavenProfile.IS_KAYAK_24X7_ROUTES_FR_ACTIVE){
				S3Util.get24x7RoutesFRSearchCritieria();
				searchCriteriaFilePath =  MavenProfile.KAYAK_24X7_ROUTES_FR_SEARCH_CRITERIA_PATH;
				searchReportFilePath = MavenProfile.KAYAK_24X7_ROUTES_FR_SEARCH_REPORT_PATH;
			}
			else {
				searchCriteriaFilePath =  MavenProfile.KAYAK_SUPPORTED_ROUTES_FR_SEARCH_CRITERIA_PATH;
				searchReportFilePath = MavenProfile.KAYAK_SUPPORTED_ROUTES_FR_SEARCH_REPORT_PATH;
			}
		}
		else if(searchCriteriaType == NewRouteSearchCriteria.class) {
			searchCriteriaFilePath =  MavenProfile.KAYAK_NEW_ROUTES_FR_SEARCH_CRITERIA_PATH;
			searchReportFilePath = MavenProfile.KAYAK_NEW_ROUTES_FR_SEARCH_REPORT_PATH;
		}
		else {
			throw new RuntimeException("Input Search Criteria Type = '" + searchCriteriaType + "' is not supported" );
		}
		buildReportFilePath = MavenProfile.SRC_MAIN_OUTPUT_PATH + "Build_Report.csv";
		KayakTestBase.searchCriteriaType = searchCriteriaType;
	}

	private void initilizeJenkinsBuildReport() {

		buildReport = new JenkinsBuildReport();

		buildReport.setBuildStartTime(getCurrentCSTTime());
		buildReport.setUserName(System.getProperty("username"));
		buildReport.setJobName(System.getProperty("jobname"));
		buildReport.setNodeName(System.getProperty("nodename"));
		buildReport.setBuildNumber(System.getProperty("buildnumber"));
		
	}

	@DataProvider
	public Object[][] inputRoutesDataProvider() throws Exception
	{ 
		var criterias = new ArrayList<RouteSearchCriteriaBase>();

		try {
			criterias = (ArrayList<RouteSearchCriteriaBase>) csvFileUtil.getInputRouteSearchCriterias();
			totalSearchTestsCount = totalTestsSearchCount(criterias);
		}  
		catch(Exception e)
		{	
			SearchCriteriaCSVException ex = new SearchCriteriaCSVException
					("Unable to find or read Search Criteria CSV File due to " + 
							e.getClass().getSimpleName() + " " + e.getMessage(), e);
			
			String statusText = "Unable To Start Automation due to below exceptions";
			addAutomationReportFailedTest(statusText, null);
			addAutomationReportFailedTest(ex.getClass().getSimpleName(),ex);
			addAutomationReportFailedTest(e.getClass().getSimpleName(),e);
			csvFileUtil.appendRouteSearchReportRow(Arrays.asList(statusText)); 
			csvFileUtil.appendRouteSearchReportRow(Arrays.asList(ex.getClass().getSimpleName())); 
			csvFileUtil.appendRouteSearchReportRow(Arrays.asList(e.getClass().getSimpleName())); 
			csvFileUtil.appendRouteSearchReportRow(Arrays.asList(ex)); 
			if(e instanceof DateTimeParseException)
			{
				String dateStatusText = "Use date format YYYY-MM-DD like 2022-08-25";
				addAutomationReportFailedTest(dateStatusText, null);
				csvFileUtil.appendRouteSearchReportRow(Arrays.asList(dateStatusText));
			}
			if(csvFileUtil.getSearchCriteriaCSVReadingRowNo()>0)
			{
				String rowStatusText = "Exception from Search Criteria CSV Row " +
											// Add +1 to represent CSV row with header
											(csvFileUtil.getSearchCriteriaCSVReadingRowNo()+1);
				addAutomationReportFailedTest(rowStatusText, null);
				csvFileUtil.appendRouteSearchReportRow(Arrays.asList(rowStatusText));
			}
			
			throw ex;
		}
		
		int criteriasCount = criterias.size();
		Object[][] testData =  new Object[criteriasCount][1];
		for(int i=0;i<criteriasCount;i++) {
			testData[i][0] =  (Object) criterias.get(i);
		}
		return testData;
	}

	
	  @BeforeMethod
	  public void testSetup() throws Exception {
		  
		  if( ! MavenProfile.IS_KAYAK_24X7_ROUTES_FR_ACTIVE) {
			  doPerTestRamdomWaitActions();
	 		 }
		  String browserUserAgent =  BrowserUserAgentsUtil.getRandomeBrowserUserAgent();
	      ChromeOptions options = new ChromeOptions();
	      options.addArguments("start-maximized");
	      options.addArguments("--user-agent=" + browserUserAgent);
	      options.addArguments("--remote-allow-origins=*");
	      options.addArguments("--blink-settings=imagesEnabled=false");
          options.addArguments("--disable-cache");
          options.addArguments("--disable-network-throttling"); 
	      options.addArguments("--log-level=3"); 
	      options.addArguments("--disable-gpu"); 
	      options.addArguments("--disable-popup-blocking");
	      options.addArguments("--disk-cache-size=0");
	      
		  try {
			  doDriverLaunchActions(options);
		      // WebDriverManager.firefoxdriver().setup();
		      // driver = new FirefoxDriver();
		  }
		  catch (Exception e) {
			  testReport.log(Status.INFO, "Unable to start new browser session. Running killChromeDriverExe() & trying again");
			  WebDriverUtil.killChromeDriverExe();
		      WebDriverManager.chromedriver().clearDriverCache().setup();
		      driver = new ChromeDriver(options);
		  }
	      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	      // driver.manage().window().maximize(); // Comment as we are using chrome option start-maximized
	  }
	  
	  @AfterMethod
	  public void testTeardown(ITestResult result) throws Exception {
		 
		  if(extentReport!=null)
		    		extentReport.flush();
		  browserQuitCounter++;
		 try {
			 doDriverQuitActions();
		 }
		 catch (Exception e) {
			  testReport.log(Status.INFO, "Unable to stop the browser session. Running killChromeDriverExe()");
			  WebDriverUtil.killChromeDriverExe();
		 }
	  }
	  
    @AfterMethod
    public synchronized void extentReportTestRename(Method method) {
        erTest.getModel().setName(method.getName() +
        								" ID = " + 
        								searchCriteria.getId());
    }
    	
    protected void loadNewKayakSearchPage(String newSearchPageURL)
	{
		try
		{
			searchPage = new KayakSearchResultsPage(driver, searchCriteria, testReport, newSearchPageURL);
		}
		catch(Exception e)
		{
			if(e instanceof KayakCaptchaTryAgainLaterException && 
					  captchaTryAgainLaterExecutionDelayCount < CAPTCHA_TRY_AGAIN_LATER_EXECUTION_DELAY_MAX_COUNT)
				{
					 captchaTryAgainLaterExecutionDelayCount++;
					 testReport.log(Status.INFO, "CAPTCHA TRY AGAIN LATER DIALOG is displayed." + 
							 							" Execution Delay Count = " + captchaTryAgainLaterExecutionDelayCount + 
							 								". Delaying the execution for " + CAPTCHA_TRY_AGAIN_LATER_EXECUTION_DELAY_MINUTES + " minutes.");				
					 BrowserWaitUtil.waitForMilliSeconds(CAPTCHA_TRY_AGAIN_LATER_EXECUTION_DELAY_MINUTES * 60 * 1000);
				 }
			
			testReport.log(Status.INFO, "------- Attempt-2 to load Kayak Search Page after failed attempt-1 due to " +  
												e.toString() + " -------");
			driver.navigate().refresh();
			driver.get("about:blank");
			searchPage = new KayakSearchResultsPage(driver, searchCriteria, testReport, newSearchPageURL);
		}
	}
	
	protected void getFirstValidItinerary() throws Exception
	{
		firstValidItineraray = null;
		try {
			firstValidItineraray = searchPage.getFirstValidItinerary();
		}
		catch (Exception e) {
			testReport.log(Status.INFO, "------- Attempt-2 to get the first valid itinerary after failed attempt-1 due to " +  
										e.toString() + " -------");
			searchPage.closeSignInOrCreateAccountDialog();
			driver.navigate().refresh();
			firstValidItineraray = searchPage.getFirstValidItinerary();
		}
	}
	
	protected void generateSearchReportCSVOutput(RouteSearchCriteriaBase SearchrCiteriaFromCSV) throws Exception {
		
		searchCriteria = SearchrCiteriaFromCSV;
		testReport.log(Status.INFO, searchCriteria.toString());
		
		var searchAirlines = new ArrayList<>(Arrays.asList(searchCriteria.getSearchAirlines().split(",")));
		searchAirlines.replaceAll(e -> e = StringUtil.getNoSpacesUpperCaseString(e));
		String newSearchPageURL=null;
		String firstSearchAirline = searchAirlines.get(0);
		
		if(firstSearchAirline.equalsIgnoreCase(SEARCH_CRITERIA_ALL_AIRLINES_CODE))
			newSearchPageURL = KayakUrlBuilder.getKayakFlightsSearchUrl(searchCriteria);
		else
			newSearchPageURL = KayakUrlBuilder.getKayakFlightsSingleAirlineSearchUrl(firstSearchAirline, searchCriteria);
		
		createSearchReportForAirline(firstSearchAirline, newSearchPageURL, false);
		searchAirlines.remove(0);
		for (String airlineCode : searchAirlines) {
			createSearchReportForAirline(airlineCode, null, true);
		}
	}

	protected void createSearchReportForAirline(String airlineCode, String newSearchPageURL, boolean setAirlineFilter) throws Exception {
		searchCriteria.setCurrentSearchAirline(airlineCode);
		Exception runtimeError = null;
		firstValidItineraray = null;
		searchAPIData = null;
		CaptchaStatusType captchaStatusAfterSearchPageLoad = null;
		
		testReport.log(Status.INFO, "------- " + airlineCode + " AIRLINE SEARCH FILTER -------");
	
		try{
			
			if(newSearchPageURL!=null) {
				loadNewKayakSearchPage(newSearchPageURL);
				captchaStatusAfterSearchPageLoad = searchCriteria.getCaptchaStatus();
			}
			
			if(setAirlineFilter){
				if(searchPage == null)
					searchPage = new KayakSearchResultsPage(driver, searchCriteria, testReport, null); 

				searchPage.expandAirlinesSection(); 
				
				if(searchPage.applyAirlineFilter(airlineCode)){
					searchPage = new KayakSearchResultsPage(driver, searchCriteria, testReport, null);
					if(captchaStatusAfterSearchPageLoad != null)
						searchCriteria.setCaptchaStatus(captchaStatusAfterSearchPageLoad);
						
					getFirstValidItinerary();
				}
				else{	
					testReport.log(Status.INFO, airlineCode + " - option is not present in Airlines Filter");
					searchCriteria.setKayakSearchStatus(KayakSearchStatusType.RESULTS_NOT_FOUND);
				}
			}
			else{
				testReport.log(Status.INFO, "------- " + airlineCode + " AIRLINE DIRECT SEARCH -------");
				getFirstValidItinerary();
			}
			
			if( searchCriteria instanceof NewRouteSearchCriteria && firstValidItineraray!=null ){
				doNewRouteFRActions();
			}
		}	
		catch (Exception e) {
			runtimeError = e;
			EmailSenderUtil.doNotificationActions(e);
			if(runtimeError instanceof KayakCaptchaTryAgainLaterException && 
					NEED_TO_END_EXECUTION_ON_CAPTCHA_TRY_AGAIN_LATER) {
				testReport.log(Status.INFO, "AUTOMATION STOPPED - CAPTCHA TRY AGAIN LATER DIALOG is displayed. ");
				addAutomationReportWarningTest("AUTOMATION STOPPED - CAPTCHA TRY AGAIN LATER DIALOG is displayed.", null);
				saveSearchReportForAirline(airlineCode,runtimeError);
				doPostExecutionActions();
				System.exit(-1);
			}
		}
		finally
		{
			saveSearchReportForAirline(airlineCode,runtimeError);
		}		
	}

	private void doNewRouteFRActions() {
		searchAPIData = null;
		try {
			var ieSearchAPI = new IESearchAPI( (NewRouteSearchCriteria) searchCriteria, firstValidItineraray, testReport);
			searchAPIData = ieSearchAPI.getSearchAPIData();
		} 
		catch (Exception e) {
			var ex = new NewRoutesFRException("Unable to do New Route FR actions due to " +	e.getClass().getSimpleName() + " " + e.getMessage(), e);
			throw ex;
		}
	}
	
	private void setKayakSearchStatusOnRuntimeError(Exception runtimeError) {
		if(runtimeError instanceof KayakCaptchaTryAgainLaterException){
				 var searchStatus =   (NEED_TO_END_EXECUTION_ON_CAPTCHA_TRY_AGAIN_LATER) ?
						  							KayakSearchStatusType.AUTOMATION_STOPPED :
						  								KayakSearchStatusType.CAPTCHA_TRY_AGAIN_LATER;
				 searchCriteria.setKayakSearchStatus(searchStatus);
		}
		else if(runtimeError instanceof KayakCaptchaException){
				  searchCriteria.setKayakSearchStatus(KayakSearchStatusType.CAPTCHA_DISPLAYED);
		}
		else {
			  searchCriteria.setKayakSearchStatus(KayakSearchStatusType.RUNTIME_ERROR);
		}
	}

	protected void saveSearchReportForAirline(String airlineName, Exception runtimeError)  {
		  
		searchCriteria.setCurrentSearchAirline(airlineName);  
		String runtimeErrorReason = "NO_ERROR";
		  
		  if(runtimeError != null && !(runtimeError instanceof NewRoutesFRException)){
			  firstValidItineraray = null; // Ignore itinerary info on test failure
			  setKayakSearchStatusOnRuntimeError(runtimeError);
		  }
		  
		  if(runtimeError != null) {
			  totalFailedTestscount ++;
			  runtimeErrorReason = runtimeError.getClass().getName();
			  runtimeErrorReason = runtimeErrorReason.replace("com.indianeagle.auto.fareresearch.exceptions.", "");
			  testReport.log(Status.FAIL,runtimeError);
			  runTimeErrorDescrption.add(runtimeErrorReason);
		  }
		  	  
		 searchCriteria.setRuntimeErrorReason(runtimeErrorReason);
		 logGotKayakItineraryStatus();
		
		 if (runtimeErrorReason != null && runtimeErrorReason.equalsIgnoreCase("NO_ERROR"))
			 totalPassedTestscount++;

		 getCaptchaOccuredStatus(searchCriteria);
	 		
		 RouteSearchReportBase searchReport = null;
		 try { 
			 searchReport = initializeSearchReport();
			 testReport.log(Status.INFO, "GENERATED SEARCH REPORT: " + 	((searchReport==null)?"NO":"YES"));
		  
			 csvFileUtil.appendRouteSearchReportRow(getSearchReportRow(searchReport));
			 testReport.log(Status.INFO, "Saved the search report to CSV file.");
		 } 
		catch (Exception e) {
			EmailSenderUtil.doNotificationActions(e);
			saveSearchReportToAutomationReportOnError(searchReport,e);
		}
 
   		extentReport.flush();
   		testReport.tempLog(Status.INFO, "END OF saveSearchReportForAirline()");
	}

	private RouteSearchReportBase initializeSearchReport() {
		 RouteSearchReportBase searchReport = null;
		 if(searchCriteria instanceof SupportedRouteSearchCriteria)
			 searchReport = new SupportedRouteSearchReport( (SupportedRouteSearchCriteria) searchCriteria, firstValidItineraray);
		 else if(searchCriteria instanceof NewRouteSearchCriteria) {
			setFinalSearchAPISearchStatus();
			logGotSearchAPIItineraryStatus();
			searchReport = new NewRouteSearchReport( (NewRouteSearchCriteria) searchCriteria, firstValidItineraray, searchAPIData);
		}
		else
			 throw new RuntimeException("Input Search Criteria Type = '" + searchCriteria.getClass() + "' is not supported" );
		 return searchReport;
	}

	private void logGotSearchAPIItineraryStatus() {
		var searchAPISearchStatus = ((NewRouteSearchCriteria) searchCriteria).getSearchAPISearchStatus();
	 	testReport.log(Status.INFO, "GOT SEARCH API ITINERARY DETAILS: " + 
				  ((searchAPIData==null || searchAPISearchStatus !=SearchAPISearchStatusType.SINGLE_ITINERARY_MATCHED) ? 
						  ("NO" + " (Search API Search Status = " + searchAPISearchStatus + ")") :
						   "YES"));
	}

	private void setFinalSearchAPISearchStatus() {
		SearchAPISearchStatusType finalSearchAPISearchStatus = null;
		if(firstValidItineraray==null)
			finalSearchAPISearchStatus = SearchAPISearchStatusType.KAYAK_ITINERARY_IS_NULL ;
		else if(searchAPIData==null)
			finalSearchAPISearchStatus = SearchAPISearchStatusType.RUNTIME_ERROR ;
		if(finalSearchAPISearchStatus!=null)
			((NewRouteSearchCriteria) searchCriteria).setSearchAPISearchStatus(finalSearchAPISearchStatus);
	}

	private void logGotKayakItineraryStatus() {
		 testReport.log(Status.INFO, "GOT KAYAK ITINERARY DETAILS: " + 
				  ((firstValidItineraray==null) ? 
						  ("NO" + " (Kayak Search Status = " + searchCriteria.getKayakSearchStatus() + ")") :
						   "YES"));
	}

	private void saveSearchReportToAutomationReportOnError(RouteSearchReportBase searchReport, Exception e) {
		String searchReportOutput = null;
		if(searchReport!=null){
			searchReportOutput = String.join(",", 
												getSearchReportRow(searchReport).stream().
													map(t -> (t==null)? "":t.toString()).
													collect(Collectors.toList()));
		} 
		
		SearchReportCSVException ex = new SearchReportCSVException
			("Unable to create or save the Search Report CSV Entry "+
				((searchReportOutput!=null)? ("\n\n" + searchReportOutput + "\n\n") : "") + 
					"due to " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
		
		testReport.log(Status.FAIL,ex);	
	}
		
	@AfterSuite
	public void kayakAfterSuite() throws Exception
	{
		doPostExecutionActions();
	}

	private void doPostExecutionActions() {
		if (MavenProfile.UPLOAD_OUTPUT_TO_S3) {
			doOutputToS3ProcessActions();
		}
	}

	private void doOutputToS3ProcessActions() {
		var processName = "OUTPUT_TO_S3_PROCESS";
		try {
			setBuildReportData();

			try {
				var buildReportCSVRow= new ArrayList<>();
				buildReportCSVRow.add(getExecutionId());
				buildReportCSVRow.addAll(buildReport.getBuildReportCSVRow()); 
				csvFileUtil.appendBuildReportRow(buildReportCSVRow);
				testReport.log(Status.INFO, "GENERATED BUILD REPORT: " + 
			 			((buildReportCSVRow==null)?"NO":"YES"));
			}
			catch (Exception e) {
				throw new BuildReportCSVException("Unable to Save Build Report due to "+e.getClass()+e.getMessage(), e);
			}

	   	    CSVToParquet.convertSearchReportToParquet(getExecutionId()); 
	   	    CSVToParquet.convertBuildReportToParquet(getExecutionId()); 

	   	    S3Util.uploadSearchReportParquetFileToS3(SEARCH_REPORT_PARQUET_FOLDER 
		   			 +CSVToParquet.changeParquetFileName(SEARCH_REPORT_PARQUET_FOLDER, getExecutionId() + "_SEARCH_REPORT"));

	   	    S3Util.uploadBuildReportParquetFileToS3(BUILD_REPORT_PARQUET_FOLDER
		   			 + CSVToParquet.changeParquetFileName(BUILD_REPORT_PARQUET_FOLDER, getExecutionId() + "_BUILD_REPORT"));
	   	    addAutomationReportInfoTest( processName + " SUCCESSFUL", null);
		}
		catch (Exception e) {
			OutputToS3ProcessException ex = new OutputToS3ProcessException
					("Unable to do Output to S3 storage actions due to "+ e.getClass().getSimpleName() + " " + e.getMessage(), e);
			addAutomationReportFailedTest( processName + " FAILED", ex);
			
			EmailSenderUtil.doNotificationActions(e);
			throw ex;
		}
	}

	private void setBuildReportData() {
		buildReport.setTotalSearchTestsCount(totalSearchTestsCount);
		buildReport.setTotalPassedTestCount(totalPassedTestscount);
		buildReport.setTotalFailedTestCount(totalFailedTestscount);
		buildReport.setTotalNotRunTestCount((buildReport.getTotalSearchTestsCount()
										 - (buildReport.getTotalPassedTestCount() + buildReport.getTotalFailedTestCount())));
		buildReport.setBuildEndTime(getCurrentCSTTime());
		buildReport.setTotalRunTimeMins(totalBuildExecutionTime());
		buildReport.setPerTestRunTimeSecs(perTestExecutionTime());
		buildReport.setCaptchaOccured(isCaptchaDisplayed(captchaOccuredCount));
		buildReport.setCaptchaTryAgianLaterOccured(isCaptchaDisplayed(captchaTryAgainLaterOccuredCount));
		buildReport.setRunTimeErrorReasons(getTimeErrorDescription());
	}

	private static int totalTestsSearchCount(List<RouteSearchCriteriaBase> criterias){
		var airlinesData = criterias.stream()
					.map(RouteSearchCriteriaBase::getSearchAirlines) 
					.collect(Collectors.toList()); 		      
		var airlinesDataCount =airlinesData.stream()
			 							.map( e -> ((Integer)e.split(",").length))
			 							.toList();
		return airlinesDataCount.stream().reduce(0, Integer::sum);

	}

	private  String getCurrentZonedTime(String timeZone) {
		return ZonedDateTime.now( ZoneId.of(timeZone))
							.format(DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss"));
	}

	private  String getCurrentCSTTime() {
		return getCurrentZonedTime("America/Chicago");
	}

	private  Duration calculateBuildTimeDurations() {
		return Duration.between(LocalTime.parse(buildReport.getBuildStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
	        					LocalTime.parse(buildReport.getBuildEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

    public double totalBuildExecutionTime() {
        return ((double)calculateBuildTimeDurations().toSeconds()/60); 
    }

    public double perTestExecutionTime() {
    	var totalTestsCount = totalPassedTestscount + totalFailedTestscount ;
        if (totalTestsCount == 0) 
            return 0; 
        else
            return ((double) calculateBuildTimeDurations().toSeconds() /totalTestsCount);
    }

    public  String getExecutionId() {
    	return buildReport.getJobName() +"_"+ buildReport.getBuildNumber();
    }

    protected static List<Object> getSearchReportHeader(List<Object> reportCSVHeaders) {
		if (MavenProfile.UPLOAD_OUTPUT_TO_S3) {
			var updatedReportCSVHeader = new ArrayList<>();
			updatedReportCSVHeader.add(EXECUTION_ID);
			updatedReportCSVHeader.addAll(reportCSVHeaders);
			return updatedReportCSVHeader;
		} else {
			return reportCSVHeaders;
		}
	}

    protected List<Object> getSearchReportRow(RouteSearchReportBase searchReport) {
		if (MavenProfile.UPLOAD_OUTPUT_TO_S3) {
			var searchReportCSVRowList = new ArrayList<>();
			searchReportCSVRowList.add(getExecutionId());
			searchReportCSVRowList.addAll(searchReport.getSearchReportCSVRow());
			return searchReportCSVRowList;
		} else {
			return searchReport.getSearchReportCSVRow();
		}
	}

	private static void setHadoopConfiguration() {
		try {
			System.setProperty("hadoop.home.dir", new File(HADOOP_HOME_DIRECTORY_PATH).getAbsolutePath());

			var hadoopHomebin = new File(HADOOP_BIN_DIRECTORY_PATH).getAbsolutePath();

			System.setProperty("java.library.path", hadoopHomebin);
			System.load(hadoopHomebin + "/hadoop.dll");
		} catch (Exception e) {
			throw new HadoopConfigException("Failed to set the Hadoop Configuration", e);
		}
	}

	private static void setMSVCR100SourceDllFile(String msvcr100SourceDllPath, String msvcr100DestinationFolder,String msvcr100DllFileName) {
		if (!isFileExists(msvcr100DestinationFolder, msvcr100DllFileName)) {
			try {
				Files.copy(Path.of(msvcr100SourceDllPath), Path.of(msvcr100DestinationFolder, msvcr100DllFileName),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new HadoopConfigException("Failed to set the msvcr100SourceDll file Configuration", e);
			}
		}
	}

	private static boolean isFileExists(String folderPath, String fileName) {
		return new File(folderPath, fileName).exists();
	}

	private static String getTimeErrorDescription() {
		return runTimeErrorDescrption.toString().replaceAll("[\\[\\]]", "");
	}

	public boolean isCaptchaDisplayed(int captchaCount) {
		if(captchaCount >0)
			return true;
		else 
			return false;
	}

	public static  void getCaptchaOccuredStatus(RouteSearchCriteriaBase searchCriteria) {
	var captchaStatus =	searchCriteria.getCaptchaStatus();
		if(captchaStatus!=null &&( captchaStatus.equals(CaptchaStatusType.CAPTCHA_SOLVED )
								|| captchaStatus.equals(CaptchaStatusType.CAPTCHA_NOT_SOLVED )
								|| captchaStatus.equals(CaptchaStatusType.CAPTCHA_RUNTIME_ERROR))) {
			captchaOccuredCount ++;
		}
		else if (captchaStatus!=null && captchaStatus.equals(CaptchaStatusType.CAPTCHA_TRY_AGAIN_LATER)) {
			captchaTryAgainLaterOccuredCount ++;
		}
	}
	private static void doPerTestRamdomWaitActions() {
		 if(DISABLE_PER_TEST_BROWSER_EXECUTION && (browserQuitCounter % BROWSER_QUIT_TESTS_COUNT == 0))
			  	BrowserWaitUtil.randomPerTestWait(); 
	  	 else if(!DISABLE_PER_TEST_BROWSER_EXECUTION)
	  			BrowserWaitUtil.randomPerTestWait(); 
	}
	private static void doDriverQuitActions() {
		if (DISABLE_PER_TEST_BROWSER_EXECUTION && (browserQuitCounter % BROWSER_QUIT_TESTS_COUNT == 0)) 
			   driver.quit();
		else if(!DISABLE_PER_TEST_BROWSER_EXECUTION)
				driver.quit();
			 
	}
	private static void doDriverLaunchActions( ChromeOptions options) {
		if (DISABLE_PER_TEST_BROWSER_EXECUTION && (browserQuitCounter % BROWSER_QUIT_TESTS_COUNT == 0)) 
			driver = new ChromeDriver(options);
		else if(!DISABLE_PER_TEST_BROWSER_EXECUTION)
			driver = new ChromeDriver(options);
		
	}
	
}
