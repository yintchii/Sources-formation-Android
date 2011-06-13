package com.idevmob.tp3;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StationsDB {

	public SQLiteDatabase database;
	
	public StationsDB(){
		database = SQLiteDatabase.openDatabase(
				SqliteHelper.dbpath + SqliteHelper.dbname, 
				null, 
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}
	
	public void closeDB(){
		database.close();
	}
	
	public void insertStation(Station s){
		Cursor c = database.rawQuery("select count(*) from stations where number="+s.getNumber(), null);
		c.moveToFirst();
		if(c.getInt(0) <= 0){
			database.execSQL("INSERT INTO stations VALUES(?, ?, ?, ?, ?, ?, ?)",
					new Object[]{s.getNumber(), s.getName(), 
					s.getLatitude(), s.getLongitude(), 
					s.getOpen(), s.getBonus(), s.getAdress()});
		}
		c.close();
	}
	
	public ArrayList<Station> getListOfStations(String search, int distance){
		String sql;
		if(!search.equals("")){
			sql = "select * from stations where name like '%"+search+"%'";
		}else {
			sql = "select * from stations";
		}
		
		ArrayList<Station> arrList = new ArrayList<Station>();
		
		Cursor c = database.rawQuery(sql, null);
		c.moveToFirst();
		do{
			Station s = new Station();
			//s.setNumber(c.getInt(0));
			// ou
			s.setNumber(c.getInt(c.getColumnIndex("number")));
			s.setName(c.getString(c.getColumnIndex("name")));
			s.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
			s.setLongitude(c.getDouble(c.getColumnIndex("longitude")));
			s.setAdress(c.getString(c.getColumnIndex("adress")));
			s.setOpen(c.getInt(c.getColumnIndex("open")));
			s.setBonus(c.getInt(c.getColumnIndex("bonus")));

			arrList.add(s);
			
		}while(c.moveToNext());
		c.close();
		return arrList;
	}
	
}
