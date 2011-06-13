package com.idevmob.tp4;

import java.util.ArrayList;

import com.gemalto.velosud.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StationsAdapter extends ArrayAdapter<Station> {

	
	ArrayList<Station> arrListLocal;
	
	public StationsAdapter(Context context,  Activity activity, ArrayList<Station> arrList) {
		super(context, 1, arrList);
		arrListLocal = arrList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View rowView = inflater.inflate(R.layout.mon_toast, null);
		
		TextView name = (TextView) rowView.findViewById(R.id.text1);
		name.setText(arrListLocal.get(position).getName());
		
		return rowView;
	}
	
	
	

}
