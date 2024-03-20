package com.test.exceptions;

public class BeforeSuiteException extends RuntimeException{
	
	public BeforeSuiteException(String message, Throwable cause)
	{
		super(message,cause);
	}

	public BeforeSuiteException(String message) {
		super(message);
	}

}
