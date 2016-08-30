package com.thisisswitch.storelove.fragments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.switchsoft.maplibrary.util.Networking;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.adapter.ProductsListAdapter;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.widgets.CustomProgressBar;

public class CategoryProductListFragment extends SherlockFragment{

	private ListView store_producit_listview;
	private PullToRefreshListView mPullToRefreshListView;
	private CustomProgressBar customProgressBar1;

	private ProdcutModel mProdcutModel;
	private ArrayList<ProdcutModel> productList;
	private AppPreferences mAppPreferences;
	private ProductsListAdapter adapter;

	String subCatId = "";
	String fromPageCount = "0";

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
		View view = inflater.inflate(R.layout.fragment_categories, container, false);

		mPullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.store_producit_listview);
		customProgressBar1 = (CustomProgressBar)view.findViewById(R.id.customProgressBar1);
		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		try {
			Bundle b = getArguments();
			subCatId =  b.getString("subcatId");
		} catch (Exception e) {}

		pullToRefreshChangedView();

		store_producit_listview.setVisibility(View.GONE);
		customProgressBar1.setVisibility(View.VISIBLE);
		customProgressBar1.execute();

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

	//TODO
	public void pullToRefreshChangedView() {
		// disabling scroll when pull
		mPullToRefreshListView.setScrollingWhileRefreshingEnabled(!mPullToRefreshListView.isScrollingWhileRefreshingEnabled());
		mPullToRefreshListView.setSelected(true);
		mPullToRefreshListView.setMode(mPullToRefreshListView.getMode() == Mode.PULL_FROM_END ?Mode.PULL_FROM_END: Mode.PULL_FROM_END);
		mPullToRefreshListView.setShowIndicator(false);

		store_producit_listview = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if(Networking.isNetworkAvailable(getActivity())) {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					else {
						new LoadProductsTask().execute();
					}
				} else {
					mPullToRefreshListView.onRefreshComplete();
					Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	//TODO///////////// loading product details
	private class LoadProductsTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected void onPreExecute() {

		}
		@Override
		protected Boolean doInBackground(String... params) {
			if(mAppPreferences.getUserid().equals("")) {
				mAppPreferences.saveUserid("0");
			}
			int count = productList.size();
			if(count !=0){
				fromPageCount = productList.get(count-1).getProduct_id();
			}
			String url = AppUrls.getCategoryProductUrl(subCatId, mAppPreferences.getRegionId(),
					mAppPreferences.getUserid(),fromPageCount,"10001");
			return getProdcutsResponse(url);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			store_producit_listview.setVisibility(View.VISIBLE);
			customProgressBar1.setVisibility(View.GONE);
			mPullToRefreshListView.onRefreshComplete();

			if(result) {
				try {
					if(adapter == null && !productList.isEmpty()) {
						adapter = new ProductsListAdapter(getActivity(), productList, mAppPreferences);
						store_producit_listview.setAdapter(adapter);
					} else {
						if(adapter != null){
							adapter.notifyDataSetChanged();						
						}
					}
					PauseOnScrollListener listener = new PauseOnScrollListener(MyApplication.imageLoader, true, true);
					store_producit_listview.setOnScrollListener(listener);
				} catch(Exception e){}
			} else {
				Toast.makeText(getActivity(), "No Products to display", Toast.LENGTH_SHORT).show();
			}
		}
	}

	//loading category response here
	private boolean getProdcutsResponse(String url){

		//Declaring the JSON object
		JSONObject jsonObject;
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
				Log.e("TAG","getProdcutsResponse mydata-->"+mydata);

				jsonObject = new JSONObject(mydata);	
				JSONArray productArrayObject = jsonObject.getJSONArray("GetSubcategoryProductDetailsResult");
				if(productArrayObject.length() != 0) {
					parseProductJsonResponse(productArrayObject);
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

	//parsing json values
	public void parseProductJsonResponse(JSONArray sub_mainArray) {
		int length = sub_mainArray.length();
		try {
			for (int i = 0; i <length; i++) {

				//image_width  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("Image1_Width"));
				//image_height  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("Image1_Height"));
				mProdcutModel.product_like_count  = sub_mainArray.getJSONObject(i).getInt("ProductLikes");
				mProdcutModel.product_desc = sub_mainArray.getJSONObject(i).getString("Product_Description");
				mProdcutModel.product_id  = sub_mainArray.getJSONObject(i).getString("Product_Id");
				mProdcutModel.product_url = sub_mainArray.getJSONObject(i).getString("Product_Image1");
				mProdcutModel.prodcut_name  = sub_mainArray.getJSONObject(i).getString("Product_Name");
				mProdcutModel.product_price  = sub_mainArray.getJSONObject(i).getString("Product_Price");
				mProdcutModel.store_name  = sub_mainArray.getJSONObject(i).getString("Store_Name");
				mProdcutModel.device_like_status = sub_mainArray.getJSONObject(i).getInt("UserLike");

				productList.add(mProdcutModel.addProdcuts(mProdcutModel));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
