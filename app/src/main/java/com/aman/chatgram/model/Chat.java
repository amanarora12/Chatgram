package com.aman.chatgram.model;

import android.view.View;

import com.aman.chatgram.ui.adapters.ChatListAdapter;

import org.drinkless.td.libcore.telegram.TdApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

/**
 * Created by Aman on 13-06-2016.
 */
public class Chat {
    private TdApi.Chat chat;
    private long chatId;
    private ChatListAdapter.ViewHolder chatHolder;
    public Chat(TdApi.Chat chat){
        this.chat=chat;
        this.chatId=chat.id;
    }

    public TdApi.Chat getChat() {
        return chat;
    }

    public long getChatId() {
        return chatId;
    }

    public ChatListAdapter.ViewHolder getChatHolder() {
        return chatHolder;
    }

    public void onViewAttached(ChatListAdapter.ViewHolder viewHolder){
        chatHolder=viewHolder;
        newMsgUpdate(getChat().topMessage);
    }
    public void onViewDetached(ChatListAdapter.ViewHolder viewHolder){
        if(viewHolder==chatHolder){
            chatHolder=null;
        }
    }
    private void newMsgUpdate(TdApi.Message newMsg){
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
        dateFormat.setTimeZone(TimeZone.getDefault());
        getChatHolder().txtTime.setText(dateFormat.format(newMsg.date*1000));

        if(newMsg.message instanceof TdApi.MessageText){
            String msg=((TdApi.MessageText) newMsg.message).text;
            getChatHolder().txtMsg.setText(msg);
        }
    }

    public void onNewMsgReceived(TdApi.UpdateNewMessage newMessage){
        if(getChatId()==newMessage.message.chatId){
            chat.topMessage=newMessage.message;
            if(getChatHolder()!=null){
                getChatHolder().txtUnreadMsgs.setText(chat.unreadCount);
                getChatHolder().txtUnreadMsgs.setVisibility(View.VISIBLE);
                newMsgUpdate(newMessage.message);
            }
        }
    }

    public void onEventMainThread(TdApi.UpdateChatReadInbox updateChatReadInbox){
           if(getChatId()==updateChatReadInbox.chatId){
               chat.unreadCount=updateChatReadInbox.unreadCount;
               chat.lastReadInboxMessageId=updateChatReadInbox.lastRead;
               if(getChatHolder()!=null){
                   getChatHolder().txtUnreadMsgs.setVisibility(View.VISIBLE);
                   getChatHolder().txtUnreadMsgs.setText(updateChatReadInbox.unreadCount);
               }
           }
    }

    public void onEventMainThread(TdApi.UpdateMessageDate messageDate){
            if(getChatId()==messageDate.chatId){
                getChatHolder().txtUnreadMsgs.setVisibility(View.GONE);
            }
    }

    public void onEventMainThread(TdApi.UpdateMessageId messageId){
            if(getChatId()==messageId.chatId){
                if(getChat().topMessage.id==messageId.oldId){
                    getChat().topMessage.id=messageId.newId;
                }
            }
    }

    public static Chat create(TdApi.Chat chat){
        Chat chat1=new Chat(chat);
        EventBus.getDefault().register(chat1);
        return chat1;
    }
}
