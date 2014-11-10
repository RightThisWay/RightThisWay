package com.cs465.rightthisway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class StartRoutingActivity extends ActionBarActivity {
	private GoogleMap map;
	private SupportMapFragment mapFragment;
	private Polyline routeLineOnMap;
	private ArrayList<LatLng> routeLines;
	private Marker currentLocMarker;
	private DirectionsData directionsData;
	private TextView streetNameText;
	private TextView remainingTimeText;
	private TextView remainingDistanceText;
	private ImageView turnImageView;
    private StreetViewPanorama streetview;
	private ArrayList<String> fakeStreetNames = new ArrayList<String>();;
	private double[] remainingTime;
	private double[] remainingDistance;
	private ArrayList<Integer> assignedTurns;
	float previousDistanceToTurn = Float.MAX_VALUE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_routing);

		//Initialize class variables
		mapFragment = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map));
		map = mapFragment.getMap();
		map.getUiSettings().setCompassEnabled(false);
		
		streetNameText = ((TextView) findViewById(R.id.streetNameTextView)); 
		remainingTimeText = ((TextView) findViewById(R.id.remainingTimeTextView));
		remainingDistanceText = ((TextView) findViewById(R.id.remainingDistanceTextView1));
		turnImageView = ((ImageView) findViewById(R.id.imageView1));
		routeLines = new ArrayList<LatLng>();
		Intent receivedIntent = getIntent();
	
		remainingTime = new double[200];
		remainingDistance = new double[200];

		directionsData = new DirectionsData();
		directionsData = receivedIntent.getParcelableExtra("directionsData");
		initFakeStreetNames();
	    setUpStreetViewPanoramaIfNeeded(savedInstanceState);
	    
		//Add starting marker
		routeLines = receivedIntent
				.getParcelableArrayListExtra("directionsDataRoute");
		currentLocMarker = map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.yahoutlinedsmall))
				.flat(true)
				.position(routeLines.get(0)));
		
	
		assignTurnsToRoute();
		
		// Draw the route
        drawRouteLines(routeLines);
        deleteExcessPoints();
        
        calRemainingTimeDistance();
		
		//Simulate traveling
		mockTravelling();
	}
	
	private void assignTurnsToRoute()
	{
		int previousIndex = 0;
		assignedTurns = new ArrayList<Integer>(Collections.nCopies(routeLines.size(), -1));
		
		for(int i = 0; i < directionsData.turns.size(); i++){
			int turnIndex = routeLines.indexOf(directionsData.turns.get(i).latlng);

			for(int c = previousIndex; c< turnIndex; c++){
				assignedTurns.set(c, i);
			}
			
			previousIndex = turnIndex;
		}
		
	}
	
	private void calRemainingTimeDistance()
	{
		for (int i = routeLines.size() - 1; i > 0; i--)
		{
			Location currentLocation = new Location("current location");
			currentLocation.setLatitude(routeLines.get(i).latitude);
			currentLocation.setLongitude(routeLines.get(i).longitude);
			Location prevLocation = new Location("prev location");
			prevLocation.setLatitude(routeLines.get(i-1).latitude);
			prevLocation.setLongitude(routeLines.get(i-1).longitude);
			float[] distanceToPrevTurn = new float[1];
			Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), prevLocation.getLatitude(), prevLocation.getLongitude(), distanceToPrevTurn);
			if(i == routeLines.size() - 1)
			{
				remainingDistance[i] = distanceToPrevTurn[0];
			} else {
				remainingDistance[i] = distanceToPrevTurn[0] + remainingDistance[i + 1];
			}
			remainingTime[i] = remainingDistance[i] / 10; //Assume 10 m / s, 36 km/h
		}
	}
	
	private void initFakeStreetNames()
	{
		fakeStreetNames.add("Lincoln Ave");
		fakeStreetNames.add("Green St");
		fakeStreetNames.add("Linwood Ave");
		fakeStreetNames.add("University Ave");
		fakeStreetNames.add("Main St");
		
	}
	
	private void setUpStreetViewPanoramaIfNeeded(Bundle savedInstanceState) {
		if (streetview == null) {
			streetview = ((SupportStreetViewPanoramaFragment)
					getSupportFragmentManager().findFragmentById(R.id.streetviewfragment))
					.getStreetViewPanorama();
			if (streetview != null) {
				if (savedInstanceState == null) {
					streetview.setPosition(directionsData.turns.get(0).latlng);
				}
			}
		}
    }
	
	private void deleteExcessPoints()
	{
		for (int i = routeLines.size() - 1; i > 0; i--)
		{
			Location currentLocation = new Location("current location");
			currentLocation.setLatitude(routeLines.get(i).latitude);
			currentLocation.setLongitude(routeLines.get(i).longitude);
			Location prevLocation = new Location("prev location");
			prevLocation.setLatitude(routeLines.get(i-1).latitude);
			prevLocation.setLongitude(routeLines.get(i-1).longitude);
			float[] distanceToPrevTurn = new float[1];
			Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), prevLocation.getLatitude(), prevLocation.getLongitude(), distanceToPrevTurn);
			if (distanceToPrevTurn[0] < 15f)
			{
				routeLines.remove(i);
				assignedTurns.remove(i);
			}
		}
	}
	
	/**
	 * Iterate through all route points to simulate driving along the route.
	 * 
	 */
	private void mockTravelling() {
		new Thread(new Runnable() {
			int i;

			@Override
			public void run() {
				
				for (i = 0; i < routeLines.size(); i++) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// do your Ui task here
							//Update remaining time and distance
							int tempTimeInt = (int)remainingTime[i];
							int min = tempTimeInt / 60;
							int second = tempTimeInt % 60;
							String tempTime = String.valueOf(min) + " min " + String.valueOf(second) + " s";
							remainingTimeText.setText(tempTime);
							
							int tempDistanceInt = (int)remainingDistance[i];
							String tempDistance = String.valueOf(tempDistanceInt) + " m";
							remainingDistanceText.setText(tempDistance);
							
							
							// Set zoom parameter
							int zoomExtent = 20;

							// Set bearing degree (not completely correct)
							Location currentLocation = new Location(
									"current location");
							currentLocation.setLatitude(routeLines.get(i).latitude);
							currentLocation.setLongitude(routeLines.get(i).longitude);

							// USE i + 2 produces better result. Two consecutive
							// points produces weird bearing angle.
							float bearingDegree;
							if(i < routeLines.size()-1)	{
								Location nextLocation = new Location(
										"next location");
								nextLocation.setLatitude(routeLines.get(i + 1).latitude);
								nextLocation.setLongitude(routeLines.get(i + 1).longitude);

								bearingDegree = currentLocation
										.bearingTo(nextLocation);
							}
							else {
								Location previousLocation = new Location(
										"next location");
								previousLocation.setLatitude(routeLines.get(i-1).latitude);
								previousLocation.setLongitude(routeLines.get(i-1).longitude);

								bearingDegree = previousLocation
										.bearingTo(currentLocation);

							}
								
							

							// Set tilt degree
							int tiltDegree = 70;

							// Construct a CameraPosition focusing on Mountain
							// View and animate the camera to that position.
							CameraPosition cameraPosition = new CameraPosition.Builder()
									.target(routeLines.get(i)) 
									.zoom(zoomExtent) // Sets the zoom
									.bearing(bearingDegree)
									.tilt(tiltDegree) 
									.build();
							map.animateCamera(CameraUpdateFactory
									.newCameraPosition(cameraPosition));
							currentLocMarker.setPosition(routeLines.get(i));
							currentLocMarker.setRotation(bearingDegree);

							float[] distanceToTurn = new float[1];
							float[] distanceTurnToStreetview = new float[1];
							turnImageView.setBackgroundResource(R.drawable.up);
							
							Turn nextTurn;
							LatLng finalDestination =routeLines.get(routeLines.size()-1);

							if(assignedTurns.get(i) == -1){
								nextTurn = new Turn(finalDestination.latitude, finalDestination.longitude);
								nextTurn.streetViewEnabled = true;
								Button fullscreenButton = (Button) findViewById(R.id.fullscreenButton);
								fullscreenButton.setVisibility(View.VISIBLE);
							}
							
							else{
								 nextTurn = directionsData.turns.get(assignedTurns.get(i));
							}
							
							Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), nextTurn.latlng.latitude, nextTurn.latlng.longitude, distanceToTurn);

							boolean turnNotDisplayedAlready;
							LatLng streetviewPosition = new LatLng(0,0);

							if(streetview.getLocation() == null)
							{
								turnNotDisplayedAlready = true;
							}
							else{
								streetviewPosition = streetview.getLocation().position;
								Location.distanceBetween(nextTurn.latlng.latitude, nextTurn.latlng.longitude, streetviewPosition.latitude, streetviewPosition.longitude, distanceTurnToStreetview);
								turnNotDisplayedAlready = distanceTurnToStreetview[0] > 5f;
							}

							if(turnNotDisplayedAlready){

								streetNameText.setText(fakeStreetNames.get(Math.abs(assignedTurns.get(i)%5)));

								streetview.setPosition(nextTurn.latlng);
								StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
								.zoom(streetview.getPanoramaCamera().zoom)
								.tilt(streetview.getPanoramaCamera().tilt)
								.bearing(bearingDegree)
								.build();
								streetview.animateTo(camera, 0);
							}
							
							if(distanceToTurn[0] < 75f)  //if we're near a turn
							{
								turnImageView.setBackgroundResource(R.drawable.left);
								
								if(nextTurn.streetViewEnabled)
								{
									displayStreetView(true);
								}

							}

						}
					});

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				


			}
		}).start();
	}

	public void drawRouteLines(ArrayList<LatLng> directionPoints) {
		PolylineOptions routeLine = new PolylineOptions().width(20).color(
				0xff0099CC);
		MarkerOptions routeStartMarker = new MarkerOptions().position(routeLines.get(0)).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		MarkerOptions routeEndMarker = new MarkerOptions().position(routeLines.get(routeLines
				.size() - 1));		
		
		for (LatLng newLinePoint: directionPoints) {
			routeLine.add(newLinePoint);
		}

		if (routeLineOnMap != null) {
			routeLineOnMap.remove();
		}
		
		routeLineOnMap = map.addPolyline(routeLine);
		map.addMarker(routeStartMarker);
		map.addMarker(routeEndMarker);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_routing, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onStreetviewToggleClicked(View view) {
	    // Is the toggle on?
		ToggleButton toggleButton = (ToggleButton) view;
	    boolean streetviewToggleOn = toggleButton.isChecked();
	    
	    if (streetviewToggleOn) {
	    	displayStreetView(true);
	    } 
	    else {
	    	displayStreetView(false);
	    }
	}
	
	public void onFullscreenButtonClicked(View view){
     	Intent navigationSwitch = new Intent(StartRoutingActivity.this, FullStreetviewActivity.class);
    	navigationSwitch.putExtra("destination", routeLines.get(routeLines.size()-1));
    	startActivity(navigationSwitch);
	
	}

	private void displayStreetView(boolean streetViewEnabled) {
		View streetviewView = findViewById(R.id.streetviewlayout);
		LinearLayout.LayoutParams newStreetviewSize = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
		int newHeight =  findViewById(R.id.RoutingLinearLayout).getHeight()/2;
		
		if(streetViewEnabled) {
			newStreetviewSize.height = newHeight;
			((ToggleButton)findViewById(R.id.streetviewToggleButton)).setChecked(true);
		}
		else {
			newStreetviewSize.height = 0;
			((ToggleButton)findViewById(R.id.streetviewToggleButton)).setChecked(false);
		}

		streetviewView.setLayoutParams(newStreetviewSize);
	}
}
