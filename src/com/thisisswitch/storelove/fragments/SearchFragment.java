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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.adapter.ProductsListAdapter;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;
import com.thisisswitch.storelove.widgets.CustomProgressBar;

public class SearchFragment extends SherlockFragment{

	private String TAG = "SearchFragment";

	private ListView search_listview = null;
	private PullToRefreshListView mPullToRefreshListView;
	private CustomProgressBar customProgressBar1;
	private RelativeLayout progress_layout;
	private EditText search_editText;
	private TextView search_textView;
	private ImageView cross_imageView1;

	private ProductsListAdapter adapter = null;
	private ProdcutModel mProdcutModel = null;
	private ArrayList<ProdcutModel> productList = null;
	private AppPreferences mAppPreferences = null;
	private String searchTag = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		productList = new ArrayList<ProdcutModel>();
		mAppPreferences = new AppPreferences(getActivity());
		mProdcutModel = new ProdcutModel();
	}
	//second this view called
	// Initialize all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		View view = inflater.inflate(R.layout.fragment_search_products, container, false);

		mPullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.search_listview);
		cross_imageView1 = (ImageView)view.findViewById(R.id.cross_imageView1);
		search_textView = (TextView)view.findViewById(R.id.search_textView);
		search_editText = (EditText)view.findViewById(R.id.search_editText);
		progress_layout = (RelativeLayout)view.findViewById(R.id.progress_layout);
		customProgressBar1 = (CustomProgressBar)view.findViewById(R.id.customProgressBar1);

		//mgr = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		pullToRefreshChangedView();
		customProgressBar1.setVisibility(View.GONE);
		search_listview.setVisibility(View.GONE);
		search_textView.setText("Search for products based on Product Name, Store Name, & Categories");
		//displayStoreDetails();

		cross_imageView1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				search_editText.setText("");
				search_textView.setVisibility(View.VISIBLE);
				search_textView.setText("Search for products based on Product Name, Store Name, & Categories");
			}
		});

		//TODO
		search_editText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

					if(Networking.isNetworkAvailable(getActivity())) {

						searchTag = search_editText.getText().toString().trim();
						customProgressBar1.setVisibility(View.VISIBLE);
						search_textView.setVisibility(View.GONE);
						customProgressBar1.execute();

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new LoadProductsTask().execute();
						}
					} else {
						Toast.makeText(getActivity(), Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
					}
				}    
				return false;
			}
		});
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
				if(Networking.isNetworkAvailable(getActivity())) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						new LoadProductsTask().execute();
					}
				} else {
					mPullToRefreshListView.onRefreshComplete();
					Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	//	//displaying only seleted store's products
	//	private void displayStoreDetails() {
	//		//		//displaying direct search result if navigating from product details page
	//		//		if(extras != null) {
	//		//			cross_imageView1.setVisibility(View.GONE);
	//		//			search_editText.setVisibility(View.GONE);
	//		//
	//		//		} else { //asking user to enter keys for search
	//		//			search_editText.setVisibility(View.VISIBLE);
	//		//		}
	//		//clear old values for product search by using tag
	//		if(productList.size() != 0){
	//			productList = new ArrayList<ProdcutModel>();
	//			if(adapter  != null){
	//				adapter = null;
	//			}
	//		}
	//	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(getActivity()).activityStart(getActivity()); 
	}
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());  
	}

	//loading products based on search query 
	private class LoadProductsTask extends AsyncTask<String, String, Boolean> {

		String fromCount = "0";
		@Override
		protected void onPreExecute() {

		}
		@Override
		protected Boolean doInBackground(String... params) {
			if(mAppPreferences.getUserid().equals("")) {
				mAppPreferences.saveUserid("0");
			}
			//taking product id for loading next products
			int count = productList.size();
			if(count !=0) {
				fromCount = productList.get(count-1).getProduct_id();
			}
			String url = AppUrls.getSearchUrl(fromCount, mAppPreferences.getRegionId(), mAppPreferences.getUserid(), searchTag, "10001");
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
				try {
					if(adapter == null && !productList.isEmpty()) {//adding new items 
						adapter = new ProductsListAdapter(getActivity(), productList, mAppPreferences);
						search_listview.setAdapter(adapter);
					} else {
						if(adapter != null){ //updated old adapter with new values
							adapter.notifyDataSetChanged();						
						}
					}
					PauseOnScrollListener listener = new PauseOnScrollListener(MyApplication.imageLoader, true, true);
					search_listview.setOnScrollListener(listener);
				} catch(Exception e){}
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
			Log.e("TAG","getProdcutsResponse mydata-->"+mydata.length());
			if(mydata.length() != 0) {

				jsonObject = new JSONObject(mydata);	
				JSONArray productArrayObject = jsonObject.getJSONArray("GetFeaturedProductsSearchResult");
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
							Log.e("TAG","mProdcutModel.product_id -->"+mProdcutModel.product_id );
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
