package com.aman.chatgram.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aman.chatgram.Constants;
import com.aman.chatgram.R;
import com.aman.chatgram.helper.CountrySingleton;
import com.aman.chatgram.model.Country;
import com.aman.chatgram.ui.adapters.CountryAdapter;

import java.util.ArrayList;

public class ChooseCountryActivity extends AppCompatActivity {
    private ListView countryList;
    private CountryAdapter countryAdapter;
    private ArrayList<Country> countries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_country);
        countries= CountrySingleton.getInstance().getCountries(this);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.choose_country));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countryList= (ListView) findViewById(R.id.country_list);
        countryAdapter=new CountryAdapter(this);
        countryAdapter.setCountries(countries);
        countryList.setAdapter(countryAdapter);

        countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name=countries.get(position).getCountryName();
                String callingCode=countries.get(position).getCountryCallingCode();
                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_COUNTRY_NAME,name);
                intent.putExtra(Constants.EXTRA_COUNTRY_CODE,callingCode);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }
}
