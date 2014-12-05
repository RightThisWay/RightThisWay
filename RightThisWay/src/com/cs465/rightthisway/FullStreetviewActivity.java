package com.cs465.rightthisway;

import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class FullStreetviewActivity extends ActionBarActivity {
	
	/**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private StreetViewPanorama streetview;
    LatLng finalDestination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_streetview);
        
        finalDestination = getIntent().getExtras().getParcelable("destination");
        
        setUpStreetViewPanoramaIfNeeded(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

	private void setUpStreetViewPanoramaIfNeeded(Bundle savedInstanceState) {
		if (streetview == null) {
			streetview = ((SupportStreetViewPanoramaFragment)
					getSupportFragmentManager().findFragmentById(R.id.streetviewfragment))
					.getStreetViewPanorama();
			if (streetview != null) {
				if (savedInstanceState == null) {
					streetview.setPosition(finalDestination);
				}
			}
		}
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
        
        case R.id.action_help:
            DialogFragment helpDialog = new HelpDialog();
            helpDialog.show(getSupportFragmentManager(), "help");
        	return true;

        // Respond to the action bar's Up/Home button
        case R.id.home:
            finish();
            return true;
        }
    	
        return super.onOptionsItemSelected(item);
    }
    
    public void onBackButtonClicked(View v)
    {
    	finish();
    }
}
