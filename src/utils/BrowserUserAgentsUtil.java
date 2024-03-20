package com.test.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BrowserUserAgentsUtil {
	
	private static final List<String> BROWSER_USER_AGENTS  = initializeBrowserUserAgents();
	private static final int BROWSER_USER_AGENTS_SIZE  = BROWSER_USER_AGENTS.size();
	private static final Random randomInt = new Random();
	public static final LocalDate LAST_BROWSER_USER_AGENT_CHANGE_DATE = LocalDate.parse("2024-01-01"); //YYYY-MM-DD format
	
	public static String getRandomeBrowserUserAgent()
	{
		return BROWSER_USER_AGENTS.get(randomInt.nextInt(BROWSER_USER_AGENTS_SIZE));
	}
	
	public static boolean needToUpdateUserAgents()
	{
		// Good to update for every 3 months
		LocalDate nextChangeDate = LAST_BROWSER_USER_AGENT_CHANGE_DATE.plusMonths(3);
		if(nextChangeDate.isAfter(LocalDate.now()))
			return false;
		return true;
	}

	private static List<String> initializeBrowserUserAgents() {
		List<String> userAgents = Arrays.asList(
				// Latest top 10+ Desktop Browser Useragents from https://www.useragents.me/
				// Don't use combinations like (Other , Windows), (Other , Mac OS X)
				"Mozilla/5.0 (X11; CrOS x86_64 14541.0.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.3",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.2365.66",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Agency/93.8.2357.5",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 OPR/107.0.0.",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 OPR/108.0.0.0",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Safari/605.1.1",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.3",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Viewer/99.9.8853.8");
							
		return userAgents;
	}
}