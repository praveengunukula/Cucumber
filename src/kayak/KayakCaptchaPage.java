package com.indianeagle.auto.fareresearch.web.pages.kayak;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.indianeagle.auto.fareresearch.exceptions.KayakCaptchaTryAgainLaterException;
import com.indianeagle.auto.fareresearch.reporting.TestReport;
import com.indianeagle.auto.fareresearch.util.BrowserWaitUtil;
import com.indianeagle.auto.fareresearch.util.SymblAI;
import com.indianeagle.auto.fareresearch.util.WebDriverUtil;
import com.indianeagle.auto.fareresearch.web.pages.base.PageClassBase;

public class KayakCaptchaPage extends PageClassBase{
	
	public static final String URL_START_SNIPPET = "https://www.kayak.com/security/check";
	private static final String KAYAK_CAPTCHA_AUDIO_LOCATION = "src/main/resources/kayak_captcha_audio.mp3";
	private static final String KAYAK_CAPTCHA_CHALLENGE_CSS_LOCATOR = "iframe[title*='recaptcha challenge']";
	private static final String KAYAK_CAPTCHA_TRY_AGAIN_LATER_CSS_LOCATOR = "div.rc-doscaptcha-header-text";
	
	@FindBy(css = "iframe[title=reCAPTCHA]")
	private WebElement iframeKayakCaptcha;
	
	@FindBy(id = "recaptcha-anchor")
	private WebElement cbCaptchaBox;
	
	@FindBy(css = KAYAK_CAPTCHA_CHALLENGE_CSS_LOCATOR)
	private WebElement iframeCaptchaChallenge;
	
	@FindBy(id = "recaptcha-audio-button")
	private WebElement btnAudioCaptcha;
	
	@FindBy(css = "a[title*='download audio as MP3']")
	private WebElement lnkDownloadAudioCaptcha;
	
	@FindBy(id = "audio-response")
	private WebElement txtAudioResponse;
	
	@FindBy(id = "recaptcha-verify-button")
	private WebElement btnVerifyCaptcha;
	
	@FindBy(css = "div.WZTU-wrap button div[class*=content]")
	private WebElement btnContinue;

	public KayakCaptchaPage(WebDriver driver, TestReport testReport) {
		super(driver, testReport);
	}
	
	public void clickCaptchaCheckBox()
	{
		driver.switchTo().frame(iframeKayakCaptcha);
		BrowserWaitUtil.randomeUserActionWait();
		WebDriverUtil.moveToAndClickElement(driver, cbCaptchaBox,0);
		driver.switchTo().parentFrame();
	}
	
	public boolean isCaptchaChallengeDisplayed()
	{
		return WebDriverUtil.
				isElementPresent(driver, By.cssSelector(KAYAK_CAPTCHA_CHALLENGE_CSS_LOCATOR));
	}
	
	public boolean isCaptchaTryAgainLaterDisplayed()
	{
		return WebDriverUtil.
				isElementPresent(driver, By.cssSelector(KAYAK_CAPTCHA_TRY_AGAIN_LATER_CSS_LOCATOR));
	}
	
	public void solveAudioCaptcah() throws MalformedURLException, IOException
	{
		driver.switchTo().parentFrame();
		driver.switchTo().frame(iframeCaptchaChallenge);
		WebDriverUtil.moveToAndClickElement(driver, btnAudioCaptcha,0);
		String audioDownloadUrl = null;
		
		try {
			audioDownloadUrl = lnkDownloadAudioCaptcha.getAttribute("href");
		}
		catch(NoSuchElementException e)
		{
			if(isCaptchaTryAgainLaterDisplayed())
				throw new KayakCaptchaTryAgainLaterException("CAPTCHA TRY AGAIN LATER DISPLAYED and can't load the Kayak Search Page.");
			throw e;
		}
		
		File mp3AudioFile =  new File(KAYAK_CAPTCHA_AUDIO_LOCATION);
		FileUtils.copyURLToFile( new URL(audioDownloadUrl), mp3AudioFile, 30000,30000);
		String captchaAudioText = SymblAI.getMP3AudioText(mp3AudioFile);
		testReport.log(Status.INFO, "Solving Captcha using audio text: " + captchaAudioText);
		BrowserWaitUtil.randomeUserActionWait();
		txtAudioResponse.sendKeys(captchaAudioText);
		WebDriverUtil.moveToAndClickElement(driver, btnVerifyCaptcha,0);
		BrowserWaitUtil.waitForMilliSeconds(3000);
		
		driver.switchTo().defaultContent();
		WebDriverUtil.moveToAndClickElement(driver, btnContinue,0);
		
		WebDriverWait kayakSiteWait = new WebDriverWait(driver,Duration.ofSeconds(30));
		kayakSiteWait.until(ExpectedConditions.urlContains(KayakUrlBuilder.KAYAK_FLIGHTS_BASE_URL));
		BrowserWaitUtil.waitForMilliSeconds(1000);
	}
}