package com.idevmob.tp4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
	
	private ServiceStation mService;
	Intent bindIntent;
	List<RunningServiceInfo> services;
	
	private Runnable getListOfStation = new Runnable(){

		@Override
		public void run() {
			
	        try {
				StationsParser sp = new StationsParser(getBaseContext());
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
			//Toast.makeText(MainActivity.this, "Stations" + arrList.size(), Toast.LENGTH_SHORT).show();
			 
			// AutoComplete
			AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.searchTxt);
			stations = new String[arrList.size()];
			for(int i=0; i<arrList.size(); i++){
				stations[i] = arrList.get(i).getName();
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, 
					android.R.layout.simple_dropdown_item_1line, 
					stations);
			search.setAdapter(adapter);
			search.setOnItemClickListener(
					new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
					
					String name = arg0.getAdapter().getItem(position).toString();
					
					int i = 0;
					boolean trouve = false;
					do{
						if(arrList.get(i++).getName().equals(name))
							trouve = true;
						
					}while(i<arrList.size() && !trouve);
					
					Log.i("TP4", "Position = " + (i-1));
					goToDetailScreen(i-1);
				}
				
			});
			
			
			// LISTEVIEW
	        liste_stations = (ListView) findViewById(R.id.liste_stations);
	        
	        StationsAdapter sa = new StationsAdapter(getBaseContext(), MainActivity.this, arrList);
	        liste_stations.setAdapter(sa);
	        
	        //liste_stations.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, stations));
	        liste_stations.setOnItemClickListener(MainActivity.this);
	       
			
		}
		
	};
	
	public boolean isServiceStarted(){
		boolean isServiceFound = false;
		
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		services = am.getRunningServices(Integer.MAX_VALUE);
		int i = 0;
		do{
			
			//Log.i("TP4", services.get(i).service.getClassName());
			
			if(getPackageName().equals(services.get(i).service.getPackageName())){
				if((getPackageName()+".ServiceStation").equals(services.get(i).service.getClassName())){
					isServiceFound = true;
				}
			}
			
			i++;
		}while(!isServiceFound && i< services.size());
		
		
		
		
		return isServiceFound;
	}
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        bindIntent = new Intent(this, ServiceStation.class);
        
        SqliteHelper sh = new SqliteHelper(getBaseContext());
        if(!sh.isDatabaseExist())
        	sh.copyDatabase();
        
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
			
			//t.show();
			goToDetailScreen(position);
			//Toast.makeText(this, this.stations[position], Toast.LENGTH_SHORT).show();
		
	}

	private void goToDetailScreen(int position) {
			
		Intent intent = new Intent(this, StationDetail.class);
		intent.putExtra("name", arrList.get(position).getName());
		intent.putExtra("number", arrList.get(position).getNumber());
		intent.putExtra("lat", arrList.get(position).getLatitude());
		intent.putExtra("lng", arrList.get(position).getLongitude());
		
		startActivity(intent);
	}

	
	private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
	    float pk = (float) (180/3.14169);
	    float a1 = lat_a / pk;
	    float a2 = lng_a / pk;
	    float b1 = lat_b / pk;
	    float b2 = lng_b / pk;

	    float t1 = FloatMath.cos(a1) * FloatMath.cos(a2) * FloatMath.cos(b1) * FloatMath.cos(b2);
	    float t2 = FloatMath.cos(a1) * FloatMath.sin(a2) * FloatMath.cos(b1) * FloatMath.sin(b2);
	    float t3 = FloatMath.sin(a1) * FloatMath.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);

	    return 6366000*tt;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.option1:
			// Intent vers Gmap + stations around me
			Intent intent = new Intent(this, StationsAroundMe.class);
			startActivity(intent);	
			return true;

		case R.id.option2:
			finish();
			return true;
		case R.id.option3:
			if(isServiceStarted()){
				Toast.makeText(this, "Le service est déjà en cours d'execution !", Toast.LENGTH_SHORT).show();
			}else{
				startService(bindIntent);
			}
			return true;
	
		case R.id.option4:
			if(isServiceStarted()){
				stopService(bindIntent);
			}else{
				Toast.makeText(this, "Le service n'existe pas !", Toast.LENGTH_SHORT).show();
			}
			return true;	

		default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}

	/*
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
	
		return super.onPrepareOptionsMenu(menu);
	}*/

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	

}