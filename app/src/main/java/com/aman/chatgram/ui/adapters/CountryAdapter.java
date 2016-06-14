package com.aman.chatgram.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aman.chatgram.R;
import com.aman.chatgram.helper.CountrySingleton;
import com.aman.chatgram.model.Country;

import java.util.ArrayList;

/**
 * Created by Aman on 12-06-2016.
 */
public class CountryAdapter extends BaseAdapter {
    private ArrayList<Country> countries;
    private Context context;
    private LayoutInflater inflater;

    public CountryAdapter(Context context){
        this.context=context;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //countries= CountrySingleton.getInstance().getCountries(context);
    }

    public void setCountries(ArrayList<Country> countries) {
        this.countries = countries;
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.list_item_country,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.countryName= (TextView) convertView.findViewById(R.id.txt_country_name);
            viewHolder.countryCode= (TextView) convertView.findViewById(R.id.txt_country_code);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Country country=countries.get(position);
        viewHolder.countryName.setText(country.getCountryName());
        viewHolder.countryCode.setText(country.getCountryCallingCode());
        return convertView;
    }

    class ViewHolder{
        TextView countryName;
        TextView countryCode;
    }
}
