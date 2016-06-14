package com.aman.chatgram.model;

/**
 * Created by Aman on 12-06-2016.
 */
public class Country {
    private String countryName;
    private String countryCallingCode;

    public Country(String countryName,String countryCallingCode){
        this.countryName=countryName;
        this.countryCallingCode=countryCallingCode;
    }
    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
