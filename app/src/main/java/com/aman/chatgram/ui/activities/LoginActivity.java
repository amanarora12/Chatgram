package com.aman.chatgram.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aman.chatgram.Constants;
import com.aman.chatgram.R;
import com.aman.chatgram.helper.CountrySingleton;
import com.aman.chatgram.model.Country;
import com.aman.chatgram.telegram.TelegramClient;
import com.aman.chatgram.telegram.UserInfo;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements TelegramClient.TelegramResponseCallback{
    private TextView edtCountryName;
    private EditText edtCountryCode;
    private EditText edtPhoneNumber;
    private String phoneNumber;
    private String countryCode;
    private Button btnVerify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountrySingleton countrySingleton=CountrySingleton.getInstance();
        countrySingleton.getCountryList(this);
        setContentView(R.layout.activity_login);

        edtCountryName= (TextView) findViewById(R.id.edt_country_name);
        edtCountryCode= (EditText) findViewById(R.id.edt_country_code);
        edtPhoneNumber= (EditText) findViewById(R.id.edt_phone_number);
        btnVerify= (Button) findViewById(R.id.btn_verify);
        String currentCountry=countrySingleton.getNetworkCountry(this);
        ArrayList<Country> countries=countrySingleton.getCountries(this);
        if(currentCountry!=null){
            for(Country country:countries){
                if(country.getCountryName().equalsIgnoreCase(currentCountry)){
                    edtCountryName.setText(country.getCountryName());
                    edtCountryCode.setText(country.getCountryCallingCode());
                }
            }
        }else{
            edtCountryName.setHint(getResources().getString(R.string.choose_country));
        }
        //edtCountryName.setText(countrySingleton.getNetworkCountry(this));
        edtCountryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCountry();
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhoneNumber();
            }
        });
    }
    public void selectCountry(){
        Intent intent=new Intent(LoginActivity.this,ChooseCountryActivity.class);
        startActivityForResult(intent,RESULT_FIRST_USER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            edtCountryName.setText(data.getStringExtra(Constants.EXTRA_COUNTRY_NAME));
            edtCountryCode.setText(data.getStringExtra(Constants.EXTRA_COUNTRY_CODE));
        }
    }

    private void verifyPhoneNumber(){
        countryCode=edtCountryCode.getText().toString();
        phoneNumber=edtPhoneNumber.getText().toString();
        if(!phoneNumber.equals("")){
            TdApi.AuthSetPhoneNumber setAuthPhoneNumber=new TdApi.AuthSetPhoneNumber(countryCode+phoneNumber);
            TelegramClient.send(this,setAuthPhoneNumber,this);
        }else{
            showInvalidPhoneError();
        }
    }
    private void showInvalidPhoneError(){
        Snackbar snackbar=Snackbar.make(findViewById(R.id.root), R.string.invalid_phone_no,Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }
    @Override
    public void onResult(TdApi.TLObject tlObject) {
        if(tlObject.getConstructor()==TdApi.AuthStateWaitSetCode.CONSTRUCTOR){
            UserInfo.getInstance().setPhoneNumber(countryCode+phoneNumber);
            Intent intent=new Intent(getApplicationContext(),OTPActivity.class);
            intent.putExtra(Constants.EXTRA_PHONE_NUMBER,countryCode+phoneNumber);
            startActivity(intent);
        }
        else {
            showInvalidPhoneError();
        }
    }
}
