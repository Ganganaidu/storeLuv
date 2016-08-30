package com.thisisswitch.storelove.fragments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.switchsoft.maplibrary.util.Networking;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.adapter.ProductsListAdapter;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.widgets.CustomProgressBar;

public class MyFeedDetailsFragment extends SherlockFragment implements 
OnClickListener {

	Button fallowing_store_button,fallowing_product_button;
	ListView myfeed_producit_listview,myfeed_stores_listview;
	CustomProgressBar myfeed_customprogressbar;

	private AppPreferences mAppPreferences;
	private ProdcutModel mProdcutModel;
	private ArrayList<ProdcutModel> productList; 

	//callback listners for displaying article details fragment 
	private MyFeedOnclickListener listener;
	private boolean isStore = false;
	private ProductsListAdapter adapter;

	//Listeners for displaying news details page
	public interface MyFeedOnclickListener {
		public void onMyFeedSelected(String storeId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MyFeedOnclickListener) {
			listener = (MyFeedOnclickListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MyListFragment.OnItemSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppPreferences = new AppPreferences(getActivity());
		mProdcutModel = new ProdcutModel();
		productList = new ArrayList<ProdcutModel>();
	}
	//second this view called
	// Initialise all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		View view = inflater.inflate(R.layout.fragment_myfeed_list, container, false);

		myfeed_producit_listview = (ListView)view.findViewById(R.id.myfeed_producit_listview);
		myfeed_stores_listview = (ListView)view.findViewById(R.id.myfeed_stores_listview);
		myfeed_customprogressbar = (CustomProgressBar)view.findViewById(R.id.myfeed_customprogressbar);
		fallowing_store_button = (Button)view.findViewById(R.id.fallowing_store_button);
		fallowing_product_button = (Button)view.findViewById(R.id.fallowing_product_button);

		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		fallowing_store_button.setOnClickListener(this);
		fallowing_product_button.setOnClickListener(this);
		fallowing_store_button.setBackgroundResource(R.drawable.ab_transparent_storelove);

		//listener.onStoreSelected(storeList.get(index));
		//		myfeed_producit_listview.setOnItemClickListener(new OnItemClickListener() {
		//			@Override
		//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
		//				//	listener.onMyFeedSelected(categoryList.get(position).getMain_cat_id());
		//			}
		//		});

		if(Networking.isNetworkAvailable(getActivity())) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
			else {
				new LoadProductsTask().execute();
			}
		} else {
			Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fallowing_store_button:
			isStore = true;

			fallowing_store_button.setBackgroundResource(R.drawable.ab_transparent_storelove);
			fallowing_product_button.setBackgroundResource(R.drawable.ab_stacked_solid_storelove);

			if(Networking.isNetworkAvailable(getActivity())) {

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				else {
					new LoadProductsTask().execute();
				}
			} else {
				Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.fallowing_product_button:

			fallowing_store_button.setBackgroundResource(R.drawable.ab_stacked_solid_storelove);
			fallowing_product_button.setBackgroundResource(R.drawable.ab_transparent_storelove);

			isStore = false;
			if(Networking.isNetworkAvailable(getActivity())) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new LoadProductsTask().execute();
				}
			} else {
				Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	//TODO///////////// loading product details
	private class LoadProductsTask extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			myfeed_producit_listview.setVisibility(View.GONE);
			myfeed_stores_listview.setVisibility(View.GONE);
			myfeed_customprogressbar.setVisibility(View.VISIBLE);
			myfeed_customprogressbar.execute();
		}
		@Override
		protected Boolean doInBackground(String... params) {
			String url = "";
			if(isStore){
				url = AppUrls.getUserLikedStores(mAppPreferences.getUserid(),"0","10001");
			} else {
				url = AppUrls.getUserLikedProducts(mAppPreferences.getUserid(),"0","10001");	
			}
			return getProductsResponse(url);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			myfeed_producit_listview.setVisibility(View.VISIBLE);
			myfeed_stores_listview.setVisibility(View.GONE);
			myfeed_customprogressbar.setVisibility(View.GONE);

			if(result) {
				if(adapter != null){
					myfeed_producit_listview.setAdapter(null);
					adapter = null;
				}
				try{
					if(!productList.isEmpty()) {
						adapter = new ProductsListAdapter(getActivity(), productList,mAppPreferences);
						myfeed_producit_listview.setAdapter(adapter);
					}
				} catch(Exception e){}
			} else {
				Toast.makeText(getActivity(), "No Products to display", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/** */
	public boolean getProductsResponse(String url) {

		Log.e("MyFeed","URL-->"+url);
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
				JSONArray productArrayObject ;
				if(isStore){
					productArrayObject = jsonObject.getJSONArray("GetUserStoreLikeProductsResult");
				} else {
					productArrayObject = jsonObject.getJSONArray("GetUserLikeProductsResult");					
				}
				if(productArrayObject.length() != 0) {
					productList = mProdcutModel.parseProductJsonResponse(productArrayObject);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
