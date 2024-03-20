package com.test.utils;

	import java.io.FileInputStream;
	import java.io.IOException;
	import java.util.Properties;

	public class ConfigReader {
	    private static final String CONFIG_FILE_PATH = "src\\test\\resources\\test-config.properties";
	    private static Properties properties;

	    static {
	        try {
	            FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE_PATH);
	            properties = new Properties();
	            properties.load(fileInputStream);
	            fileInputStream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new RuntimeException("Error loading config properties file");
	        }
	    }

	    public static String getChromeArguments() {
	        return properties.getProperty("chrome.arguments");
	    }

	    public static String getBrowser() {
	        return properties.getProperty("browser");
	    }

	    public static String getExcelSheetName() {
	        return properties.getProperty("excel.sheetName");
	    }
	    
	    public static String getUsername() {
	        return properties.getProperty("username");
	    }

	    public static String getPassword() {
	        return properties.getProperty("password");
	    }

}
