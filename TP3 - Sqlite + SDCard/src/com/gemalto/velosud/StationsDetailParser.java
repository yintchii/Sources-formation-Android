package com.gemalto.velosud;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class StationsDetailParser extends DefaultHandler {

	int available;
	int free;
	int total;
	int ticket;
	
	StringBuffer current;
	boolean isAvailable;
	boolean isFree;
	boolean isTotal;
	boolean isTicket;
	
	public StationsDetailParser(int IDStation){
		try {
			
		URL carto = new	 URL("http://www.levelo-mpm.fr/service/stationdetails/"+IDStation);
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();
		xr.setContentHandler(this);
		xr.parse(new InputSource(carto.openStream()));
		
		JSONObject jo = new JSONObject();
		jo.getJSONObject("site").getDouble("lat");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public int getAvailable() {
		return available;
	}

	public void setAvailable(int available) {
		this.available = available;
	}

	public int getFree() {
		return free;
	}
	public void setFree(int free) {
		this.free = free;
	}
	
	public int getTotal() {
		return total;
	}




	public void setTotal(int total) {
		this.total = total;
	}




	public int getTicket() {
		return ticket;
	}




	public void setTicket(int ticket) {
		this.ticket = ticket;
	}




	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		if(localName.equals("available")){
			isAvailable = true;
			current = new StringBuffer("");
		}
		
		if(localName.equals("free")){
			isFree = true;
			current = new StringBuffer("");
		}
		
		if(localName.equals("total")){
			isTotal = true;
			current = new StringBuffer("");
		}
		
		if(localName.equals("ticket")){
			isTicket = true;
			current = new StringBuffer("");
		}
		
	}
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if(isAvailable || isTotal || isFree || isTicket){
		current.append(new String(ch, start, length));
		}
		
	}


	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		if(localName.equals("available")){
			isAvailable = false;
			available = Integer.parseInt(current.toString());
		}
		
		if(localName.equals("free")){
			isFree = false;
			free = Integer.parseInt(current.toString());
		}
		
		if(localName.equals("total")){
			isTotal = false;
			total = Integer.parseInt(current.toString());
		}
		
		if(localName.equals("ticket")){
			isTicket = true;
			ticket = Integer.parseInt(current.toString());
		}
		
	}

	
	
	
}
