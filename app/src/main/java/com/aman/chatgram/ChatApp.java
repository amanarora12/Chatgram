package com.aman.chatgram;

import android.app.Application;
import android.content.Context;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TG;
import org.drinkless.td.libcore.telegram.TdApi;

import de.greenrobot.event.EventBus;

/**
 * Created by Aman on 11-06-2016.
 */
public class ChatApp extends Application {
    public static ChatApp sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
        TG.setDir(getFilesDir().toString());
    }

    public static ChatApp getInstance() {
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
    private class UpdatesHandler implements Client.ResultHandler{
        TdApi.TLObject tlObject;
        @Override
        public void onResult(TdApi.TLObject object) {
            tlObject=object;
            EventBus.getDefault().post(tlObject);
        }
    }
}
