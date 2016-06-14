package com.aman.chatgram.telegram;

import android.app.Activity;
import android.util.Log;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aman on 13-06-2016.
 */
public class ChatsHelper {
    private static ChatsHelper mInstance;
    private HashMap<Long,TdApi.Chat> chatsMap=new HashMap<>();

    private ChatsHelper(){

    }
    public static ChatsHelper getInstance(){
        if(mInstance==null){
            mInstance=new ChatsHelper();
        }
        return mInstance;
    }
    public void getChat(Activity context,Long chatId, final Callback<TdApi.Chat> callback){
        TdApi.Chat chat=chatsMap.get(chatId);
        if(chat!=null){
            callback.onResult(chat);
            return;
        }else{
            TdApi.GetChat getChat=new TdApi.GetChat(chatId);
            TelegramClient.send(context, getChat, new TelegramClient.TelegramResponseCallback() {
                @Override
                public void onResult(TdApi.TLObject tlObject) {
                    TdApi.Chat newChat= (TdApi.Chat) tlObject;
                    chatsMap.put(newChat.id,newChat);
                    callback.onResult(newChat);
                }
            });
        }
    }

    public void getChats(Activity context,final Callback<List<TdApi.Chat>> callback){
        TdApi.GetChats chatList=new TdApi.GetChats(0,50);
        TelegramClient.send(context, chatList, new TelegramClient.TelegramResponseCallback() {
            @Override
            public void onResult(TdApi.TLObject tlObject) {
                int c=0;
                List<TdApi.Chat> chats=new ArrayList<>();
                for(TdApi.Chat chat:((TdApi.Chats)tlObject).chats){
                    NewMessageReceiver.getInstance().setIDforNewMsg(chat.topMessage.chatId,chat.topMessage.id);
                    chatsMap.put(chat.id,chat);
                    chats.add(chat);
                }
                callback.onResult(chats);

            }
        });
    }
    public interface Callback<T>{
        void onResult(T result);
    }
}
