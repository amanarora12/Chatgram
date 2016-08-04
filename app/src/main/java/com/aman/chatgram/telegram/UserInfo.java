package com.aman.chatgram.telegram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aman.chatgram.ChatApp;
import com.aman.chatgram.ui.activities.MainActivity;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * Created by Aman on 12-06-2016.
 */
public class UserInfo {
    private static UserInfo mInstance;
    private TdApi.User loggedInUser;
    private String phoneNumber;
    public static final String PHONE="phone";
    private boolean hasLoggedInUser;
    private UserInfo(){

    }
    public static UserInfo getInstance(){
        if(mInstance==null){
            mInstance=new UserInfo();
           // EventBus.getDefault().register(mInstance);
        }
        return mInstance;
    }

    public TdApi.User getLoggedInUser(Context context) {
        if(loggedInUser==null){
           synchronized (UserInfo.class){
               getMyUser(context);
           }
        }
        return loggedInUser;
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
    public void getMyUser(Context context){
        if(hasLoggedInUser){
            return;
        }
        hasLoggedInUser=true;
        final CountDownLatch latch = new CountDownLatch(1);
        TelegramClient.send((Activity) context, new TdApi.GetMe(), new TelegramClient.TelegramResponseCallback() {
            @Override
            public void onResult(TdApi.TLObject tlObject) {
                if(tlObject instanceof TdApi.User){
                    loggedInUser= (TdApi.User) tlObject;
                }
                latch.countDown();
            }
        },false);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void logout(final Activity context){
        TdApi.AuthReset authReset=new TdApi.AuthReset(false);
        TelegramClient.send(context, authReset, new TelegramClient.TelegramResponseCallback() {
            @Override
            public void onResult(TdApi.TLObject tlObject) {
                setPhoneNumber(null);
                Intent intent=new Intent(context.getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });
    }
}
