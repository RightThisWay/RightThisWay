package com.cs465.rightthisway;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Turn implements Parcelable {
	public boolean streetViewEnabled;
	public LatLng latlng;
	
	public Turn(double latitude, double longitude)
	{
		latlng = new LatLng(latitude, longitude);
	}
	
	public static final Parcelable.Creator<Turn> CREATOR
	= new Parcelable.Creator<Turn>() {
		public Turn createFromParcel(Parcel in) {
			return new Turn(in);
		}

		public Turn[] newArray(int size) {
			return new Turn[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeByte((byte) (streetViewEnabled ? 1 : 0));
		out.writeParcelable(latlng, flags);
	}
	
	private Turn(Parcel in) {
		streetViewEnabled = in.readByte() == 1;
		latlng = in.readParcelable(LatLng.class.getClassLoader());
	}
}
