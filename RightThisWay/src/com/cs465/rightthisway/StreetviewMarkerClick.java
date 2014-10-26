package com.cs465.rightthisway;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

public class StreetviewMarkerClick implements OnMarkerClickListener 
{
	private Context context;
	
	public StreetviewMarkerClick(Context context)
	{
		this.context = context;
	}
	
	private void displayStatus(String message)
	{
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if(marker.getTitle().equals("unselected")){
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			marker.setTitle("selected");
			displayStatus("Streetview Enabled");
			return true;
		}
		else if(marker.getTitle().equals("selected")){
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
			marker.setTitle("unselected");
			displayStatus("Streetview Disabled");
			return true;
		}
		return false;
	}

}
