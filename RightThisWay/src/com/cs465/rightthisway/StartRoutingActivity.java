package com.cs465.rightthisway;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class StartRoutingActivity extends ActionBarActivity {
	private GoogleMap map;
	private SupportMapFragment fragment;
	private SupportStreetViewPanoramaFragment streetviewFragment;
	private LatLngBounds latlngBounds;
	private Polyline newPolyline;
	private ArrayList<LatLng> routeLines;
	private ArrayList<Marker> turnMarkers;
	private DirectionsData directionsData;
	private Marker currentLocMarker;
	
    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private StreetViewPanorama mSvp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_routing);
		fragment = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map));
		map = fragment.getMap();
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
        if (mSvp == null) {
            mSvp = ((SupportStreetViewPanoramaFragment)
                getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama2))
                    .getStreetViewPanorama();
            if (mSvp != null) {
                if (savedInstanceState == null) {
                    mSvp.setPosition(SYDNEY);
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
		PolylineOptions rectLine = new PolylineOptions().width(20).color(
				0xff0099CC);

		for (int i = 0; i < directionPoints.size(); i++) {
			rectLine.add(directionPoints.get(i));
		}
		if (newPolyline != null) {
			newPolyline.remove();
		}
		newPolyline = map.addPolyline(rectLine);
		latlngBounds = createLatLngBoundsObject(routeLines.get(0),
				routeLines.get(routeLines.size() - 1));

		map.addMarker(new MarkerOptions().position(routeLines.get(0)).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		map.addMarker(new MarkerOptions().position(routeLines.get(routeLines
				.size() - 1)));
		// The following line introduces crash
		// map.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds,
		// 150));
	}

	private LatLngBounds createLatLngBoundsObject(LatLng firstLocation,
			LatLng secondLocation) {
		if (firstLocation != null && secondLocation != null) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(firstLocation).include(secondLocation);

			return builder.build();
		}
		return null;
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
	    boolean on = toggleButton.isChecked();
	    
	    if (on) {
	    	streetViewDisplay(true);
	    } else {
	    	streetViewDisplay(false);
	    }
	}

	private void streetViewDisplay(boolean streetViewEnabled) {
		View view = findViewById(R.id.streetviewlayout);
		
		int halfLayoutHeight =  findViewById(R.id.RoutingLinearLayout).getHeight()/2;
		
		if(streetViewEnabled)
		{
			view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, halfLayoutHeight));
		}
		else
		{
			view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0));
		}
		
	}
}
