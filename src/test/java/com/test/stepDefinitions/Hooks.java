package com.test.stepDefinitions;


import org.openqa.selenium.WebDriver;

import com.test.utils.CucumberReportGenerator;
import com.test.utils.WebDriverManager;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;

public class Hooks {
	
	  private WebDriver driver;

	    @Before
	    public void setup() {
	        driver = WebDriverManager.getDriver();
	    }

	    @After
	    public void teardown() {
	        WebDriverManager.quitDriver();
	    }
	
	@AfterAll
    public static void generateReport() {
		CucumberReportGenerator.generateReport();
    }

}
