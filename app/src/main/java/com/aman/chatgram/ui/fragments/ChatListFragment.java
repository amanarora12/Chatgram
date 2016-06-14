package com.aman.chatgram.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.aman.chatgram.R;
import com.aman.chatgram.model.Chat;
import com.aman.chatgram.telegram.ChatsHelper;
import com.aman.chatgram.telegram.TelegramClient;
import com.aman.chatgram.ui.adapters.ChatListAdapter;

import org.drinkless.td.libcore.telegram.TdApi;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment implements TelegramClient.TelegramResponseCallback,
        ChatsHelper.Callback<List<TdApi.Chat>>{

    private ListView chatListView;
    private List<Chat> chats;
    private ChatListAdapter chatListAdapter;
    private FloatingActionButton fabNewChat;
    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        chats=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat_list, container, false);
        ChatsHelper.getInstance().getChats(getActivity(),this);
        chatListView= (ListView) view.findViewById(R.id.chats_list);
        chatListAdapter=new ChatListAdapter(getActivity(),chats);
        chatListView.setAdapter(chatListAdapter);
        fabNewChat = (FloatingActionButton) view.findViewById(R.id.fab_new_chat);
        fabNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelegramClient.send(getActivity(),new TdApi.AuthReset());
            }
        });
        return view;
    }


    @Override
    public void onResult(List<TdApi.Chat> result) {
        for (TdApi.Chat chat : result) {
            chats.add(Chat.create(chat));
        }
        chatListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        if(chatListAdapter!=null){
            chatListAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public void onResult(TdApi.TLObject tlObject) {
        if(tlObject.getConstructor()==TdApi.Chats.CONSTRUCTOR){
            for(TdApi.Chat chat:((TdApi.Chats)tlObject).chats){
                chats.add(Chat.create(chat));
            }
            chatListAdapter.notifyDataSetChanged();
        }
    }
    public void onEventMainThread(TdApi.UpdateNewMessage newMessage) {
        for (int i = 0; i < chats.size(); i++) {
            Chat chat = chats.get(i);
            if (chat.getChatId() == newMessage.message.chatId) {
                chats.remove(i);
                chats.add(0, chat);
                break;
            }
        }
        chatListAdapter.notifyDataSetChanged();
    }


    public void onEventMainThread(TdApi.UpdateChatTitle chatTitle) {
        long chatId = chatTitle.chatId;
        ChatsHelper.getInstance().getChat(getActivity(),chatId, new ChatsHelper.Callback<TdApi.Chat>() {
            @Override
            public void onResult(TdApi.Chat result) {
                boolean isChatExist = false;
                for (Chat chat : chats) {
                    if (chat.getChatId() == result.id) {
                        isChatExist = true;
                        break;
                    }
                }
                if (!isChatExist) {

                    chats.add(0, Chat.create(result));
                    chatListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
