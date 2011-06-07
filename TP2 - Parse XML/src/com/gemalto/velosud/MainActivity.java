package com.gemalto.velosud;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity 
extends Activity
implements OnItemClickListener{

	ListView liste_stations;
	String[] stations;
	
	ArrayList<Station> arrList;
	
	Thread t1;
	Handler handler = new Handler();
	ProgressDialog progress;
	
	private Runnable getListOfStation = new Runnable(){

		@Override
		public void run() {
			
	        try {
				StationsParser sp = new StationsParser();
				arrList = sp.arrList;
				Log.i("TP2", "Stations : " + arrList.size());

				handler.post(RefreshListOfStation);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}
		
	};
	
	
	private Runnable RefreshListOfStation = new Runnable(){

		@Override
		public void run() {
			progress.dismiss();
			Toast.makeText(MainActivity.this, "Stations" + arrList.size(), Toast.LENGTH_SHORT).show();
			        
	        liste_stations = (ListView) findViewById(R.id.liste_stations);
	        
	        StationsAdapter sa = new StationsAdapter(getBaseContext(), MainActivity.this, arrList);
	        liste_stations.setAdapter(sa);
	        
	        //liste_stations.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, stations));
	        liste_stations.setOnItemClickListener(MainActivity.this);
	       
			
		}
		
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        t1 = new Thread(getListOfStation);
        t1.start();
        progress = ProgressDialog.show(this, getResources().getString(R.string.app_name), "Chargement en cours ...", true);
                
    }

	@Override
	public void onItemClick(AdapterView<?> stationsAdapter, View ligne, int position, long arg3) {
		
		LayoutInflater inflate = getLayoutInflater();
		View layout = inflate.inflate(R.layout.mon_toast, null);
		TextView msg = (TextView) layout.findViewById(R.id.text1);
		msg.setText("" + arrList.get(position).getNumber());

		Toast t = new Toast(this);
		t.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		t.setDuration(Toast.LENGTH_SHORT);
		t.setView(layout);
		
		t.show();
		
		//Toast.makeText(this, this.stations[position], Toast.LENGTH_SHORT).show();
	}

}