package com.cs465.rightthisway;

import java.util.ArrayList;
import java.util.Map;
import org.w3c.dom.Document;
import com.google.android.gms.maps.model.LatLng;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, DirectionsData>
{
    public static final String USER_CURRENT_LAT = "user_current_lat";
    public static final String USER_CURRENT_LONG = "user_current_long";
    public static final String DESTINATION_LAT = "destination_lat";
    public static final String DESTINATION_LONG = "destination_long";
    public static final String DIRECTIONS_MODE = "directions_mode";
    private NavigationActivity activity;
    private Exception exception;
    private ProgressDialog progressDialog;
 
    public GetDirectionsAsyncTask(NavigationActivity activity)
    {
        super();
        this.activity = activity;
    }
 
    public void onPreExecute()
    {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Calculating directions");
        progressDialog.show();
    }
 
    @Override
    public void onPostExecute(DirectionsData result)
    {
        progressDialog.dismiss();
        if (exception == null)
        {
            activity.drawRouteLines(result.routeLines);
            activity.drawTurns(result.turns);
            activity.directionsData = result;
        }
        else
        {
            processException();
        }
    }
 
    @Override
    protected DirectionsData doInBackground(Map<String, String>... params)
    {
        Map<String, String> paramMap = params[0];
        try
        {
        	DirectionsData directionsData = new DirectionsData();
            LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
            LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
            GMapV2Direction md = new GMapV2Direction();
            Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
            ArrayList<LatLng> directionPoints = md.getDirection(doc);
            
            ArrayList<Turn> turns = md.getTurns(doc);
            
            directionsData.routeLines = directionPoints;
            directionsData.turns = turns;
            
            return directionsData;
        }
        catch (Exception e)
        {
            exception = e;
            return null;
        }
    }
 
    private void processException()
    {
        Toast.makeText(activity, "Error retriving data", 3000).show();
    }
}