package com.idevmob.listview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ArrayList<Country> countries = new ArrayList<Country>();
        countries.add(new Country("France", "FR"));
        countries.add(new Country("Belgium", "BE"));
        countries.add(new Country("Spain", "ES"));
        countries.add(new Country("Germany", "DE"));
        
        
        final ListView listCountry = (ListView) findViewById(R.id.list_country);
        CountryAdapter adapter = new CountryAdapter(getBaseContext(), this, countries);
        listCountry.setAdapter(adapter);
    }
}