package com.idevmob.tp3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class SqliteHelper {

	public static String dbpath = "/data/data/com.gemalto.velosud/";
	public static String dbname = "velosud.sqlite";
	
	Context context;
	
	public SqliteHelper(Context context){
		this.context = context;
	}
	
	public boolean isDatabaseExist(){
		boolean exist = false;
		
		String mypath = dbpath + dbname;
		Log.i("TP4", mypath);
		File dbfile = new File(mypath);
		if(dbfile.isFile())
			exist = true;
		
		return exist;
	}
	
	public void copyDatabase(){

		String mypath = dbpath + dbname;
		try {
			FileOutputStream fos = new FileOutputStream(mypath);
			InputStream is = context.getAssets().open(dbname);
			
			byte[] buffer = new byte[1024];
			int lenght;
			while((lenght = is.read(buffer)) > 0){
				fos.write(buffer, 0, lenght);
			}
			fos.flush();
			fos.close();
			is.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
