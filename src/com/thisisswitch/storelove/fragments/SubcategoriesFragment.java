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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.switchsoft.maplibrary.util.Networking;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.adapter.CateGoryAdapter;
import com.thisisswitch.storelove.models.CategoriesModel;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.widgets.CustomProgressBar;

public class SubcategoriesFragment extends SherlockFragment{

	private ListView store_producit_listview;
	private PullToRefreshListView mPullToRefreshListView;
	private CustomProgressBar customProgressBar1;

	private CategoriesModel mCategoriesModel;
	private ArrayList<CategoriesModel> categoryList;
	//private AppPreferences mAppPreferences;

	private String main_catid = "";

	//callback listners for displaying article details fragment 
	private SubCategoryListOnclickListener listener;

	//Listeners for displaying news details page
	public interface SubCategoryListOnclickListener {
		public void onSubCateogrySelected(String storeId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof SubCategoryListOnclickListener) {
			listener = (SubCategoryListOnclickListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MyListFragment.OnItemSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//mAppPreferences = new AppPreferences(getActivity());
		mCategoriesModel = new CategoriesModel();
		categoryList = new ArrayList<CategoriesModel>();
	}
	//second this view called
	// Initialise all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		View view = inflater.inflate(R.layout.fragment_categories, container, false);

		mPullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.store_producit_listview);
		customProgressBar1 = (CustomProgressBar)view.findViewById(R.id.customProgressBar1);
		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		try {
			Bundle b = getArguments();
			main_catid =  b.getString("maincatId");
		} catch (Exception e) {}

		store_producit_listview = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setShowIndicator(false);
		mPullToRefreshListView.setPullToRefreshEnabled(false);
		store_producit_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				int pos = (position - 1);
				listener.onSubCateogrySelected(categoryList.get(pos).getSub_cat_id());
			}
		});

		if(Networking.isNetworkAvailable(getActivity())) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new LoadSubCategoriesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new LoadSubCategoriesTask().execute();
			}
		} else {
			Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
		}
	}

	//TODO///////////// loading product details
	private class LoadSubCategoriesTask extends AsyncTask<String, String, Boolean> {

		@Override
		protected void onPreExecute() {
			mPullToRefreshListView.setVisibility(View.GONE);
			customProgressBar1.setVisibility(View.VISIBLE);
			customProgressBar1.execute();
		}
		@Override
		protected Boolean doInBackground(String... params) {
			//			String time_stamp = mAppPreferences.getCategoryTimeStamp();
			//			try {
			//				time_stamp = URLEncoder.encode(time_stamp,"UTF-8");
			//			} catch (UnsupportedEncodingException e) {
			//				e.printStackTrace();
			//			}
			Log.d("SubCat","sucaturl-->"+AppUrls.getSubCategoriesUrl(main_catid, ""));
			String time_stamp = "";
			return getSubCategoriesResponse(AppUrls.getSubCategoriesUrl(main_catid, time_stamp));
		}
		@Override
		protected void onPostExecute(Boolean result) {
			mPullToRefreshListView.setVisibility(View.VISIBLE);
			customProgressBar1.setVisibility(View.GONE);

			if(result) {
				try {
					CateGoryAdapter adapter = new CateGoryAdapter(getActivity(), categoryList);
					store_producit_listview.setAdapter(adapter);
				} catch(Exception e){}
			} else {
				Toast.makeText(getActivity(), "No Products to display", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/** */
	public boolean getSubCategoriesResponse(String url) {

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
				jsonObject = new JSONObject(mydata);	
				JSONObject subcat_object = jsonObject.getJSONObject("GetSubCateResult");
				JSONArray sub_mainArray = subcat_object.getJSONArray("GET_SubCategoryList");
				categoryList = mCategoriesModel.parseSubCategories(sub_mainArray);;
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
