package com.idevmob.tp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class StationsParser extends DefaultHandler {
	
	ArrayList<Station> arrList;
	static String url = "http://www.levelo-mpm.fr/service/carto";
	XMLReader xr;
	File root;
	Context context;
	SharedPreferences myPrefs;
	StationsDB database;
	boolean isLocal;
	
	private void loadStationsFromSDCard(){
		File baseXML = new File(root, "VELOSUD/stations.xml");
		try {
			FileInputStream fip = new FileInputStream(baseXML);
			xr.parse(new InputSource(fip));
			fip.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveStationsToSDCard(InputSource is){
		File folderOut = new File(root, "VELOSUD");
		folderOut.mkdir();
		
		try {
			FileOutputStream fos = new FileOutputStream(folderOut.getPath()+"/stations.xml");
			byte[] buffer = new byte[1024];
			int byteread;
			while((byteread = is.getByteStream().read(buffer)) != -1){
				fos.write(buffer, 0, byteread);
			}
			fos.flush();
			fos.close();
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private boolean isUpToDateStations(){
		Long lastUpdate = myPrefs.getLong("lastupdate", 0);
		Date now = new Date();
		if(now.getTime() - lastUpdate > (86400000)){
			return false;
		}else{
			return true;
		}
	}
	

	
	public StationsParser(Context context) throws SAXException, ParserConfigurationException, IOException{
		this.context = context;
		myPrefs = context.getSharedPreferences("velostations", Context.MODE_PRIVATE);
		database = new StationsDB();
		isLocal = true;
		
	    root = Environment.getExternalStorageDirectory();
	    File baseXML = new File(root, "VELOSUD/stations.xml");
	    
	    if(!baseXML.isFile() || !isUpToDateStations()){
			URL urlfeed = new URL(url);
	    	InputSource is = new InputSource(urlfeed.openConnection().getInputStream());
	    	saveStationsToSDCard(is);
	    	isLocal = false;
	    	// sharedPreferences en mode ecriture
	    	SharedPreferences.Editor edit = myPrefs.edit();
	    	Date now = new Date();
	    	edit.putLong("lastupdate", now.getTime());
	    	edit.commit();
	    }
		
	    
	    if(isLocal){
	    	Log.i("TP4", "load from database");
	    	arrList = database.getListOfStations("", 0);
	    	database.closeDB();
	    }else{
	    	Log.i("TP4", "load from xml SDCARD");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			xr.setContentHandler(this);
			
			loadStationsFromSDCard();
	    }

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
		database.closeDB();
	}


	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		arrList = new ArrayList<Station>();
		
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		if(localName.equals("marker")){
			Station station = new Station();
			station.setName(attributes.getValue("name"));
			station.setLatitude(Double.parseDouble(attributes.getValue("lat")));
			station.setLongitude(Double.parseDouble(attributes.getValue("lng")));
			station.setNumber(Integer.parseInt(attributes.getValue("number")));
			
			arrList.add(station);
			if(!isLocal) database.insertStation(station);
		}
		
	}
	
	
	
	
	

}
