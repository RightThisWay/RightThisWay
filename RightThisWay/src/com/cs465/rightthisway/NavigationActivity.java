package com.cs465.rightthisway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Address;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class NavigationActivity extends FragmentActivity {

	private GoogleMap map;
	private SupportMapFragment fragment;
	private LatLngBounds latlngBounds;
	private Polyline newPolyline;
	private int width, height;
	
	private Address destination;
	private Address startLocation;
	
	private StreetviewMarkerClick streetviewMarkerListener;
	
	private ArrayList<Marker> turnMarkers;
	
	private CheckBox selectAllCheckbox;
	public DirectionsData directionsData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		
		selectAllCheckbox = (CheckBox) findViewById(R.id.checkBoxSelectAll);
		selectAllCheckbox.setBackgroundColor(0x80FFFFFF);
		
		turnMarkers = new ArrayList<Marker>();
		
	    fragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		map = fragment.getMap(); 	

		streetviewMarkerListener = new StreetviewMarkerClick(getApplicationContext());
		map.setOnMarkerClickListener(streetviewMarkerListener);
		
		getScreenDimensions();
		
		//Retrieve the start and end locations from the calling activity
		startLocation = getIntent().getExtras().getParcelable("startLocation");
		destination = getIntent().getExtras().getParcelable("destination");
		
		findDirections( startLocation.getLatitude(), startLocation.getLongitude(), destination.getLatitude(), destination.getLongitude(), GMapV2Direction.MODE_DRIVING );
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
    	latlngBounds = createLatLngBoundsObject(startLocation, destination);
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 150));

	}
	
	public void onSelectAllClicked(View view)
	{
		Toast toast;
		boolean checked = selectAllCheckbox.isChecked();
		    
		    // Check which checkbox was clicked
		    switch(view.getId()) {
		        case R.id.checkBoxSelectAll:
		            if (checked){
		            	for(Marker marker : turnMarkers){
		            		marker.setTitle("selected");
		            		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.select));
		            	}
		        		toast = Toast.makeText(getApplicationContext(), "All Streetviews Enabled", Toast.LENGTH_SHORT);
		        		toast.show();
		            }
		            else
		            {
		            	for(Marker marker : turnMarkers){
		            		marker.setTitle("unselected");
		            		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.unselect));
		            	}
		            	toast = Toast.makeText(getApplicationContext(), "All Streetviews Disabled", Toast.LENGTH_SHORT);
		        		toast.show();
		            }

		            break;
		    }

	}

	/**
	 * Call back function used to plot the route when the direction results are returned
	 * @param directionPoints
	 */
	public void drawRouteLines(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(20).color(0xff0099CC);

		for(int i = 0 ; i < directionPoints.size() ; i++) 
		{          
			rectLine.add(directionPoints.get(i));
		}
		if (newPolyline != null)
		{
			newPolyline.remove();
		}
		newPolyline = map.addPolyline(rectLine);
		latlngBounds = createLatLngBoundsObject(startLocation, destination);
		
		map.addMarker(new MarkerOptions().position(new LatLng(startLocation.getLatitude(), startLocation.getLongitude())).title(startLocation.getFeatureName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		map.addMarker(new MarkerOptions().position(new LatLng(destination.getLatitude(), destination.getLongitude())).title(destination.getFeatureName()));
		
	    map.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, 150));
	}
	
	public void drawTurns(ArrayList<LatLng> turns) {
		Marker newMarker;
		for(LatLng turn : turns){
			newMarker = map.addMarker(new MarkerOptions().position(turn).anchor(0.5f, 0.5f).title("unselected").icon(BitmapDescriptorFactory.fromResource(R.drawable.unselect)));
			turnMarkers.add(newMarker);
		}
	}
	
	/**
	 * Sets the screen dimensions which are used in the initial map zooming due to Android wanting this info if you zoom too early
	 * (before the map layout is generated)
	 */
	private void getScreenDimensions()
	{
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth(); 
		height = display.getHeight(); 
	}
	
	private LatLngBounds createLatLngBoundsObject(Address address1, Address address2)
	{
		LatLng firstLocation = new LatLng(address1.getLatitude(), address1.getLongitude());
		LatLng secondLocation = new LatLng(address2.getLatitude(), address2.getLongitude());
		if (firstLocation != null && secondLocation != null)
		{
			LatLngBounds.Builder builder = new LatLngBounds.Builder();    
			builder.include(firstLocation).include(secondLocation);
			
			return builder.build();
		}
		return null;
	}
	
	
	/**
	 * Starts a side task to retrieve the directions info from google 
	 * @param fromPositionDoubleLat
	 * @param fromPositionDoubleLong
	 * @param toPositionDoubleLat
	 * @param toPositionDoubleLong
	 * @param mode
	 */
	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
		
		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);	
	}
	/**
	 * 
	 * @param v
	 */
	public void onStartClicked(View v)
    {
		
     	Intent startRoutingSwitch = new Intent(NavigationActivity.this, StartRoutingActivity.class);
    	//startRoutingSwitch.putExtra("directionsDataTurns", directionsData.turns);
    	startRoutingSwitch.putParcelableArrayListExtra("directionsDataRoute", directionsData.routeLines);
    	
    	//startRoutingSwitch.putExtra("markers", turnMarkers);
    	//navigationSwitch.putExtra("startLocation", currentLocation);
    	startActivity(startRoutingSwitch);
    }


}
