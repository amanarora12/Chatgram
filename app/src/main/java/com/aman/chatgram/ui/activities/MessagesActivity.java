package com.aman.chatgram.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aman.chatgram.Constants;
import com.aman.chatgram.R;
import com.aman.chatgram.model.ChatMessage;
import com.aman.chatgram.model.ChatMessageFactory;
import com.aman.chatgram.telegram.ChatsHelper;
import com.aman.chatgram.telegram.NewMessageReceiver;
import com.aman.chatgram.telegram.TelegramClient;
import com.aman.chatgram.telegram.UserInfo;
import com.aman.chatgram.ui.adapters.MessagesAdapter;
import com.aman.chatgram.utility.CircularImageView;
import com.aman.chatgram.utility.CreateInitials;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

public class MessagesActivity extends AppCompatActivity implements TelegramClient.TelegramResponseCallback,
        ChatsHelper.Callback<TdApi.Chat>{
    private Toolbar toolbar;
    private RecyclerView chatHistoryList;
    private TextView txtNoChat;
    private EditText edtMsg;
    private ImageView imgSend;
    private LinearLayoutManager layoutManager;

    private TdApi.User user;
    private HashMap<Long,TdApi.User> userHashMap;
    private long chatId;
    private TdApi.Chat chat;
    private List<ChatMessage> chatMessages=new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private boolean isHistoryEmpty;
    private boolean loading;
    private boolean allLoaded;
    private int msgsLoadedCount=0;
    private static final int LOAD_MESSAGES_COUNT = 10;
    private TdApi.PrivateChatInfo chatInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_messages);

        Intent intent=getIntent();
        chatId=intent.getLongExtra(Constants.EXTRA_CHAT_ID,0);
        userHashMap=new HashMap<>();
        TdApi.User loggedInUser= UserInfo.getInstance().getLoggedInUser(this);
        userHashMap.put((long) loggedInUser.id,loggedInUser);

        TdApi.GetUser getUser=new TdApi.GetUser((int) chatId);
        TelegramClient.send(this,getUser,this);
        ChatsHelper.getInstance().getChat(this,chatId,this);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtMsg= (EditText) findViewById(R.id.edt_msg);
        imgSend= (ImageView) findViewById(R.id.img_send);
        chatHistoryList= (RecyclerView) findViewById(R.id.chat_history);
        txtNoChat= (TextView) findViewById(R.id.txt_no_chat);

        isHistoryEmpty=chatMessages.size()<=0;
        txtNoChat.setVisibility(isHistoryEmpty?View.VISIBLE:View.GONE);
        chatHistoryList.setVisibility(isHistoryEmpty?View.GONE:View.VISIBLE);

        layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatHistoryList.setLayoutManager(layoutManager);

        messagesAdapter=new MessagesAdapter(this,chatMessages,loggedInUser.id);
        chatHistoryList.setAdapter(messagesAdapter);

        chatHistoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(recyclerView.computeVerticalScrollOffset()<=50 && dy<0){
                    if(loading||allLoaded){
                        return;
                    }
                    loadMsgs();
                }
            }
        });
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMesssage(edtMsg.getText().toString());
            }
        });


    }

    private void loadMsgs() {
        loading=true;
        int lastMsgId=10000000;
        TdApi.GetChatHistory getHistory=new TdApi.GetChatHistory(chatId,lastMsgId,msgsLoadedCount,LOAD_MESSAGES_COUNT);
        TelegramClient.send(this,getHistory,this);
    }

    private void setupToolbar(String name){
        View view = getLayoutInflater().inflate(R.layout.msg_toolbar, null);
        ImageView initialsBg= (ImageView) view.findViewById(R.id.img_user_initals);
        TextView txtInitials= (TextView) view.findViewById(R.id.txt_initials);
        TextView sender= (TextView) view.findViewById(R.id.txt_sender);
        sender.setText(name);

        CircularImageView imageView=CreateInitials.createCircle(this,null,name);
        CreateInitials.createInitialsImage(true,name,txtInitials);
        initialsBg.setImageDrawable(imageView);
        initialsBg.setBackgroundColor(Color.TRANSPARENT);
        toolbar.addView(view);
    }
    private void onHistoryLoaded(TdApi.Messages msgs){
        TdApi.Message[] messageList=msgs.messages;
        if(messageList.length==0){
            allLoaded=true;
        }
        if(msgsLoadedCount+messageList.length>0){
            txtNoChat.setVisibility(View.GONE);
            chatHistoryList.setVisibility(View.VISIBLE);
        }else {
            txtNoChat.setVisibility(View.VISIBLE);
            chatHistoryList.setVisibility(View.GONE);
        }
        boolean needsAdjustScroll = false;
        for (int i =messageList.length - 1; i >= 0; i--) {
            TdApi.Message message = messageList[i];
            TdApi.User sender = userHashMap.get((long) message.fromId);
            if (msgsLoadedCount == 0) {
                chatMessages.add(ChatMessageFactory.create(chat, message, sender,this));
            }
            else {
                int indexToInsert = (messageList.length - 1) - i;
                needsAdjustScroll = true;
                chatMessages.add(indexToInsert, ChatMessageFactory.create(chat, message, sender,this));
            }
        }
        messagesAdapter.notifyDataSetChanged();
        if (needsAdjustScroll) {
            layoutManager.scrollToPositionWithOffset(messageList.length, 0);
        }
        msgsLoadedCount += messageList.length;
        loading = false;

    }
    private void sendMesssage(String text){
        TdApi.InputMessageText messageText=new TdApi.InputMessageText(text);
        TdApi.SendMessage sendMessage=new TdApi.SendMessage(chat.id,messageText);
        TelegramClient.send(this,sendMessage,this);
    }
    public void onEventMainThread(TdApi.UpdateNewMessage newMsg){
        if(newMsg.message.chatId!=chatId){
            return;
        }
        TdApi.User sender=userHashMap.get(newMsg.message.fromId);
        chatMessages.add(ChatMessageFactory.create(chat,newMsg.message,sender,MessagesActivity.this));
        messagesAdapter.notifyDataSetChanged();
        layoutManager.scrollToPositionWithOffset(chatMessages.size()-1,0);

        chatHistoryList.setVisibility(View.VISIBLE);
        isHistoryEmpty=false;
    }

    @Override
    public void onResult(TdApi.Chat result) {
        chat=result;
        if(chat.type instanceof TdApi.PrivateChatInfo){
            chatInfo= (TdApi.PrivateChatInfo) chat.type;
            userHashMap.put((long) chatInfo.user.id,chatInfo.user);
            loadMsgs();
        }
    }

    @Override
    public void onResult(TdApi.TLObject tlObject) {
        if(tlObject instanceof TdApi.User){
            user=((TdApi.User)tlObject);
            setupToolbar(user.firstName+" "+user.lastName);
        }
        else if(tlObject instanceof TdApi.Messages){
            onHistoryLoaded((TdApi.Messages) tlObject);
        }
        else if(tlObject instanceof TdApi.Message){
            TdApi.User sender = UserInfo.getInstance().getLoggedInUser(MessagesActivity.this);
            chatMessages.add(ChatMessageFactory.create(chat, (TdApi.Message) tlObject, sender,MessagesActivity.this));
            messagesAdapter.notifyDataSetChanged();
            layoutManager.scrollToPositionWithOffset(chatMessages.size() - 1, 0);
            edtMsg.setText(null);
            edtMsg.setTag(null);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        else if(tlObject instanceof TdApi.Error){
            Toast.makeText(this,"Error Occurred",Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
