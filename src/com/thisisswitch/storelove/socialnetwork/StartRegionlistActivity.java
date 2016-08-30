package com.thisisswitch.storelove.socialnetwork;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.switchsoft.googlemapsv2.support.SupportGoogleMap;
import com.switchsoft.googlemapsv2.support.SupportGoogleMap.SupportOnInfoWindowClickListener;
import com.switchsoft.maplibrary.MapLibrary;
import com.switchsoft.maplibrary.interfaces.MapLibraryInterface;
import com.switchsoft.maplibrary.model.DirectionModel;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.adapter.RegionListAdapter;
import com.thisisswitch.storelove.dialog.DialogFragment_SingleButton;
import com.thisisswitch.storelove.models.RegionsModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;

public class StartRegionlistActivity extends SherlockFragmentActivity implements OnClickListener, MapLibraryInterface {
	private String TAG = "SelectRegionActivity";

	private TextView seleted_region_textView;
	private Button checkgps_button;
	private ListView region_listView;

	private SupportGoogleMap mGoogleMap;
	private AppPreferences mAppPreferences ;
	private MapLibrary maplibrary ;

	private RegionsModel mRegionsModel;
	private ArrayList<RegionsModel> regionLIst = null;
	private RegionListAdapter adapter = null;
	private ProgressDialog mActivityIndicator;
	private double lat,lang;
	private String cityName= ""; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_region_list);

		mAppPreferences = new AppPreferences(this);
		mRegionsModel = new RegionsModel();

		// Specify that the Home/Up button should be enabled
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("PICK A REGION");

		seleted_region_textView = (TextView)findViewById(R.id.seleted_region_textView);
		checkgps_button = (Button)findViewById(R.id.checkgps_button);
		region_listView  = (ListView)findViewById(R.id.region_listView);

		//loading location list from server and disaplying in listview
		if(Networking.isNetworkAvailable(this)){
			new LoadRegionsTask().execute();
		} else {
			Toast.makeText(this, Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
		}
		loadMapDetails();

		//printing saved and selected region name 
		seleted_region_textView.setText(mAppPreferences.getRegionName());
		checkgps_button.setOnClickListener(this);

		region_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				//sving for later use
				mAppPreferences.saveRegionName(regionLIst.get(position).getRegion_name());
				mAppPreferences.saveRegionId(regionLIst.get(position).getRegion_id());
				mAppPreferences.savePreviousRegionId(regionLIst.get(position).getRegion_id());

				Intent in = new Intent(StartRegionlistActivity.this,StartUpLoginActvity.class);
				startActivity(in);
				finish();

				Toast.makeText(StartRegionlistActivity.this, regionLIst.get(position).getRegion_name(), Toast.LENGTH_SHORT).show();
				//finish();
			}
		});
	}

	/**loading map fragment */
	private void loadMapDetails() {
		try {
			// Get an instance of the SupportGoogleMap
			mGoogleMap = SupportGoogleMap.newInstance(getApplicationContext(),
					(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));

			//This closes the info window on its click
			mGoogleMap.setOnInfoWindowClickListener(new SupportOnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(long id, Marker marker) {
					marker.hideInfoWindow();
					//hideDirectionView();
				}
			});
			//Listener to hide the direction view
			mGoogleMap.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public void onMapClick(LatLng arg0) {
					//hideDirectionView();
				}
			});
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) { 
			finish();
			overridePendingTransition (R.anim.pull_out, R.anim.pull_out);
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkgps_button:

			new GetAddressTask().execute();
			break;
		default:
			break;
		}
	}

	@Override
	public void onStop() {
		maplibrary.onStopClass();
		super.onStop();
	}

	@Override
	public void onPause() {
		maplibrary.onPauseClass();
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
		maplibrary = new MapLibrary();
		MapLibrary.delegate = this;
		maplibrary.intializeLocation(this);
		maplibrary.onStartClass();
	}

	@Override
	public void onResume() {
		super.onResume();
		maplibrary.onResumeClass();
	}
	@Override
	public void userLocation(String address, double latitude, double longitude,String cityName) {
		
		String latLang = String.valueOf(latitude)+","+String.valueOf(longitude);
		if(latLang.length() !=0) {
			mAppPreferences.saveLatLang(latLang);
		}

		this.cityName = cityName;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.point);
		LatLng location = new LatLng(latitude,longitude);
		mGoogleMap.setMyLocationEnabled(true);
		maplibrary.drawMarker(mGoogleMap, location, "", "", 15, bitmap, true);

		//printing auto location
		seleted_region_textView.setText(cityName);
	}
	@Override
	public void locationServiceConnected() {
		// TODO Auto-generated method stub
		maplibrary.findUserLocation(false, "");	
	}
	@Override
	public void directionResult(DirectionModel directionModel) {
	}
	@Override
	public void turnOnGpsforLocation() {
		try {
			Toast.makeText(this, getResources().getString(R.string.switch_gps), Toast.LENGTH_LONG).show();	
		} catch (Exception e) {
		}
	}
	@Override
	public void googlePlayUnavailable() {
	}
	@Override
	public void noNetwork() {
		try {
			showDialog_singlebutton(getResources().getString(R.string.no_connection));	
		} catch (Exception e) {
		}
	}
	@Override
	public void failedDirection() {
	}
	@Override
	public void failedCurrentLocation() {
		try {
			showDialog_singlebutton(getResources().getString(R.string.location_notfound));	
		} catch (Exception e) {
		}
	}

	/** Displaying the alert dialog
	 */
	private void showDialog_singlebutton(String message) {
		DialogFragment_SingleButton newFragment = DialogFragment_SingleButton.newInstance(message);
		newFragment.show(getSupportFragmentManager(), "dialog");
	}

	//TODO 
	private class LoadRegionsTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected void onPreExecute() {
			mActivityIndicator  =  new ProgressDialog(StartRegionlistActivity.this);
			mActivityIndicator.show();
		}
		@Override
		protected Boolean doInBackground(String... params) {

			String time_stamp = mAppPreferences.getRegionsTimestamp();
			try {
				time_stamp = URLEncoder.encode(time_stamp,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Log.e(TAG,"region list "+AppUrls.getRegionsUrl(time_stamp));
			return getRegionsResponse(AppUrls.getRegionsUrl(time_stamp));
		}
		@Override
		protected void onPostExecute(Boolean result) {

			if(mActivityIndicator  != null) {
				mActivityIndicator.dismiss();
			}

			if(result) {

				try {
					adapter = new RegionListAdapter(StartRegionlistActivity.this, regionLIst);
					region_listView.setAdapter(adapter);

					// trying to find auto location list in exisisted list
					for(int i=0; i< regionLIst.size();i++) {
						String name= regionLIst.get(i).getRegion_name();
						if(name.equalsIgnoreCase(cityName)) {
							//saving city name id for later use
							mAppPreferences.saveRegionName(cityName);
							mAppPreferences.saveRegionId(regionLIst.get(i).getRegion_id());					
						}
					}
				} catch(Exception e){}
			} else {
				Toast.makeText(StartRegionlistActivity.this, "No Location listed", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean getRegionsResponse(String url){
		//Declaring the URL connection object
		URLConnection tc1;
		try{

			URL tableConnect = new URL(url);
			tc1 = tableConnect.openConnection(); 
			tc1.setReadTimeout(Globals.readTimeOut);
			tc1.setConnectTimeout(Globals.readTimeOut);
			BufferedReader input = new BufferedReader(new InputStreamReader(tc1.getInputStream()));
			String line2;
			String mydata = "";
			//reading the content of the result
			while ((line2 = input.readLine()) != null) {
				//Gets all the characters and adding them into myData string
				mydata = mydata + line2;
			}
			if(mydata.length() != 0) {
				regionLIst = mRegionsModel.parseRegionsJson(mydata);
			} else {
				return false;
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}


	//TODO
	protected class GetAddressTask extends AsyncTask<String, String, String> {
		String addressText = "";

		@Override
		public void onPreExecute(){
			mActivityIndicator  =  new ProgressDialog(StartRegionlistActivity.this);
			mActivityIndicator.show();
		}
		/**
		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned
		 * address, and return the address to the UI thread.
		 */
		@Override
		protected String doInBackground(String... params) {
			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses
			 * android.location.Geocoder, but other geocoders that conform to address standards
			 * can also be used.
			 */
			Geocoder geocoder = new Geocoder(StartRegionlistActivity.this, Locale.getDefault());

			// Create a list to contain the result address
			List <Address> addresses = null;

			// Try to get an address for the current location. Catch IO or network problems.
			try {
				/*
				 * Call the synchronous getFromLocation() method with the latitude and
				 * longitude of the current location. Return at most 1 address.
				 */
				if(lat != 0){
					addresses = geocoder.getFromLocation(lat,lang, 1);
				}
				Log.e(TAG,"addresses-->"+addresses);
				// Catch network or other I/O problems.
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (addresses != null && addresses.size() > 0) {
				//This gets the complete address
				addressText = addresses.get(0).getLocality();

				Log.e(TAG,"addressText-->"+addressText);
				// Return the text
				return addressText;
				// If there aren't any addresses, post a message
			} else {
				return "No location found";
			}
		}
		@Override
		public void onPostExecute(String reponse) {
			Log.e(TAG,"addressText-->"+reponse);

			if(mActivityIndicator != null) {
				mActivityIndicator.dismiss();				
			}
			String regionName = "";
			for(int i=0; i< regionLIst.size();i++) {
				regionName = regionLIst.get(i).getRegion_name();
				if(regionName.equalsIgnoreCase(reponse)) {
					mAppPreferences.saveRegionName(regionName);
					mAppPreferences.saveRegionId(regionLIst.get(i).getRegion_id());					
				}
			}
			seleted_region_textView.setText(mAppPreferences.getRegionName());
			Toast.makeText(StartRegionlistActivity.this, regionName, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition (0, R.anim.pull_out);
	}
}
