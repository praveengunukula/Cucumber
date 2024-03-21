package com.ieagle.auto.site.enums;

public enum TripType {
	
    ONE_WAY("One Way"), 
    ROUND_TRIP("Round Trip"), 
    MULTI_CITY("Multi City");

    private String value;

    TripType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
