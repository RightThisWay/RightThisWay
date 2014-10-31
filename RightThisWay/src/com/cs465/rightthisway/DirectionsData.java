package com.cs465.rightthisway;
import java.util.ArrayList;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;


public class DirectionsData implements Parcelable{
	public ArrayList<LatLng> turns;
	public ArrayList<LatLng> routeLines;

	public DirectionsData()
	{
		turns = new ArrayList<LatLng>();
		routeLines = new ArrayList<LatLng>();
	}
	
	public static final Parcelable.Creator<DirectionsData> CREATOR
	= new Parcelable.Creator<DirectionsData>() {
		public DirectionsData createFromParcel(Parcel in) {
			return new DirectionsData(in);
		}

		public DirectionsData[] newArray(int size) {
			return new DirectionsData[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeTypedList(turns);
		//out.writeTypedList(routeLines);
	}
	
	private DirectionsData(Parcel in) {
		in.readTypedList(turns, LatLng.CREATOR);
		//in.readTypedList(routeLines, LatLng.CREATOR);

	}

}
