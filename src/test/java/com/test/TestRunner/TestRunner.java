package com.test.TestRunner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"src\\test\\resources\\search.feature"}, 
    tags = "@test",
    glue = {"com.test.stepDefinitions"}, 
    plugin = {"pretty", "html:target/cucumber-reports/cucumber-html-reports.html",
    		"rerun:target/failedScripts.txt","json:target//cucumber-reports/cucumber-html-reports.json"} 
)

public class TestRunner {
    // No implementation needed here
}
