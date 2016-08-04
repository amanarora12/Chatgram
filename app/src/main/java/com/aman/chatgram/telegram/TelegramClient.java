package com.aman.chatgram.telegram;

import android.app.Activity;
import android.content.Context;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TG;
import org.drinkless.td.libcore.telegram.TdApi;

/**
 * Created by Aman on 12-06-2016.
 */
public class TelegramClient {
    public static void send(final Activity context, TdApi.TLFunction function, final TelegramResponseCallback responseCallback){
        TG.getClientInstance().send(function, new Client.ResultHandler() {
            @Override
            public void onResult(final TdApi.TLObject object) {
                if(responseCallback==null){
                    return;
                }
                if(context!=null){
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseCallback.onResult(object);
                        }
                    });
                }else {
                    responseCallback.onResult(object);
                }
            }
        });
    }
    public static void send(final Activity context, TdApi.TLFunction function, final TelegramResponseCallback responseCallback
            , final boolean runOnUiThread){
        TG.getClientInstance().send(function, new Client.ResultHandler() {
            @Override
            public void onResult(final TdApi.TLObject object) {
                if(responseCallback==null){
                    return;
                }
                if(runOnUiThread && context!=null){
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseCallback.onResult(object);
                        }
                    });
                }else {
                    responseCallback.onResult(object);
                }
            }
        });
    }
    public static void send(Activity activity,TdApi.TLFunction tlFunction){
        send(activity,tlFunction,null);
    }
    public interface TelegramResponseCallback{
        void onResult(TdApi.TLObject tlObject);
    }
}
