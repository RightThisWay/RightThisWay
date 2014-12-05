package com.cs465.rightthisway;

import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;


public class MainActivity extends ActionBarActivity implements OnItemClickListener {
	
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
    
    private Address destination;
    private Address currentLocation;
    
// These commented lines will be used for real place search functionality (not in prototype) 
//    private static final String LOG_TAG = "ExampleApp";
//    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
//    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
//    private static final String OUT_JSON = "/json";
//    private static final String API_KEY = "AIzaSyCbhjljcFBxSCz73jNen9AhlLu9rlB9gFE";

    private View goBtn; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        goBtn = findViewById(R.id.goButton);
        
        currentLocation = new Address(Locale.getDefault());
        destination = new Address(Locale.getDefault());
        
        //Siebel Center is the default location
        currentLocation.setLatitude(LOCATION_SIEBEL.latitude);
        currentLocation.setLongitude(LOCATION_SIEBEL.longitude);
        currentLocation.setFeatureName("Siebel Center");

        //Setup search autocompletion
        AutoCompleteTextView findDestinationTextView = (AutoCompleteTextView) findViewById(R.id.findDestination);
        findDestinationTextView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.search_suggestions));
        findDestinationTextView.setOnItemClickListener(this);
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

	private void gotoLocation(LatLng location, String name) 
	{
		/**
    	 * Zoom range 0 - 21 
    	 */
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 16);
        /**
         * Update the map with the camera update
         */
        mMap.animateCamera(update);
        /**
         * Add a marker for Arc
         */
    	mMap.addMarker(new MarkerOptions().position(location).title(name));
    	goBtn.setVisibility(View.VISIBLE);
    	
    	destination.setLatitude(location.latitude);
    	destination.setLongitude(location.longitude);
    	destination.setFeatureName(name);
	}
    
    /**
     * onGoClicked
     * Pass the ends of the route on to the routing screen
     */
    public void onGoClicked(View v)
    {
     	Intent navigationSwitch = new Intent(MainActivity.this, NavigationActivity.class);
    	navigationSwitch.putExtra("destination", destination);
    	navigationSwitch.putExtra("startLocation", currentLocation);
    	startActivity(navigationSwitch);
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

        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case R.id.action_settings:
        	return true;
        
        case android.R.id.home:
        	NavUtils.navigateUpFromSameTask(this);
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function generates the list of items for the autocomplete
     * destination search
     * @param input Partial search term
     * @return Suggested places
     */
    private ArrayList<String> autocomplete(String input) {
// The commented code here is meant to implement place search suggestions, but is disabled for the prototype
//        ArrayList<String> resultList = null;
//
//        HttpURLConnection conn = null;
//        StringBuilder jsonResults = new StringBuilder();
//        try {
//            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
//            sb.append("?key=" + API_KEY);
//            sb.append("&components=country:us");
//            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
//
//            URL url = new URL(sb.toString());
//            conn = (HttpURLConnection) url.openConnection();
//            InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//            // Load the results into a StringBuilder
//            int read;
//            char[] buff = new char[1024];
//            while ((read = in.read(buff)) != -1) {
//                jsonResults.append(buff, 0, read);
//            }
//        } catch (MalformedURLException e) {
//            Log.e(LOG_TAG, "Error processing Places API URL", e);
//            return resultList;
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error connecting to Places API", e);
//            return resultList;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        try {
//            // Create a JSON object hierarchy from the results
//            JSONObject jsonObj = new JSONObject(jsonResults.toString());
//            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
//
//            // Extract the Place descriptions from the results
//            resultList = new ArrayList<String>(predsJsonArray.length());
//            for (int i = 0; i < predsJsonArray.length(); i++) {
//                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
//            }
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, "Cannot process JSON results", e);
//        }
    	
    	ArrayList<String>resultList = new ArrayList<String>();
    	
    	resultList.add("ARC");
    	resultList.add("Goodrich");
    	resultList.add("Jupiter's");
    	

        return resultList;
    }
    
    /**
     * Adapter to link the find destination search input
     * to the autocomplete backend  
     */
    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }
    
    /**
     * Captures clicks/selections in the autocomplete destination list
     * and chooses that as the destination for travel.  Used by the 
     * OnItemClickListener interface that this class implements
     */
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String destination = (String) adapterView.getItemAtPosition(position);
        if(destination.equals("ARC")) {
        	gotoLocation(LOCATION_ARC, "ARC");
        }
        else if(destination.equals("Goodrich")) {
        	gotoLocation(LOCATION_GOODRICH, "Goodrich");
        }
        else if(destination.equals("Jupiter's")) {
        	gotoLocation(LOCATION_JUPITER, "Jupiter's");
        }
        
    }

}

