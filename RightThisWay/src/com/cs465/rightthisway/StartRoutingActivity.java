package com.cs465.rightthisway;

import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class StartRoutingActivity extends ActionBarActivity {
	private GoogleMap map;
	private SupportMapFragment mapFragment;
	private Polyline routeLineOnMap;
	private ArrayList<LatLng> routeLines;
	private Marker currentLocMarker;
	private DirectionsData directionsData;
	
    // George St, Sydney
    private static final LatLng EXAMPLE_LOCATION = new LatLng(-33.87365, 151.20689);

    private StreetViewPanorama streetview;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_routing);
		mapFragment = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map));
		map = mapFragment.getMap();
		routeLines = new ArrayList<LatLng>();
		Intent receivedIntent = getIntent();
		routeLines = receivedIntent
				.getParcelableArrayListExtra("directionsDataRoute");
		// Markers is not implemented parcelable.
		// turnMarkers = receivedIntent.getParcelableArrayListExtra("markers");
		currentLocMarker = map.addMarker(new MarkerOptions()
				.position(routeLines.get(0)));
		
		directionsData = new DirectionsData();
		directionsData = receivedIntent.getParcelableExtra("directionsData");

        setUpStreetViewPanoramaIfNeeded(savedInstanceState);
		
		// Draw the route
		drawRouteLines(routeLines);
		// Simulate travelling
		mockTravelling();
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
	
	/**
	 * Iterate through all route points to simulate driving along the route.
	 * 
	 */
	private void mockTravelling() {
		new Thread(new Runnable() {
			int i;

			@Override
			public void run() {
				
				for (i = 0; i < routeLines.size() - 2; i++) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// do your Ui task here
							// Set zoom parameter
							int zoomExtent = 20;

							// Set bearing degree (not completely correct)
							Location currentLocation = new Location(
									"current location");
							currentLocation.setLatitude(routeLines.get(i).latitude);
							currentLocation.setLongitude(routeLines.get(i).longitude);

							// USE i + 2 produces better result. Two consecutive
							// points produces weird bearing angle.
							Location nextLocation = new Location(
									"next location");
							nextLocation.setLatitude(routeLines.get(i + 2).latitude);
							nextLocation.setLongitude(routeLines.get(i + 2).longitude);

							float bearingDegree = currentLocation
									.bearingTo(nextLocation);

							// Set tilt degree
							int tiltDegree = 70;

							// Construct a CameraPosition focusing on Mountain
							// View and animate the camera to that position.
							CameraPosition cameraPosition = new CameraPosition.Builder()
									.target(routeLines.get(i)) // Sets the
																// center of the
																// map to
																// Mountain View
									.zoom(zoomExtent) // Sets the zoom
									.bearing(bearingDegree) // Sets the
															// orientation of
															// the camera to
															// east
									.tilt(tiltDegree) // Sets the tilt of the
														// camera to 30 degrees
									.build(); // Creates a CameraPosition from
												// the builder
							map.animateCamera(CameraUpdateFactory
									.newCameraPosition(cameraPosition));
							currentLocMarker.setPosition(routeLines.get(i));
						
							float[] distanceToTurn = new float[1];

							for(Turn turn : directionsData.turns)
							{
								Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), turn.latlng.latitude, turn.latlng.longitude, distanceToTurn);

								if(distanceToTurn[0] < 50f)
								{
									streetview.setPosition(turn.latlng);
//									StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
//									    .zoom(streetview.getPanoramaCamera().zoom)
//									    .tilt(streetview.getPanoramaCamera().tilt)
//									    .bearing(bearingDegree)
//									    .build();
//									streetview.animateTo(camera, 0);
									if(turn.streetViewEnabled)
									{
										displayStreetView(true);
									}
									break;
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
