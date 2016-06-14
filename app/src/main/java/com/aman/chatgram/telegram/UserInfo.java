package com.aman.chatgram.telegram;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aman.chatgram.ChatApp;

import org.drinkless.td.libcore.telegram.TdApi;

import de.greenrobot.event.EventBus;

/**
 * Created by Aman on 12-06-2016.
 */
public class UserInfo {
    private static UserInfo mInstance;
    private TdApi.User user;
    private String phoneNumber;
    public static final String PHONE="phone";
    private UserInfo(){

    }
    public static UserInfo getInstance(){
        if(mInstance==null){
            mInstance=new UserInfo();
           // EventBus.getDefault().register(mInstance);
        }
        return mInstance;
    }

    public TdApi.User getUser() {
        if(user==null){

        }
        return user;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(ChatApp.getAppContext()).edit();
        editor.putString(PHONE,phoneNumber);
        editor.apply();
    }

    public String getPhoneNumber() {
        if(phoneNumber==null){
            phoneNumber=PreferenceManager.getDefaultSharedPreferences(ChatApp.getAppContext()).getString(PHONE,null);
        }
        return phoneNumber;
    }
}
