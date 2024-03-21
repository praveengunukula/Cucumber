package com.ieagle.auto.site.enums;

public enum CabinType {
	
    ECONOMY("economy"), 
    PREMIUM_ECONOMY("premium"), 
    BUSINESS("business"), 
    FIRST("first");

    private String value;

    CabinType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
