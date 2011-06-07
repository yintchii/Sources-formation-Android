package com.gemalto.velosud;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class StationsParser extends DefaultHandler {
	
	ArrayList<Station> arrList;
	static String url = "http://www.levelo-mpm.fr/service/carto";
	XMLReader xr;
	
	
	public StationsParser() throws SAXException, ParserConfigurationException, IOException{
		URL urlfeed = new URL(url);
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		xr = sp.getXMLReader();
		xr.setContentHandler(this);
		xr.parse(new InputSource(urlfeed.openConnection().getInputStream()));

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
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
		}
		
	}
	
	
	
	
	

}
