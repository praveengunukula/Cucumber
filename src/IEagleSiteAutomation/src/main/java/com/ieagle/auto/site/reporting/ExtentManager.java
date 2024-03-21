package com.ieagle.auto.site.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    
    private static ExtentReports extentReport;
    
    public static ExtentReports getInstance() {
    	if (extentReport == null)
    		createInstance("test-output/extent.html");
    	
        return extentReport;
    }
    
    public static ExtentReports createInstance(String fileName) {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fileName);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle(fileName);
        sparkReporter.config().setEncoding("utf-8");
        sparkReporter.config().setReportName(fileName);
        
        extentReport = new ExtentReports();
        extentReport.attachReporter(sparkReporter);
        
        return extentReport;
    }
}