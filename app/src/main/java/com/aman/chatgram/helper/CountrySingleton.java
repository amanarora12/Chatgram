package com.aman.chatgram.helper;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.aman.chatgram.R;
import com.aman.chatgram.model.Country;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Aman on 12-06-2016.
 */
public class CountrySingleton {
    private ArrayList<Country> countries;
    private static CountrySingleton mInstance;
    private boolean notEmpty;

    private CountrySingleton(){

    }
    public static CountrySingleton getInstance(){
        if(mInstance==null){
            mInstance=new CountrySingleton();
        }
        return mInstance;
    }

    public void getCountryList(Context context){
        if(notEmpty){
            return;
        }
        notEmpty=true;
        countries=new ArrayList<>();
        String[] countryList=context.getResources().getStringArray(R.array.countries);
        for(String country:countryList){
            String[] info=country.split(",");
            countries.add(new Country(new Locale("",info[1]).getDisplayCountry(),info[0]));
        }
    }

    public ArrayList<Country> getCountries(Context context) {
        getCountryList(context);
        return countries;
    }
    public String getNetworkCountry(Context context){
        TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simCountry=telephonyManager.getSimCountryIso();
        if(simCountry!=null && simCountry.length()==2)
            return new Locale("",simCountry.toUpperCase(Locale.US)).getDisplayCountry();
        else if(telephonyManager.getPhoneType()!=TelephonyManager.PHONE_TYPE_CDMA){
            String country = telephonyManager.getNetworkCountryIso();
            if(country !=null && country.length()==2 )
                return new Locale("",country.toUpperCase(Locale.US)).getDisplayCountry();
        }
        return null;
    }
}
