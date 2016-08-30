package com.thisisswitch.storelove;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.thisisswitch.storelove.adapter.ProductsDetailsViewPageAdapter;
import com.thisisswitch.storelove.fragments.ProductDetailsFragment;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.services.SendLikesToserverTask;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;
import com.thisisswitch.storelove.widgets.CustomProgressBar;
import com.viewpagerindicator.CirclePageIndicator;

public class ProductDetailsActivity extends SherlockFragmentActivity 
implements OnClickListener,ProductDetailsFragment.ProductOnclickListener {

	private ViewPager mViewPager = null;
	private CirclePageIndicator indicator = null;
	private CustomProgressBar customProgressBar1;
	private RelativeLayout help_layout;

	private ArrayList<String> productUrlsList = null;
	private ProdcutModel mProdcutModel = null;
	//private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prodcut_details);

		mProdcutModel = new ProdcutModel();
		//mAppPreferences = new AppPreferences(this);
		productUrlsList = new ArrayList<String>();

		//getSupportActionBar().hide();
		getSupportActionBar().setTitle("Hyderabad");
		//getSupportActionBar().setIcon(icon);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		help_layout  = (RelativeLayout)findViewById(R.id.help_layout);
		customProgressBar1  = (CustomProgressBar)findViewById(R.id.customProgressBar1);
		mViewPager = (ViewPager)findViewById(R.id.pager);
		indicator = (CirclePageIndicator)findViewById(R.id.indicator); 

		Bundle extras = getIntent().getExtras();
		if(extras != null) {

			mProdcutModel.product_id = extras.getString("product_id");
			mProdcutModel.product_like_count =extras.getInt("totalproducts");
			mProdcutModel.device_like_status = extras.getInt("likestatus");
		}

		if(Networking.isNetworkAvailable(this)) {
			new LoadProductDetailsTask().execute();			
		} else {
			Toast.makeText(this, Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		//		Intent in = null;
		//		switch (v.getId()) {
		//		}
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
	public void onBackPressed(){
		super.onBackPressed();
		onBack() ;
	}
	/** */
	private void onBack() {
		finish();
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	//TODO///////////// loading prodcut details
	private class LoadProductDetailsTask extends AsyncTask<String, String, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return getStoreResponse(AppUrls.getProductDetailsUrl(mProdcutModel.product_id));
		}
		@Override
		protected void onPostExecute(Boolean result) {

			customProgressBar1.setVisibility(View.GONE);
			help_layout.setVisibility(View.VISIBLE);

			if(result) {
				//ImageAdapter mAdapter = new ImageAdapter(this);
				ProductsDetailsViewPageAdapter mAdapter = new ProductsDetailsViewPageAdapter(getSupportFragmentManager(),productUrlsList,mProdcutModel);
				mViewPager.setAdapter(mAdapter);
				indicator.setViewPager(mViewPager);

				mViewPager.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
						return false;
					}
				});

				final float density = getResources().getDisplayMetrics().density;
				//indicator.setBackgroundColor(getResources().getColor(R.color.greyheader));
				indicator.setRadius(5 * density);
				//indicator.setPageColor(getResources().getColor(R.color.greyheaderend));
				indicator.setFillColor(getResources().getColor(android.R.color.white));
				indicator.setStrokeColor(getResources().getColor(android.R.color.black));
				indicator.setStrokeWidth(1 * density);

			} else {
				finish();
			}
		}
		@Override
		protected void onPreExecute() {

			customProgressBar1.setVisibility(View.VISIBLE);
			customProgressBar1.execute();
			help_layout.setVisibility(View.GONE);
		}
	}

	//loading category reponse here
	private boolean getStoreResponse(String url){

		Log.e("Pdetails","URL  "+url);
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
				JSONArray sub_mainArray = jsonObject.getJSONArray("GetProductDetailsResult");
				if(sub_mainArray.length() != 0) {

					mProdcutModel.mainCategoryName = sub_mainArray.getJSONObject(0).getString("Main_Category_Name");
					mProdcutModel.product_desc = sub_mainArray.getJSONObject(0).getString("Product_Description");
					mProdcutModel.product_id = sub_mainArray.getJSONObject(0).getString("Product_Id");
					mProdcutModel.prodcut_name = sub_mainArray.getJSONObject(0).getString("Product_Name");
					mProdcutModel.product_price = sub_mainArray.getJSONObject(0).getString("Product_Price");
					mProdcutModel.product_tags = sub_mainArray.getJSONObject(0).getString("Product_Tags");
					mProdcutModel.store_user_like = sub_mainArray.getJSONObject(0).getInt("StoreUserLike");
					mProdcutModel.store_address = sub_mainArray.getJSONObject(0).getString("Store_Address");
					mProdcutModel.store_eamilid = sub_mainArray.getJSONObject(0).getString("Store_Email_Id");
					mProdcutModel.store_id = sub_mainArray.getJSONObject(0).getString("Store_ID");
					mProdcutModel.store_lat_lang = sub_mainArray.getJSONObject(0).getString("Store_Location");
					mProdcutModel.store_mobile_number = sub_mainArray.getJSONObject(0).getString("Store_Mobile_No");
					mProdcutModel.store_name = sub_mainArray.getJSONObject(0).getString("Store_Name");
					mProdcutModel.sub_cat_name = sub_mainArray.getJSONObject(0).getString("Sub_Category_Name");

					String product_Image1 = sub_mainArray.getJSONObject(0).getString("Product_Image1");
					String product_Image2 = sub_mainArray.getJSONObject(0).getString("Product_Image2");
					String product_Image3 = sub_mainArray.getJSONObject(0).getString("Product_Image3");
					String product_Image4 = sub_mainArray.getJSONObject(0).getString("Product_Image4");

					if(product_Image1.length() > 0) {
						productUrlsList.add(product_Image1);
					}
					if(product_Image2.length() > 0) {
						productUrlsList.add(product_Image2);
					}
					if(product_Image3.length() > 0) {
						productUrlsList.add(product_Image3);
					}
					if(product_Image4.length() > 0) {
						productUrlsList.add(product_Image4);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void onLikeChanged(String url) {
		new SendLikesToserverTask(url).execute();
	}

	@Override
	public void onProductTagSearch(String tagName) {
		Intent in = new Intent(ProductDetailsActivity.this,SearchActivity.class);
		in.putExtra("tagName", tagName);
		startActivity(in);
	}
}