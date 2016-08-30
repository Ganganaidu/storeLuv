package com.thisisswitch.storelove;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.switchsoft.googlemapsv2.support.SupportGoogleMap;
import com.switchsoft.googlemapsv2.support.SupportGoogleMap.SupportOnInfoWindowClickListener;
import com.switchsoft.maplibrary.MapLibrary;
import com.switchsoft.maplibrary.interfaces.MapLibraryInterface;
import com.switchsoft.maplibrary.model.DirectionModel;
import com.thisisswitch.storelove.adapter.ConsumerDirectionAdapter;
import com.thisisswitch.storelove.dialog.DialogFragment_SingleButton;

/** Map activity, which displays the store on the map
 * Send the map latitude and longitude in intent while calling this activity
 * 
 * Sample:
 * 	Intent products = new Intent(this, MapActivity.class);
	products.putExtra("lat", latitude);
	products.putExtra("long", longitude);
	startActivity(products);
 * */
public class MapActivity extends SherlockFragmentActivity 
implements MapLibraryInterface, OnClickListener {

	//Action bar
	private ActionBar actionBar;

	private SupportGoogleMap mGoogleMap;
	private RelativeLayout mDirection_Relativelayout;
	private TextView mDriveTo_Textview;
	//Direction
	private LinearLayout mDirection_Linearlayout;
	private TextView mFromaddress_Textview, mToaddress_Textivew;
	private ListView mDirections_Listview;

	//Map library
	private MapLibrary maplibrary;

	//Data holder
	private String currentLocation = "";
	private String driveMeToLocation = "";
	private boolean hideMenu = true;
	private DirectionModel mDirectionmodel;

	//Animation
	private Animation inFromBottom, outToBottom;
	double lat,lang;

	@Override
	protected void onCreate(Bundle savedInstance) {
		//Add a request for Progress bar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_consumer_map);

		// Set up the action bar.
		actionBar = getSupportActionBar();

		// Specify that the Home/Up button should be enabled
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Storeluv - Map");

		mDirection_Relativelayout = (RelativeLayout)findViewById(R.id.direction_relativelayout);
		mDriveTo_Textview = (TextView)findViewById(R.id.driveTo_textview);

		//Direction
		mDirection_Linearlayout = (LinearLayout)findViewById(R.id.direction_linearlayout);
		mFromaddress_Textview = (TextView)findViewById(R.id.fromaddress_textview);
		mToaddress_Textivew = (TextView)findViewById(R.id.toaddress_textivew);
		mDirections_Listview = (ListView)findViewById(R.id.directions_listview);

		//Adding header to list
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header, mDirections_Listview, false);
		mDirections_Listview.addHeaderView(header, null, false);

		//By default the progress is hidden
		setSupportProgressBarIndeterminateVisibility(false);

		try {
			// Get an instance of the SupportGoogleMap
			mGoogleMap = SupportGoogleMap.newInstance(getApplicationContext(),
					(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));

			//This closes the info window on its click
			mGoogleMap.setOnInfoWindowClickListener(new SupportOnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(long id, Marker marker) {
					marker.hideInfoWindow();
					hideDirectionView();
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

		mDirection_Relativelayout.setOnClickListener(this);

		// load the animation
		inFromBottom = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_bottom_in);
		outToBottom = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_bottom_out);

		//Get data sent from previous class
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			
			lat = extras.getDouble("lat");
			lang = extras.getDouble("long");
			driveMeToLocation = String.valueOf(lat) + "," +String.valueOf(lang);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.map_menu, menu);

		//Hide the action bar menu item
		MenuItem list_item = menu.findItem(R.id.menu_list);

		if(hideMenu){
			list_item.setVisible(false);
		} else {
			list_item.setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			if(mDirection_Linearlayout.getVisibility() == View.VISIBLE){
				mDirection_Linearlayout.setVisibility(View.GONE);

				hideMenu = false;
				this.supportInvalidateOptionsMenu();
			} else {
				finish();
				overridePendingTransition (R.anim.open_main, R.anim.close_next);
			}
			break;

		case R.id.menu_list:
			displayDirectionList();
			break;

		default:
			break;
		}
		return true;
	}

	/** Displaying the alert dialog
	 */
	private void showDialog_singlebutton(String message) {
		DialogFragment_SingleButton newFragment = DialogFragment_SingleButton.newInstance(
				message);
		newFragment.show(getSupportFragmentManager(), "dialog");
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

		Log.v("MAP","lat-->"+lat);
		Log.v("MAP","lang-->"+lang);
		//
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.point);
		LatLng location = new LatLng(lat,lang);
		//LatLng location = new LatLng(17.4399295,78.4982741);
		
		//mGoogleMap.setMyLocationEnabled(true);
		//mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
		maplibrary.drawMarker(mGoogleMap, location, "", "", 15, bitmap, true);
		//maplibrary.dra
	}

	@Override
	public void onResume() {
		super.onResume();
		maplibrary.onResumeClass();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		maplibrary.onActivityResultClass(requestCode, resultCode, intent);
	}


	private void enableView(){
		setSupportProgressBarIndeterminateVisibility(false);
	}

	private void disableView(){
		setSupportProgressBarIndeterminateVisibility(true);
	}

	private void displayDirectionView(){
		if(mDirection_Relativelayout.getVisibility() == View.GONE){
			mDirection_Relativelayout.setVisibility(View.VISIBLE);
			// start the animation
			mDirection_Relativelayout.startAnimation(inFromBottom);	
		}

		// set animation listener
		inFromBottom.setAnimationListener(new AnimationListener() {

			@Override 
			public void onAnimationStart(Animation animation) {
				mDirection_Relativelayout.setVisibility(View.VISIBLE);
			}

			@Override public void onAnimationRepeat(Animation animation) {}

			@Override public void onAnimationEnd(Animation animation) {}
		});
	}

	private void hideDirectionView(){
		if(mDirection_Relativelayout.getVisibility() == View.VISIBLE){
			// start the animation
			mDirection_Relativelayout.startAnimation(outToBottom);	
		}

		// set animation listener
		outToBottom.setAnimationListener(new AnimationListener() {

			@Override public void onAnimationStart(Animation animation) {}

			@Override public void onAnimationRepeat(Animation animation) {}

			@Override 
			public void onAnimationEnd(Animation animation) {
				mDirection_Relativelayout.setVisibility(View.GONE);				
			}
		});
	}

	@Override
	public void directionResult(DirectionModel directionmodel) {
		try {
			//Current location
			Bitmap from = BitmapFactory.decodeResource(getResources(), R.drawable.point_a);
			Bitmap to = BitmapFactory.decodeResource(getResources(), R.drawable.point_b);
			maplibrary.drawMarker(mGoogleMap, directionmodel.getPolyline().get(0), 
					"", directionmodel.getStartAddress(), 13, from, true);
			maplibrary.drawMarker(mGoogleMap, directionmodel.getPolyline().
					get(directionmodel.getPolyline().size() -1 ), "", 
					directionmodel.getEndAddress(), 13, to, false);

			//Driving direction
			mDirectionmodel = directionmodel;
			maplibrary.drawPolyline(mGoogleMap, directionmodel.getPolyline(), Color.BLUE, 4, 
					directionmodel.getStartAddress(), directionmodel.getEndAddress());

			enableView();	

			hideMenu = false;
			this.supportInvalidateOptionsMenu();


		} catch (Exception e) {
		}

	}

	@Override
	public void failedCurrentLocation() {
		try {
			enableView();
			showDialog_singlebutton(getResources().getString(R.string.location_notfound));	
		} catch (Exception e) {
		}
	}

	@Override
	public void failedDirection() {
		try {
			enableView();
			showDialog_singlebutton(getResources().getString(R.string.failed_directions));	
		} catch (Exception e) {
		}

	}

	@Override
	public void googlePlayUnavailable() {
	}

	@Override
	public void locationServiceConnected() {
		try {
			disableView();
			maplibrary.findUserLocation(false, "");	
		} catch (Exception e) {
		}
	}

	@Override
	public void noNetwork() {
		try {
			enableView();
			showDialog_singlebutton(getResources().getString(R.string.no_connection));	
		} catch (Exception e) {
		}
	}

	@Override
	public void turnOnGpsforLocation() {
		try {
			Toast.makeText(this, getResources().getString(R.string.switch_gps), Toast.LENGTH_LONG).show();	
		} catch (Exception e) {
		}

	}

	@Override
	public void userLocation(String address, double latitude, double longitude,String cityName) {
		try {
			currentLocation = String.valueOf(latitude) + "," + String.valueOf(longitude);
			//Display driving view
			mDriveTo_Textview.setText("Driving direction" + address);
			displayDirectionView();	
			enableView();	
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.direction_relativelayout:
			disableView();
			try {
				maplibrary.getDirections(MapActivity.this, currentLocation, driveMeToLocation);	
			} catch (Exception e) {
				enableView();
				failedDirection();
			}
			mDirection_Relativelayout.setVisibility(View.GONE);
			break;

		default:
			break;
		}		
	}

	private void displayDirectionList(){

		mDirection_Linearlayout.setVisibility(View.VISIBLE);

		mFromaddress_Textview.setText(mDirectionmodel.getStartAddress());	
		mToaddress_Textivew.setText(mDirectionmodel.getEndAddress());	

		ConsumerDirectionAdapter adapter = new ConsumerDirectionAdapter(this, mDirectionmodel);
		mDirections_Listview.setAdapter(adapter);

		hideMenu = true;
		this.supportInvalidateOptionsMenu();

	}

	@Override
	public void onBackPressed() {
		if(mDirection_Linearlayout.getVisibility() == View.VISIBLE){
			mDirection_Linearlayout.setVisibility(View.GONE);

			hideMenu = false;
			this.supportInvalidateOptionsMenu();
		} else {
			finish();
		}
	}
	
	
}
