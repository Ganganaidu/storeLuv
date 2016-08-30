package com.thisisswitch.storelove;

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
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.thisisswitch.storelove.adapter.ProductsListAdapter;
import com.thisisswitch.storelove.adapter.StoreSearchListAdapter;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;
import com.thisisswitch.storelove.widgets.CustomProgressBar;

public class SearchActivity extends SherlockFragmentActivity {

	protected static final String TAG = "SearchActivity";

	private RelativeLayout  serach_editext_layout;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView search_listview = null;
	private CustomProgressBar customProgressBar1;
	private RelativeLayout progress_layout;
	private TextView search_textView;

	ProductsListAdapter adapter = null;
	StoreSearchListAdapter mStoreSearchListAdapter = null;
	ProdcutModel mProdcutModel = null;
	ArrayList<ProdcutModel> productList = null;
	AppPreferences mAppPreferences = null;

	MixpanelAPI mMixpanel;
	private String tagName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_search_products);

		productList = new ArrayList<ProdcutModel>();
		mAppPreferences = new AppPreferences(this);
		mProdcutModel = new ProdcutModel();

		search_textView = (TextView)findViewById(R.id.search_textView);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.search_listview);

		progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);
		serach_editext_layout = (RelativeLayout) findViewById(R.id.serach_editext_layout);
		customProgressBar1 = (CustomProgressBar)findViewById(R.id.customProgressBar1);
		serach_editext_layout.setVisibility(View.GONE);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("Search");

		customProgressBar1.setVisibility(View.GONE);
		mMixpanel = MixpanelAPI.getInstance(this, Globals.MIXPANEL_TOKEN);
		pullToRefreshChangedView();
		displayStoreDetails();

		search_textView.setText("Search for products based on Product Name, Store Name, & Categories");
	}

	//TODO
	public void pullToRefreshChangedView() {
		// disabling scroll when pull
		mPullToRefreshListView.setScrollingWhileRefreshingEnabled(!mPullToRefreshListView.isScrollingWhileRefreshingEnabled());
		mPullToRefreshListView.setSelected(true);
		mPullToRefreshListView.setMode(mPullToRefreshListView.getMode() == Mode.PULL_FROM_END ?Mode.PULL_FROM_END: Mode.PULL_FROM_END);
		mPullToRefreshListView.setShowIndicator(false);

		search_listview = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if(Networking.isNetworkAvailable(SearchActivity.this)) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					else {
						new LoadProductsTask().execute();
					}
				} else {
					mPullToRefreshListView.onRefreshComplete();
					Toast.makeText(SearchActivity.this, Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) { 
			finish();
			overridePendingTransition (R.anim.open_main, R.anim.close_next);
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); 
	}
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  
	}

	//displaying only seleted store's products
	private void displayStoreDetails() {

		JSONObject props = new JSONObject();
		try {
			props.put("SEARCH", "");
			//props.put("Gender", "Female");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mMixpanel.track("SEARCH", props);

		Bundle extras = getIntent().getExtras();
		//displaying direct search result if navigating from product details page
		if(extras != null) {
			tagName =  extras.getString("tagName").trim();
			new LoadProductsTask().execute();
		} 
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onDestroy() {
		mMixpanel.flush();
		super.onDestroy();
	}

	private class LoadProductsTask extends AsyncTask<String, String, Boolean> {

		String fromCount = "0";
		@Override
		protected void onPreExecute() {

		}
		@Override
		protected Boolean doInBackground(String... params) {
			if(mAppPreferences.getUserid().equals("")){
				mAppPreferences.saveUserid("0");
			}
			//taking product id for loading next products
			int count = productList.size();
			if(count !=0){
				fromCount = productList.get(count-1).getProduct_id();
			}
			//
			if(tagName.length() != 0) {
				tagName = tagName.replace(" ", "%20");				
			}
			String url = AppUrls.getTagSearchUrl(fromCount, mAppPreferences.getRegionId(),
					mAppPreferences.getUserid(), tagName, "10001");
			return getProdcutsResponse(url);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			mPullToRefreshListView.onRefreshComplete();

			mPullToRefreshListView.setVisibility(View.VISIBLE);
			search_listview.setVisibility(View.VISIBLE);
			progress_layout.setVisibility(View.GONE);
			customProgressBar1.setVisibility(View.GONE);

			if(result) {
				if(adapter == null && !productList.isEmpty()) {//adding new items 
					adapter = new ProductsListAdapter(SearchActivity.this, productList, mAppPreferences);
					search_listview.setAdapter(adapter);
				} else {
					if(adapter != null) { //updated old adapter with new values
						adapter.notifyDataSetChanged();						
					}
				}
				PauseOnScrollListener listener = new PauseOnScrollListener(MyApplication.imageLoader, true, true);
				search_listview.setOnScrollListener(listener);

			} else {
				progress_layout.setVisibility(View.VISIBLE);
				search_textView.setVisibility(View.VISIBLE);
				search_textView.setText("Sorry! No results found. Please try a different search");
			}
		}
	}

	//TODO
	/** parsing data and saving in local arraylist
	 * @param url URL of the products
	 * @param 10001 for pulldown and 10002 for pull up side*/
	private boolean getProdcutsResponse(String url){

		Log.e(TAG," url-->"+ url);
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
			//Log.e("TAG","getProdcutsResponse mydata-->"+mydata.length());
			if(mydata.length() != 0) {

				jsonObject = new JSONObject(mydata);	
				JSONArray productArrayObject = jsonObject.getJSONArray("GetFeaturedProductsTagSearchResult");
				int length = productArrayObject.length();

				if(length != 0) {
					try {
						for (int i = 0; i <length; i++) {
							//image_width  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("Image1_Width"));
							//image_height  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("Image1_Height"));
							mProdcutModel.product_like_count  = productArrayObject.getJSONObject(i).getInt("ProductLikes");
							mProdcutModel.product_desc = productArrayObject.getJSONObject(i).getString("Product_Description");
							mProdcutModel.product_id  = productArrayObject.getJSONObject(i).getString("Product_Id");
							mProdcutModel.product_url = productArrayObject.getJSONObject(i).getString("Product_Image1");
							mProdcutModel.prodcut_name  = productArrayObject.getJSONObject(i).getString("Product_Name");
							mProdcutModel.product_price  = productArrayObject.getJSONObject(i).getString("Product_Price");
							mProdcutModel.store_name  = productArrayObject.getJSONObject(i).getString("Store_Name");
							mProdcutModel.device_like_status = productArrayObject.getJSONObject(i).getInt("UserLike");

							productList.add(mProdcutModel.addProdcuts(mProdcutModel));
							//Log.e("TAG","mProdcutModel.product_id -->"+mProdcutModel.product_id );
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
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
