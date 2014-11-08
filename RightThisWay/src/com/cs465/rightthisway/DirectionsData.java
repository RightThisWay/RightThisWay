package com.cs465.rightthisway;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;


public class DirectionsData implements Parcelable{
	public ArrayList<LatLng> routeLines;
	public ArrayList<Turn> turns;

	public DirectionsData()
	{
		turns = new ArrayList<Turn>();
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
		out.writeTypedList(routeLines);
	}
	
	private DirectionsData(Parcel in) {
		turns = new ArrayList<Turn>();
		routeLines = new ArrayList<LatLng>();
		
		in.readTypedList(turns, Turn.CREATOR);
		in.readTypedList(routeLines, LatLng.CREATOR);

	}

}
