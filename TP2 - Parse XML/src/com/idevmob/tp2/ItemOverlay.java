package com.idevmob.tp2;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItemOverlay extends ItemizedOverlay<OverlayItem> {

	Context context;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public ItemOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;

	}
	
	
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		if(!item.getSnippet().equals("")){
			Intent intent = new Intent(context, StationDetail.class);
			intent.putExtra("number", Integer.parseInt(item.getSnippet()));
			context.startActivity(intent);
		}
		
		return true;
	}



	public void addOverlay(OverlayItem overlay){
		mOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
