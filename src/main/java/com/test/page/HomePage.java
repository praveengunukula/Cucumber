package com.test.page;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.test.utils.CommonUtils;
import com.test.utils.ExcelReader;

public class HomePage {
    private WebDriver driver;

    @FindBy(id = "email")
    private WebElement username;
    @FindBy(id = "passContainer")
    private WebElement password;

    @FindBy(name = "login")
    private WebElement loginButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void navigateTo() {
        driver.get("https://www.facebook.com/");
    }

    public void enterSearchTerm(String sheetname) throws IOException {
    	List<Map<String, String>> testData = ExcelReader.readExcel("TestData/TestData.xlsx", sheetname);

    	Map<String, String> row = ExcelReader.findRowByValue(testData, "user1","username" );
    	
    	 String userid = row.get("username");
 	    String passwordid = row.get("password");
 	   CommonUtils.sendKeysToElement(username, userid);
 	   CommonUtils.sendKeysToElement(password, passwordid);
    	
    	
    }

    public void clickSearchButton() {
    	CommonUtils.clickElement(loginButton);
    	
    }


}
