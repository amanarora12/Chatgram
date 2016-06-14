package com.aman.chatgram.ui.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aman.chatgram.R;
import com.aman.chatgram.telegram.TelegramClient;
import com.aman.chatgram.ui.adapters.TabsPagerAdapter;

import org.drinkless.td.libcore.telegram.TdApi;

public class ChatListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsPagerAdapter pagerAdapter;
    private FloatingActionButton fabNewChat;
    private int[] tabIcons={R.drawable.ic_chat,R.drawable.ic_contacts};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        pagerAdapter=new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setTabIcons();
        fabNewChat = (FloatingActionButton)findViewById(R.id.fab_new_chat);
        fabNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelegramClient.send(ChatListActivity.this,new TdApi.AuthReset());
            }
        });
    }
    private void setTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
}
