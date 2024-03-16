package com.test.TestRunner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "@target/failedScripts.txt", 
    glue = {"com.test.stepDefinitions"}, 
    plugin = {"pretty", 
              "html:target/cucumber-rerun-reports/cucumber-html-rerun-reports.html",
              "json:target/cucumber-rerun-reports/cucumber-html-rerun-reports.json"}
)
public class FailedTestRunner {
    // Failed test runner configuration
}
