package com.aman.chatgram.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aman.chatgram.R;
import com.aman.chatgram.telegram.TelegramClient;

import org.drinkless.td.libcore.telegram.TdApi;

public class OTPActivity extends AppCompatActivity implements TelegramClient.TelegramResponseCallback{
    private EditText edtOtp;
    private Button btnStart;
    private TextView txtIncorrectCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtOtp= (EditText) findViewById(R.id.edt_otp);
        btnStart= (Button) findViewById(R.id.btn_get_started);
        txtIncorrectCode= (TextView) findViewById(R.id.incorrect_code);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });
    }
    private void verifyCode(){
        String otp=edtOtp.getText().toString();
        if(!otp.equals("")){
            TdApi.AuthSetCode checkAuthCode=new TdApi.AuthSetCode(otp);
            TelegramClient.send(this,checkAuthCode,this);
        }else{
            edtOtp.setError(getString(R.string.blank_edt_error));
        }

    }
    @Override
    public void onResult(TdApi.TLObject tlObject) {
        if(tlObject.getConstructor()==TdApi.AuthStateOk.CONSTRUCTOR){
            Intent intent=new Intent(getApplicationContext(),ChatListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else{
            txtIncorrectCode.setVisibility(View.VISIBLE);
        }
    }
}
