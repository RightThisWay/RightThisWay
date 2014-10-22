package com.cs465.rightthisway;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;


public class MainActivity extends ActionBarActivity {
	
	/**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    /**
     * Siebel center location is picked for simulation
     */
    private final LatLng LOCATION_SIEBEL = new LatLng(40.1138215,-88.2249023);
    private final LatLng LOCATION_ARC = new LatLng(40.101098,-88.236465);
    private final LatLng LOCATION_GOODRICH = new LatLng(40.072756,-88.25205);
    private final LatLng LOCATION_JUPITER = new LatLng(40.117862,-88.241487);
    
    private View goBtn; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        goBtn = findViewById(R.id.goButton);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
    	/**
    	 * Zoom range 0 - 21, 16 works decently. 
    	 */
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_SIEBEL, 18);
        /**
         * Update the map with the camera update
         */
        mMap.animateCamera(update);
        /**
         * Add a marker for siebel center
         */
    	mMap.addMarker(new MarkerOptions().position(LOCATION_SIEBEL).title("Siebel Center").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    	
    	//goButton.setVisibility(View.INVISIBLE);

    }
    /**
     * on DestButton Clicked
     * @param v
     * Event when DestButton is clicked
     */
    public void onDestButton1_Clicked(View v)
    {
    	/**
    	 * Zoom range 0 - 21 
    	 */
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_ARC, 18);
        /**
         * Update the map with the camera update
         */
        mMap.animateCamera(update);
        /**
         * Add a marker for Arc
         */
    	mMap.addMarker(new MarkerOptions().position(LOCATION_ARC).title("ARC"));
    	goBtn.setVisibility(View.VISIBLE);
    }
    
    public void onDestButton2_Clicked(View v)
    {
    	/**
    	 * Zoom range 0 - 21 
    	 */
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_GOODRICH, 18);
        /**
         * Update the map with the camera update
         */
        mMap.animateCamera(update);
        /**
         * Add a marker for Arc
         */
    	mMap.addMarker(new MarkerOptions().position(LOCATION_GOODRICH).title("Goodrich"));
    	goBtn.setVisibility(View.VISIBLE);
    }
    
    public void onDestButton3_Clicked(View v)
    {
    	/**
    	 * Zoom range 0 - 21 
    	 */
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_JUPITER, 18);
        /**
         * Update the map with the camera update
         */
        mMap.animateCamera(update);
        /**
         * Add a marker for Arc
         */
    	mMap.addMarker(new MarkerOptions().position(LOCATION_JUPITER).title("Jupiter's"));
    	goBtn.setVisibility(View.VISIBLE);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
}
