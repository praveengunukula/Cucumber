package com.test.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;

import com.test.page.HomePage;
import com.test.utils.ExcelReader;
import com.test.utils.WebDriverManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;


public class SearchSteps {
	
	private WebDriver driver;
    private HomePage homePage;

    @Given("User is on the home page")
    public void user_is_on_the_home_page() {
        driver = WebDriverManager.getDriver();
        homePage = new HomePage(driver);
        homePage.navigateTo();
    }

    @When("^User enters \"([^\"]*)\" in the search bar$")
    public void user_enters_in_the_search_bar(String searchTerm) throws IOException {
        homePage.enterSearchTerm(searchTerm);
    }

    @When("User clicks the search button")
    public void user_clicks_the_search_button() {
        homePage.clickSearchButton();
    }

    @Then("^Search results for \"([^\"]*)\" are displayed$")
    public void search_results_for_are_displayed(String searchTerm) {
        
        String pageTitle = driver.getTitle();
        
        Assert.assertTrue("Search results page title does not contain the search term", pageTitle.contains(searchTerm));
    }
   

}
