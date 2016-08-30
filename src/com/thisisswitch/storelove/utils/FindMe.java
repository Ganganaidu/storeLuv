package com.thisisswitch.storelove.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class FindMe {

	public static interface FindMeListener {
		public void foundLocation(String provider, Location location);
		public void couldntFindLocation();
	}

	public static FindMe please(Context context, int timeout, boolean getLastKnownLocation, FindMeListener listener) {
		FindMe findMe = new FindMe(context, timeout, getLastKnownLocation, listener);
		findMe.please();
		return findMe;
	}

	private LocationManager locationManager;
	private FindMeListener listener;
	private int timeoutMS;
	private boolean getLastKnownLocation;

	private Timer timer;

	public FindMe(Context context, int timeoutMS, boolean getLastKnownLocation, FindMeListener listener) {
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.listener = listener;
		this.timeoutMS = timeoutMS;
		this.getLastKnownLocation = getLastKnownLocation;
	}

	public void please() {
		if (timer == null) {
			if (getLastKnownLocation) {
				Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (loc != null) {
					listener.foundLocation(LocationManager.GPS_PROVIDER, loc);
					return;
				}

				loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (loc != null) {
					listener.foundLocation(LocationManager.NETWORK_PROVIDER, loc);
					return;
				}
			}

			timer = new Timer();
			try {
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						timer = null;
						listener.couldntFindLocation();
					}
				}, timeoutMS);
			} catch (Exception e) {e.printStackTrace();};

			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetworkProvider);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPSProvider);
		}
	}

	final LocationListener locationListenerNetworkProvider = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			FindMe.this.onLocationChanged(this, LocationManager.NETWORK_PROVIDER, location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			FindMe.this.onProviderDisabled(provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			FindMe.this.onProviderEnabled(provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	};

	final LocationListener locationListenerGPSProvider = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			FindMe.this.onLocationChanged(this, LocationManager.GPS_PROVIDER, location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			FindMe.this.onProviderDisabled(provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			FindMe.this.onProviderEnabled(provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	};

	private void onLocationChanged(LocationListener locationListener, String provider, Location location) {
		if (location != null) {
			timer.cancel();
			timer = null;

			locationManager.removeUpdates(locationListener);
			listener.foundLocation(provider, location);
		}
	}

	private void onProviderDisabled(String provider) {
		Log.d("FindMe", "Provider disabled - " + provider); 
	}

	private void onProviderEnabled(String provider) {
		Log.d("FindMe", "Provider enabled - " + provider);
	}

}