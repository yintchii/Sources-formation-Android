package com.idevmob.tp2;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.gemalto.velosud.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class StationDetail extends Activity {

	Thread t1;
	Handler handler = new Handler();
	ProgressDialog progress;
	StationsDetailParser sdp;
	int number;
	Double lat;
	Double lng;
	Bitmap thumbnail;
	String nameTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_station);
		
		number = getIntent().getIntExtra("number", 0);
		lat = getIntent().getDoubleExtra("lat", 0.0);
		lng = getIntent().getDoubleExtra("lng", 0.0);
		nameTxt = getIntent().getStringExtra("name");
		
		if(number == 0){
			SharedPreferences myprefs = getSharedPreferences("numberFavorite", Context.MODE_PRIVATE);
			number = myprefs.getInt("number", 0);
		}
		
		StationsDB database = new StationsDB();
		Station s = database.getInfoStation(number);
		database.closeDB();
		
		lat = s.getLatitude();
		lng = s.getLongitude();
		nameTxt = s.getName();
				
		TextView name = (TextView) findViewById(R.id.name);
		name.setText(nameTxt);
		
		t1 = new Thread(getDetailStation);
		t1.start();
		progress =  ProgressDialog.show(this, 
				getResources().getString(R.string.app_name), "Chargement ...");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflate = getMenuInflater();
		inflate.inflate(R.menu.menu_detail, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.option1:
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tarik@alaoui.me", "alaoui@idevmob.fr"});
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Disponibilité vélo à " + nameTxt);
			emailIntent.putExtra(Intent.EXTRA_TEXT, "Salut, Il y a "+sdp.getAvailable()+" vélo(s) à "+ nameTxt);
			startActivity(Intent.createChooser(emailIntent, "Velo Sud"));
			
			break;
		case R.id.option2:
			Uri smsUri = Uri.parse("tel:0600000000");
			Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
			intent.putExtra("sms_body", "Il y a "+sdp.getAvailable()+" vélo(s) à "+ nameTxt);
			intent.setType("vnd.android-dir/mms-sms");
			startActivity(intent);
			break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	
	private Runnable getDetailStation = new Runnable(){

		@Override
		public void run() {
			sdp = new StationsDetailParser(number);
			handler.post(downloadGmapImage);
		}
		
	};
	
	private Runnable downloadGmapImage = new Runnable(){
		@Override
		public void run() {
			
			try{
				
				URL url = new URL("http://maps.google.com/maps/api/staticmap?center="
						+lat+","+lng
						+"&zoom=16&size=250x250&markers=color:blue%7Clabel:S|"+lat+","+lng+"&sensor=false");
				BitmapFactory bf = new BitmapFactory();
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				thumbnail = BitmapFactory.decodeStream(is);
				handler.post(refreshInfoOnScreen);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	};
	

	private Runnable refreshInfoOnScreen = new Runnable(){

		@Override
		public void run() {
			
			TextView emplacement = (TextView) findViewById(R.id.emplacement);
			TextView velos = (TextView) findViewById(R.id.velos);
			
			emplacement.setText(""+sdp.getFree());
			velos.setText(""+sdp.getAvailable());
			
			ImageView gmap = (ImageView) findViewById(R.id.gmap);
			gmap.setImageBitmap(thumbnail);

			
			progress.dismiss();
		}
		
	};


	@Override
	protected void onDestroy() {
		super.onDestroy();
		thumbnail.recycle();
		System.gc();
	}
	
	
	
	
}
