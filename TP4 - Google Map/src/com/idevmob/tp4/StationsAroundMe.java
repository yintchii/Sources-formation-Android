package com.idevmob.tp4;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class StationsAroundMe extends MapActivity {

	ArrayList<Station> arrList;
	LocationManager lmanager;
	Location location;
	
	MapView carte;
	MapController mc;
	List<Overlay> mapOverlay;
	ItemOverlay userOverlays;
	ItemOverlay stationOverlays;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arroundme);
		
		StationsDB database = new StationsDB();
		arrList = database.getListOfStations("", 0);
		database.closeDB();
		
		// GPS
		
		lmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		try{
			location = lmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Log.i("TP4", location.getLatitude()  +" - " + location.getLongitude());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		lmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new gpslistenerLocation());
		
		// MapView
		carte = (MapView) findViewById(R.id.mymap);
		carte.setBuiltInZoomControls(true);
		mc = carte.getController();
		mapOverlay = carte.getOverlays();
	
		Drawable bike = getResources().getDrawable(R.drawable.bike);
		stationOverlays = new ItemOverlay(bike, this);

		Drawable user = getResources().getDrawable(R.drawable.user);
		userOverlays = new ItemOverlay(user, this);
		
	}

	
	public void showListOfStationAroundMe(){
		mapOverlay.clear();
		
		// Stations
		for(int i=0; i<arrList.size(); i++)
		{

			float results[] = {0,0,0};
			Location.distanceBetween(arrList.get(i).getLatitude(), 
					arrList.get(i).getLongitude(), 
					43.294627, 5.37519, results);
			
			if(results[0] <= 300.0)
			{
				Double latstation = arrList.get(i).getLatitude() * 1E6;
				Double lngstation = arrList.get(i).getLongitude() * 1E6;
				GeoPoint pointStation = new GeoPoint(latstation.intValue(), lngstation.intValue());
				OverlayItem stationO = new OverlayItem(pointStation, 
						arrList.get(i).getName(), 
						""+arrList.get(i).getNumber());
				
				stationOverlays.addOverlay(stationO);
			}
		}		
		// refresh ListView
		mapOverlay.add(stationOverlays);
		
		
		// MyPostion
		Double latme = location.getLatitude() * 1E6;
		Double lngme = location.getLongitude() * 1E6;
		GeoPoint pointMe = new GeoPoint(latme.intValue(), lngme.intValue());
		OverlayItem userO = new OverlayItem(pointMe, "Moi", "");
		userOverlays.addOverlay(userO);
		mapOverlay.add(userOverlays);
		mc.animateTo(pointMe);
		mc.setZoom(17);

	}
	

	class gpslistenerLocation implements LocationListener{

		@Override
		public void onLocationChanged(Location arg0) {
			
			Log.i("TP4", "New Position : " + arg0.getLatitude() + ", " + arg0.getLongitude());
			
			location = arg0;
			showListOfStationAroundMe();

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
	}


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
