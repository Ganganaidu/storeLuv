package com.thisisswitch.storelove.fragments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.thisisswitch.storelove.MapActivity;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.adapter.ProductsListAdapter;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.models.StoreModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.services.SendLikesToserverTask;
import com.thisisswitch.storelove.socialnetwork.StorluvLoginActvity;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;
import com.thisisswitch.storelove.widgets.CustomProgressBar;

public class StoreProductsListFragment extends SherlockFragment implements OnClickListener{

	private ListView store_producit_listview;
	private ImageView store_imageView,store_call_imageView,
	store_webadd_imageView,store_mail_imageView,store_locate_imageView;
	private TextView store_name_textView,like_count_textView,follow_store_textView;
	private PullToRefreshListView mPullToRefreshListView;
	private RelativeLayout progress_bar_layout;
	private CustomProgressBar customProgressBar1;

	private ProdcutModel mProdcutModel;
	private ArrayList<ProdcutModel> productList;
	private AppPreferences mAppPreferences;
	private ProductsListAdapter adapter;
	private StoreModel mStoreModel;

	private String fromPageCount = "0";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppPreferences = new AppPreferences(getActivity());
		mProdcutModel = new ProdcutModel();
		mStoreModel = new StoreModel();
		productList = new ArrayList<ProdcutModel>();

	}
	//second this view called
	// Initialise all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		View view = inflater.inflate(R.layout.fragment_store_productslist, container, false);

		mPullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.store_producit_listview);
		customProgressBar1 = (CustomProgressBar)view.findViewById(R.id.customProgressBar1);
		store_imageView = (ImageView)view.findViewById(R.id.store_imageView);

		store_call_imageView = (ImageView)view.findViewById(R.id.store_call_imageView);
		store_webadd_imageView = (ImageView)view.findViewById(R.id.store_webadd_imageView);
		store_mail_imageView = (ImageView)view.findViewById(R.id.store_mail_imageView);
		store_locate_imageView = (ImageView)view.findViewById(R.id.store_locate_imageView);

		follow_store_textView = (TextView)view.findViewById(R.id.follow_store_textView);
		store_name_textView = (TextView)view.findViewById(R.id.store_name_textView);
		like_count_textView = (TextView)view.findViewById(R.id.like_count_textView);
		progress_bar_layout = (RelativeLayout)view.findViewById(R.id.progress_bar_layout);

		store_imageView.setVisibility(View.VISIBLE);
		follow_store_textView.setVisibility(View.VISIBLE);
		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		try {
			Bundle b = getArguments();
			mStoreModel = (StoreModel)b.getSerializable("storelist");
			//			Log.e("Store","mStoreModel "+mStoreModel.getStore_name());
			//			storeId =  b.getString("storeid");
			//			storeimageurl = b.getString("storeimageurl");
			//			storeLikeCount = b.getString("storeLikeCount");
		} catch (Exception e) {}

		pullToRefreshChangedView();
		upDateStoreCount();

		follow_store_textView.setOnClickListener(this);
		store_call_imageView.setOnClickListener(this);
		store_webadd_imageView.setOnClickListener(this);
		store_mail_imageView.setOnClickListener(this);
		store_locate_imageView.setOnClickListener(this);

		store_producit_listview.setVisibility(View.GONE);
		progress_bar_layout.setVisibility(View.VISIBLE);
		customProgressBar1.setVisibility(View.VISIBLE);
		customProgressBar1.execute();

		//displaying store image
		MyApplication.imageLoader.displayImage(mStoreModel.store_imge_url, store_imageView, MyApplication.options);

		if(Networking.isNetworkAvailable(getActivity())) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new LoadProductsTask().execute();
			}
		} else {
			Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onClick(View v) {

		Intent mIntent;
		switch (v.getId()) {

		case R.id.follow_store_textView:
			if(mAppPreferences.getUserid().equals("") || mAppPreferences.getUserid().equals("0")) {

				Intent in = new Intent(getActivity(),StorluvLoginActvity.class);
				startActivity(in);
				getActivity().overridePendingTransition(R.anim.pull_up, R.anim.pull_out);

			} else {
				Log.e("TAG", "mStoreModel.user_like_count "+mStoreModel.user_like_count);

				if(mStoreModel.user_like_count == 10002) {
					mStoreModel.store_like_count = mStoreModel.store_like_count+1; 
					mStoreModel.user_like_count = 10001;
				} else {
					if(mStoreModel.store_like_count  != 0) {
						mStoreModel.store_like_count = mStoreModel.store_like_count-1;						
					}
					mStoreModel.user_like_count = 10002;
				}
				upDateStoreCount();

				String storeLikeUrl = AppUrls.insertStoresLikesUrl(mStoreModel.store_id, mAppPreferences.getUserid(), String.valueOf(mStoreModel.user_like_count));
				if(com.thisisswitch.storelove.utils.Networking.isNetworkAvailable(getActivity())){
					new SendLikesToserverTask(storeLikeUrl).execute();
				} else {
					Toast.makeText(getActivity(), Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
				}				
			}
			break;
		case R.id.store_call_imageView:

			String number = "tel:" + mStoreModel.store_phone_number.trim();
			mIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number)); 
			startActivity(mIntent);

			break;

		case R.id.store_webadd_imageView:
			if(Networking.isNetworkAvailable(getActivity())) {

				mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thisisswitch.com"));
				startActivity(mIntent);

			} else {
				Toast.makeText(getActivity(), Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.store_mail_imageView:

			if(Networking.isNetworkAvailable(getActivity())){
				sendMail();
			} else {
				Toast.makeText(getActivity(), Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.store_locate_imageView:
			double lat ,lang ;
			if(Networking.isNetworkAvailable(getActivity())){

				if(mStoreModel.store_latlang_location.trim().length() >0){
					StringTokenizer st = new StringTokenizer(mStoreModel.store_latlang_location,",");
					while (st.hasMoreElements()) {

						lat = Double.parseDouble(st.nextElement().toString());
						lang = Double.parseDouble(st.nextElement().toString());

						Intent in = new Intent(getActivity(),MapActivity.class);
						in.putExtra("lat", lat);
						in.putExtra("long", lang);
						startActivity(in);
						getActivity().overridePendingTransition(R.anim.pull_up, R.anim.pull_out);
					}
				} else {
					Toast.makeText(getActivity(), "Location not available", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getActivity(), Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	//TODO
	private void upDateStoreCount(){

		store_name_textView.setText(mStoreModel.store_name);
		like_count_textView.setText(String.valueOf(mStoreModel.store_like_count)+" followers");

		if(mStoreModel.user_like_count == 10001) {
			follow_store_textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_following_oc, 0, 0);
		} else {
			follow_store_textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_following, 0, 0);
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
					} else {
						new LoadProductsTask().execute();
					}
				} else {
					mPullToRefreshListView.onRefreshComplete();
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
			String url = AppUrls.getStoreProductUrl(mStoreModel.getStore_id(), mAppPreferences.getUserid(),fromPageCount,"10001");
			return getProdcutsResponse(url);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			store_producit_listview.setVisibility(View.VISIBLE);
			progress_bar_layout.setVisibility(View.GONE);
			customProgressBar1.setVisibility(View.GONE);
			mPullToRefreshListView.onRefreshComplete();

			if(result) {
				try{
					if(adapter == null && !productList.isEmpty()) {
						adapter = new ProductsListAdapter(getActivity(), productList, mAppPreferences);
						store_producit_listview.setAdapter(adapter);
					} else {
						if(adapter != null) {
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

		Log.e("StoreProducts"," url-->"+ url);
		mProdcutModel = new ProdcutModel();
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
				JSONArray productArrayObject = jsonObject.getJSONArray("GetStoreProductsResult");

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

	//sending mail 
	private void sendMail() {

		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{mStoreModel.store_emailid});		  
		email.putExtra(Intent.EXTRA_SUBJECT, "Contact: "+mStoreModel.store_name);
		email.putExtra(Intent.EXTRA_TEXT, "This mail is sent from Storluv app.");
		email.setType("image/jpeg");
		//email.putExtra(Intent.EXTRA_STREAM, uri);

		PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> matches = pm.queryIntentActivities(email, 0);
		ResolveInfo best = null;
		for(ResolveInfo info : matches)
			if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
				best = info;
		if (best != null)
			email.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		startActivity(email);
	}
}
