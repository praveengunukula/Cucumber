package com.test.utils;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports createInstance() {
        String reportPath = "target/extent-report.html";
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(reportPath);
        htmlReporter.config().setDocumentTitle("Cucumber Extent Report");
        htmlReporter.config().setReportName("BDD Test Automation Report");
        htmlReporter.config().setEncoding("utf-8");

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        return extent;
    }
}
