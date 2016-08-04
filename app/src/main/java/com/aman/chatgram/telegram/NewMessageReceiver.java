package com.aman.chatgram.telegram;

import android.content.Intent;
import android.util.Log;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by Aman on 13-06-2016.
 */
public class NewMessageReceiver {
    private static NewMessageReceiver mInstance;
    private static HashMap<Long,Integer> messageMap=new HashMap<>();
    public static final int LIMIT=10000000;
    private NewMessageReceiver(){

    }
    public static NewMessageReceiver getInstance(){
        if(mInstance==null){
            mInstance=new NewMessageReceiver();
            EventBus.getDefault().register(mInstance);
        }
        return mInstance;
    }

    public void setIDforNewMsg(Long chatId,Integer msgId){
        synchronized (messageMap){
           // Log.e("AMAN",chatId+" "+msgId+" ");
            Integer messageId=messageMap.get(chatId);
            messageId=Math.max(messageId==null?0:messageId,msgId);
            if(messageId<=0){
                messageId=LIMIT;
            }
            messageMap.put(chatId,messageId);
        }
    }

    public int getIDforNewMsg(Long chatId){
        synchronized (messageMap){
            return LIMIT;
        }
    }

    public void onEvent(TdApi.UpdateNewMessage newMessage){
        setIDforNewMsg(newMessage.message.chatId,newMessage.message.id);
    }
}
