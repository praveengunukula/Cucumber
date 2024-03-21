package com.ieagle.auto.site.exceptions;

public class WebPageException extends RuntimeException{
	
	public WebPageException(String message, Throwable cause)
	{
		super(message,cause);
	}

	public WebPageException(String message) {
		super(message);
	}

}
