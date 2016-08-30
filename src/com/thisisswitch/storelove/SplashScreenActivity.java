package com.thisisswitch.storelove;

import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMRegistrar;
import com.thisisswitch.storelove.adapter.ViewFragmentAdapter;
import com.thisisswitch.storelove.fragments.SplashFragment1;
import com.thisisswitch.storelove.fragments.SplashFragment2;
import com.thisisswitch.storelove.fragments.SplashFragment4;
import com.thisisswitch.storelove.gcm.GCMUtilClient;
import com.thisisswitch.storelove.listeners.OnDataChangeListeners;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.services.DataDownloader;
import com.thisisswitch.storelove.socialnetwork.StartRegionlistActivity;
import com.thisisswitch.storelove.utils.Networking;
import com.thisisswitch.storelove.widgets.CustomProgressBar;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class SplashScreenActivity extends SherlockFragmentActivity implements 
SplashFragment4.OnSkipListener{

	TextView loading_state_textView,appname_textView1;
	CustomProgressBar splash_customProgressBar1;
	RelativeLayout loading_layout,help_layout;

	//PanningView panningView1; 
	DataDownloader mDataDownloader = null;
	AppPreferences mAppPreferences = null;
	OnDataChangeListeners mOnDataChangeListeners = null;

	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splashscreen);

		mAppPreferences = new AppPreferences(this);

		loading_layout = (RelativeLayout)findViewById(R.id.loading_layout);
		help_layout = (RelativeLayout)findViewById(R.id.help_layout);

		loading_state_textView = (TextView)findViewById(R.id.loading_state_textView);
		appname_textView1 = (TextView)findViewById(R.id.appname_textView1);
		splash_customProgressBar1 = (CustomProgressBar)findViewById(R.id.splash_customProgressBar1);
		splash_customProgressBar1.execute();

		//android device id
		String android_id = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
		mAppPreferences.saveDeviceId(android_id);

		if(Networking.isNetworkAvailable(this)){
			displayDetails();
		} else {
			//TODO
			//Display alert and close this layout
			Toast.makeText(this,Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
			finish();
		}
	}


	private void displayDetails() {

		//checking first time splash screens
		if(mAppPreferences.getSplashState() == 0) {

			help_layout.setVisibility(View.VISIBLE);//showing slides
			loading_layout.setVisibility(View.GONE);
			initialisePaging();			

		} else if(mAppPreferences.getRegionName().equals("")) {
			navigateToRegionList();
		} else {
			navigateToNextScreen();			
		}
		//if network avaialble loading store and product details from server
		if(mAppPreferences.getDeviceId().equals("")){
			GCMUtilClient.register(SplashScreenActivity.this);
		}
		//nextAct();
		//mAppPreferences.saveRegionId("9");//temp
		mAppPreferences.saveFromCount("0");
		//mAppPreferences.saveUserid("0");
	}

	private void navigateToNextScreen() {
		splash_customProgressBar1.clearAnimation();
		Intent in = new Intent(this,StoreLoveActivity.class);
		startActivity(in);
		finish();
	}

	//	private void navigateToLoginScreen() {
	//		splash_customProgressBar1.clearAnimation();
	//		Intent in = new Intent(this,StartUpLoginActvity.class);
	//		startActivity(in);
	//		finish();
	//	}

	private void navigateToRegionList() {
		splash_customProgressBar1.clearAnimation();
		//if(mAppPreferences.getRegionName().equals("")) {
		Intent in = new Intent(SplashScreenActivity.this,StartRegionlistActivity.class);
		startActivity(in);
		finish();
		//}
	}

	/**
	 * Initialise the fragments to be paged(Help pages)
	 */
	private void initialisePaging() {

		List<Fragment> fragments = new Vector<Fragment>();

		fragments.add(Fragment.instantiate(this, SplashFragment1.class.getName()));
		fragments.add(Fragment.instantiate(this, SplashFragment2.class.getName()));
		//fragments.add(Fragment.instantiate(this, SplashFragment3.class.getName()));
		fragments.add(Fragment.instantiate(this, SplashFragment4.class.getName()));
		//fragments.add(Fragment.instantiate(this, SplashFragment5.class.getName())); 

		ViewFragmentAdapter mAdapter = new ViewFragmentAdapter(getSupportFragmentManager(),fragments);
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator = indicator;
		indicator.setViewPager(mPager);

		final float density = getResources().getDisplayMetrics().density;
		//indicator.setBackgroundColor(getResources().getColor(R.color.greyheader));
		indicator.setRadius(5 * density);
		//indicator.setPageColor(getResources().getColor(R.color.greyheaderend));
		indicator.setFillColor(getResources().getColor(R.color.white));
		indicator.setStrokeColor(getResources().getColor(R.color.greyheader));
		indicator.setStrokeWidth(1 * density);
	}

	@Override
	public void onDoneSelected(int result) {

		mAppPreferences.saveSplashState(1);
		if(mAppPreferences.getRegionName().equals("")){
			navigateToRegionList();
		} else {
			navigateToNextScreen();				
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}

	@Override
	protected void onDestroy() {
		if(GCMRegistrar.isRegistered(getApplicationContext() )) {
			GCMRegistrar.onDestroy(getApplicationContext());	
		} 
		super.onDestroy();
	}
}
