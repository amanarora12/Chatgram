package com.aman.chatgram.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aman.chatgram.telegram.TelegramClient;

import org.drinkless.td.libcore.telegram.TdApi;

public class MainActivity extends AppCompatActivity implements TelegramClient.TelegramResponseCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
        TdApi.AuthGetState getAuthState=new TdApi.AuthGetState();
        TelegramClient.send(this,getAuthState,this);
    }

    @Override
    public void onResult(TdApi.TLObject tlObject) {
        if(tlObject instanceof TdApi.AuthStateOk){
            startActivity(new Intent(getApplicationContext(),ChatListActivity.class));
        }
        else if(tlObject instanceof TdApi.AuthStateWaitSetCode){
            startActivity(new Intent(getApplicationContext(),OTPActivity.class));
        }
        else if(tlObject instanceof TdApi.AuthStateWaitSetPhoneNumber){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        else if(tlObject instanceof TdApi.AuthStateWaitSetName){

        }
        else if (tlObject instanceof TdApi.Error){
            TelegramClient.send(this,new TdApi.AuthReset(),this);
        }
        else{
        }
    }
}
