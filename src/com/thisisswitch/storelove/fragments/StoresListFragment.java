package com.thisisswitch.storelove.fragments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.switchsoft.googlemapsv2.support.SupportGoogleMap;
import com.switchsoft.googlemapsv2.support.SupportGoogleMap.SupportOnInfoWindowClickListener;
import com.switchsoft.maplibrary.MapLibrary;
import com.switchsoft.maplibrary.interfaces.MapLibraryInterface;
import com.switchsoft.maplibrary.model.DirectionModel;
import com.switchsoft.maplibrary.model.MarkerListModel;
import com.switchsoft.maplibrary.util.Networking;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.adapter.StoreListAdapter;
import com.thisisswitch.storelove.adapter.StoresWithDirectionAdapter;
import com.thisisswitch.storelove.models.StoreModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.socialnetwork.StorluvLoginActvity;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.widgets.CustomProgressBar;

public class StoresListFragment extends Fragment implements OnClickListener,MapLibraryInterface{

	//private static final String TAG = "StoresListFragment";

	private  ListView store_listView1;
	private CustomProgressBar customProgressBar1;
	private Button stores_nearme_button,allstores_button,
	stores_ifallow_button;
	public FrameLayout map_container;

	private StoreModel mStoreModel;
	private AppPreferences mAppPreferences ;
	private MapLibrary maplibrary ;
	private SupportGoogleMap mGoogleMap;
	//private FragmentManager mFragmentManager;
	private View view;

	//callback listners for displaying article details fragment 
	private StoreListOnclickListener listener;
	private PauseOnScrollListener pauseListener;
	private StoreListAdapter adapter;
	private ArrayList<StoreModel> storeList;
	private MarkerListModel markers;

	private String latLang = "";
	private int isFromNearBy = 0;
	private double latitude,longitude;
	private LoadStoreTask mLoadStoreTask;

	
	//Listeners for displaying news details page
	public interface StoreListOnclickListener {
		public void onStoreSelected(StoreModel mStoreModel);
		public void onMapEnabled(boolean enbale,MarkerListModel markers);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof StoreListOnclickListener) {
			listener = (StoreListOnclickListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MyListFragment.OnItemSelectedListener");
		}
	}

	//https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/FragmentTabsActivity.java
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppPreferences = new AppPreferences(getActivity());
		markers = new MarkerListModel();
		mStoreModel = new StoreModel();
		//mCache = ACache.get(getActivity());
		storeList = new ArrayList<StoreModel>();
	}

	// second this view called
	// Initialise all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		view = inflater.inflate(R.layout.fragment_stores_list, container, false);

		customProgressBar1 = (CustomProgressBar)view.findViewById(R.id.customProgressBar1);
		store_listView1 = (ListView)view.findViewById(R.id.store_listView1);
		map_container = (FrameLayout)view.findViewById(R.id.map_container);

		stores_nearme_button = (Button)view.findViewById(R.id.stores_nearme_button);
		allstores_button = (Button)view.findViewById(R.id.allstores_button);
		stores_ifallow_button = (Button)view.findViewById(R.id.stores_ifallow_button);

		map_container.setVisibility(View.GONE);
		//loading google map fragment details
		loadMapDetails();

		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		stores_nearme_button.setOnClickListener(this);
		allstores_button.setOnClickListener(this);
		stores_ifallow_button.setOnClickListener(this);
		pauseListener = new PauseOnScrollListener(MyApplication.imageLoader, true, true);
		//dispalying default buttons 
		changeButtonsBackground(1);

		if(Networking.isNetworkAvailable(getActivity())){
			isFromNearBy = 0;
			mLoadStoreTask = new LoadStoreTask();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				mLoadStoreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
			else {
				mLoadStoreTask.execute();
			}
		} else {
			Toast.makeText(getActivity(), "No network coonection", Toast.LENGTH_SHORT).show();
		}
		//listener.onStoreSelected(storeList.get(index));
		store_listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {

				mStoreModel.store_imge_url = storeList.get(position).getStore_imge_url();
				mStoreModel.store_like_count = storeList.get(position).getStore_like_count();
				mStoreModel.user_like_count = storeList.get(position).getUser_like_count();
				mStoreModel.store_latlang_location = storeList.get(position).getStore_latlang_location();
				mStoreModel.store_address = storeList.get(position).getStore_address();
				mStoreModel.store_emailid = storeList.get(position).getStore_emailid();
				mStoreModel.store_phone_number = storeList.get(position).getStore_phone_number();
				mStoreModel.store_phone_number = storeList.get(position).getStore_phone_number();
				//sendList.add(mStoreModel.addStores(mStoreModel));
				listener.onStoreSelected(mStoreModel);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.allstores_button:

			//cancel asyntask for disalying prasent selected values
			if(mLoadStoreTask != null) {
				mLoadStoreTask.cancel(true);
				mLoadStoreTask = null;
			}

			listener.onMapEnabled(false, markers);
			map_container.setVisibility(View.GONE);
			changeButtonsBackground(1);

			//clear previous adapter
			isFromNearBy = 0;
			if(adapter != null) {
				store_listView1.setAdapter(null);
				adapter = null;
			}
			//displaying stores list 
			if(!storeList.isEmpty()) {
				adapter = new StoreListAdapter(getActivity(), storeList);
				store_listView1.setAdapter(adapter);
				store_listView1.setOnScrollListener(pauseListener);				
			} else {
				Toast.makeText(getActivity(), "No Stores to display", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.stores_nearme_button: //TODO
			listener.onMapEnabled(true, markers);
			map_container.setVisibility(View.VISIBLE);
			changeButtonsBackground(2);
			//load all stores lat and langs and displaying in mapview
			loadMapLatlangs();
			isFromNearBy = 1;

			if(Networking.isNetworkAvailable(getActivity())) {
				//if previous task is running cancel it and run new one
				if(mLoadStoreTask != null) {
					mLoadStoreTask.cancel(true);
					mLoadStoreTask = null;
				}
				mLoadStoreTask = new LoadStoreTask();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					mLoadStoreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				else {
					mLoadStoreTask.execute();
				}
			} else {
				Toast.makeText(getActivity(), "No network coonection", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.stores_ifallow_button:
			listener.onMapEnabled(false, markers);
			map_container.setVisibility(View.GONE);
			changeButtonsBackground(3);

			if(mLoadStoreTask != null) {
				mLoadStoreTask.cancel(true);
				mLoadStoreTask = null;
			}
			//if user not logged in navigating to login page
			if(mAppPreferences.getUserid().equals("")) {

				mAppPreferences.saveUserid("0");
				Intent in = new Intent(getActivity(),StorluvLoginActvity.class);
				startActivity(in);
				getActivity().overridePendingTransition(R.anim.pull_up, R.anim.pull_out);

			}  else {
				//displaying uer fallowed stores
				ArrayList<StoreModel> fallowingStoresList = new ArrayList<StoreModel>();
				for(StoreModel storeModel:storeList){
					if(storeModel.getUser_like_count() == 10001){
						fallowingStoresList.add(mStoreModel.addStores(storeModel));
					}
				}
				//clear old adapter
				if(adapter != null) {
					store_listView1.setAdapter(null);
					adapter = null;
				}
				if(!fallowingStoresList.isEmpty()) {
					adapter = new StoreListAdapter(getActivity(), fallowingStoresList);
					store_listView1.setAdapter(adapter);
					store_listView1.setOnScrollListener(pauseListener);
				} else {
					store_listView1.setAdapter(null);
					Toast.makeText(getActivity(), "No stores to display", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			break;
		}
	}

	/** 
	 * @param position 1: all store 2: near me 3: fallow stores*/
	private void changeButtonsBackground(int position){
		switch (position) {
		case 1:

			allstores_button.setBackgroundResource(R.drawable.ab_transparent_storelove);
			stores_nearme_button.setBackgroundResource(R.drawable.ab_stacked_solid_storelove);
			stores_ifallow_button.setBackgroundResource(R.drawable.ab_stacked_solid_storelove);

			break;
		case 2:

			allstores_button.setBackgroundResource(R.drawable.ab_stacked_solid_storelove);
			stores_nearme_button.setBackgroundResource(R.drawable.ab_transparent_storelove);
			stores_ifallow_button.setBackgroundResource(R.drawable.ab_stacked_solid_storelove);

			break;
		case 3:

			stores_nearme_button.setBackgroundResource(R.drawable.ab_stacked_solid_storelove);
			allstores_button.setBackgroundResource(R.drawable.ab_stacked_solid_storelove);
			stores_ifallow_button.setBackgroundResource(R.drawable.ab_transparent_storelove);

			break;
		default:
			break;
		}
	}

	/**loading map fragment */
	private void loadMapDetails() {
		try {

			//mFragmentManager = getFragmentManager();
			// Get an instance of the SupportGoogleMap
			mGoogleMap = SupportGoogleMap.newInstance(getActivity(),
					(SupportMapFragment)getFragmentManager().findFragmentById(R.id.map));

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

	//TODO///////////// loading stores list
	private class LoadStoreTask extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			customProgressBar1.setVisibility(View.VISIBLE);
			customProgressBar1.execute();
			store_listView1.setVisibility(View.GONE);
		}
		@Override
		protected Boolean doInBackground(String... params) {
			return getStoreResponse(AppUrls.getNearByStores(mAppPreferences.getRegionId(), mAppPreferences.getUserid(), "", mAppPreferences.getLatLang()));
		}
		@Override
		protected void onPostExecute(Boolean result) {

			customProgressBar1.setVisibility(View.GONE);
			store_listView1.setVisibility(View.VISIBLE);

			if(result) {
				try{
					if(isFromNearBy == 1) {
						isFromNearBy = 0;
						StoresWithDirectionAdapter adapter = new StoresWithDirectionAdapter(getActivity(), storeList);
						store_listView1.setAdapter(adapter);
					} else {
						adapter = new StoreListAdapter(getActivity(), storeList);
						store_listView1.setAdapter(adapter);
					}
					PauseOnScrollListener listener = new PauseOnScrollListener(MyApplication.imageLoader, true, true);
					store_listView1.setOnScrollListener(listener);
				} catch(Exception e){}
			} else {
				Toast.makeText(getActivity(), "No stores to display", Toast.LENGTH_SHORT).show();
			}
		}

		//loading category reponse here
		private boolean getStoreResponse(String url){

			Log.d("Stores","url-->"+url);
			//Declaring the JSON object
			JSONObject jsonObject;
			//Declaring the URL connection object
			URLConnection tc1;

			try {

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
					//Log.e(TAG,"getStoreResponse mydata-->"+mydata);
					jsonObject = new JSONObject(mydata);	
					//GetStoresUserLikeResult
					JSONObject subcat_object = jsonObject.getJSONObject("GetStoresWithLoactionResult");
					JSONArray sub_mainArray = subcat_object.getJSONArray("GET_STORE");
					String timeStamp = subcat_object.getString("SDT");
					mAppPreferences.saveStoreTimestamp(timeStamp);

					if(sub_mainArray.length() != 0) {
						storeList = mStoreModel.parseStoreDetails(sub_mainArray);
					} else {
						return false;						
					}
				}
			}catch (Exception e){
				e.printStackTrace();
				return false;
			}
			return true;
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
		maplibrary.intializeLocation(getActivity());
		maplibrary.onStartClass();
	}

	@Override
	public void onResume() {
		super.onResume();
		maplibrary.onResumeClass();
	}

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("SupportMapFragment");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException i) { } 
		catch (IllegalAccessException i) { }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView(); 
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));   
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}

	private void loadMapLatlangs(){

		for(StoreModel storeModel:storeList){
			///Log.d(TAG,"Latlangs-->"+storeModel.getStore_latlang_location());
			String latlang = storeModel.getStore_latlang_location();	
			if(latlang.length() !=0 ){
				StringTokenizer st = new StringTokenizer(latlang, ",");
				markers.setAddress(storeModel.getStore_name());
				markers.setCity(storeModel.getStore_address());

				while (st.hasMoreTokens()) {
					latitude = Double.parseDouble(st.nextToken());
					longitude = Double.parseDouble(st.nextToken());

					LatLng value = new LatLng(latitude, longitude);
					markers.setLatLong(value);
					//Log.d(TAG,"latitude-->"+latitude);
					//Log.d(TAG,"longitude-->"+longitude);
				}
			}
			//finding current location
			maplibrary.findUserLocation(false, "");	
		}
	}

	@Override
	public void userLocation(String address, double latitude, double longitude,String cityName) {
		//TODO
		latLang = String.valueOf(latitude)+","+String.valueOf(longitude);
		//Log.v(TAG,"latLang-->"+latLang);
		if(latLang.length() !=0) {
			mAppPreferences.saveLatLang(latLang);
		}
		if(markers.getLatlong().size() !=0) {
			//markers
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.point);
			maplibrary.drawMarkers(mGoogleMap, markers,bitmap, true);			
		}
	}

	@Override
	public void locationServiceConnected() {
		//maplibrary.findUserLocation(false, "");	
	}
	@Override
	public void directionResult(DirectionModel directionModel) {
	}
	@Override
	public void turnOnGpsforLocation() {
	}
	@Override
	public void googlePlayUnavailable() {
	}
	@Override
	public void noNetwork() {
	}
	@Override
	public void failedDirection() {
	}
	@Override
	public void failedCurrentLocation() {
		latLang = "0";
	}
}
