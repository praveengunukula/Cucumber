package com.test.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.NotImplementedException;

public class BrowserWaitUtil {
	
	private static final int AUTOMATED_TEST_FIXED_WAIT_MILLI_SECONDS = 10000; 
	private static final int AUTOMATED_TEST_RANDOM_WAIT_MILLI_SECONDS = 5000; 
	
	private static final Random randomInt = new Random();
	
	public static void randomeUserActionWait()
	{
		try {
			Thread.sleep( 1000 + 
							randomInt.nextInt(1500) );
		}
		catch (InterruptedException e) {
			throw new NotImplementedException("Need to handle catch during randomeUserActionWait()", e);
		}
	}
	
	public static void randomPerTestWait()
	{
		try {
			Thread.sleep( AUTOMATED_TEST_FIXED_WAIT_MILLI_SECONDS + 
								randomInt.nextInt(AUTOMATED_TEST_RANDOM_WAIT_MILLI_SECONDS) );
		}
		catch (InterruptedException e) {
			throw new NotImplementedException("Need to handle catch during randomAutomatedTestWait()", e);
		}
	}
	
	public static void waitForMilliSeconds(long totalMilliSeconds)
	{
		try {
			Thread.sleep(totalMilliSeconds);
		}
		catch (InterruptedException e) {
			throw new NotImplementedException("Need to handle catch during waitForMilliSeconds()", e);
		}
	}
}
