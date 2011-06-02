package com.idevmob.listview;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CountryAdapter extends ArrayAdapter<Country> {

	ArrayList<Country> arrList;
	
	public CountryAdapter(Context context, Activity activity, ArrayList<Country> countries) {
			super(context, 1, countries);
			
			this.arrList = countries;
			
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		
		View rowView = inflater.inflate(R.layout.country_row, null);
		
		TextView country = (TextView) rowView.findViewById(R.id.country);
		country.setText(arrList.get(position).getmCountry());

		TextView code = (TextView) rowView.findViewById(R.id.code);
		code.setText(arrList.get(position).getmCode());
		
		return rowView;

		
	}

	
	
	
}
