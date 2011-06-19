package com.idevmob.tp4;

import java.util.Random;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class ServiceStation extends Service {

	private final IBinder mBinder = new ServiceStationBinder();
	Thread traitement;
	int duration = 30 * 1000;
	boolean stopped = true;
	Context context;

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class ServiceStationBinder extends Binder{
		ServiceStation getService(){
			return ServiceStation.this;
		}
	}

	@Override
	public void onCreate() {
		startService();
		Toast.makeText(context, "Service créé", Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void onDestroy() {
		stopService();
		Toast.makeText(context, "Service supprimé", Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	public void startService(){
		stopped = false;
		context = getBaseContext();
		traitement = new Thread(spyInfoStation);
		traitement.start();
	}
	
	private Runnable spyInfoStation = new Runnable(){

		@Override
		public void run() {
				
			do{
				try{
				
					StationsDetailParser sdp = new StationsDetailParser(1002);
					Random rnd = new Random();
					String message = sdp.getFree() + " vélo(s) disponible(s) - " + rnd.nextInt(10000);
					
					Notification notification = new Notification(R.drawable.bike, message, System.currentTimeMillis());
					
					Intent detailIntent = new Intent(context, StationDetail.class);
					detailIntent.putExtra("number", 1002);
					
					SharedPreferences myprefs = getSharedPreferences("numberFavorite", Context.MODE_PRIVATE);
					SharedPreferences.Editor edit = myprefs.edit();
					edit.putInt("number", 1002);
					edit.commit();
					
					PendingIntent pendingIntent = PendingIntent.getActivity(
							context, 
							0, 
							detailIntent, 0);
					
					notification.setLatestEventInfo(
							context, 
							getResources().getString(R.string.app_name), 
							message, pendingIntent);
					
					NotificationManager nm = (NotificationManager) 
					context.getSystemService(Context.NOTIFICATION_SERVICE);
					
					nm.notify(1000, notification);
					
				Thread.sleep(duration);
				}catch(Exception e){
					e.printStackTrace();
				}
			}while(!stopped);
			
		}
		
	};
	
	public void stopService(){
		stopped = true;
		if(traitement.isAlive()) traitement.stop();
	}
}
