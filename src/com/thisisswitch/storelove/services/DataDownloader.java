package com.thisisswitch.storelove.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thisisswitch.storelove.listeners.OnDataChangeListeners;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.ACache;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;

public class DataDownloader {

	private static final String TAG = "DataDownloader";
	ProdcutModel mProdcutModel;
	AppPreferences mAppPreferences = null;
	//OnDataChangeListeners mOnDataChangeListeners = null;
	OnDataChangeListeners delegate = null;
	Context mContext;
	StringBuilder sb ;
	private ACache mCache;

	/** using this for loading all this details into database */
	public DataDownloader(Context context,OnDataChangeListeners delegate,AppPreferences mAppPreferences,ACache mCache){

		this.mContext = context;
		this.delegate = delegate;
		this.mAppPreferences = mAppPreferences;
		this.mCache =  mCache;
	}

	public void runProductsTask(){
		new LoadProductsTask().execute();		
	}
	
	public void runRegionsTask(){
		new LoadRegionsTask().execute();
	}

	//TODO///////////// loading prodcut details
	private class LoadProductsTask extends AsyncTask<String, String, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			if(mAppPreferences.getUserid().equals("")){
				mAppPreferences.saveUserid("0");
			}
			return getProdcutsResponse(AppUrls.getProductsUrl(Globals.PAGE_COUNT_VALUE, mAppPreferences.getFromCount(), 
					mAppPreferences.getRegionId(), mAppPreferences.getUserid(),"10001"));
		}
		@Override
		protected void onPostExecute(Boolean result) {

			if(result){
				delegate.onLoadingFinished("products",true);
			} else {
				delegate.onLoadingFinished("error",false);
			}
		}
		@Override
		protected void onPreExecute() {
			delegate.onLoadingStarted("products");
		}
	}

	//TODO///////////// loading prodcut details
	private class LoadRegionsTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {

			String time_stamp = mAppPreferences.getRegionsTimestamp();
			try {
				time_stamp = URLEncoder.encode(time_stamp,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return getRegionsResponse(AppUrls.getRegionsUrl(time_stamp));
		}
		@Override
		protected void onPostExecute(Boolean result) {

			if(result) {
				delegate.onLoadingFinished("regions",true);
			} else {
				delegate.onLoadingFinished("error",false);
			}
		}
		@Override
		protected void onPreExecute() {
			delegate.onLoadingStarted("regions");
		}
	}

	//loading category reponse here
	private boolean getProdcutsResponse(String url){

		Log.e(TAG," url-->"+ url);
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
				jsonObject = new JSONObject(mydata);	
				//JSONObject subcat_object = jsonObject.getJSONObject("GET_PRODUCTPageResult");
				JSONArray productArrayObject = jsonObject.getJSONArray("GetFeaturedProductsResult");
				if(productArrayObject.length() != 0) {
					mCache.put("products", productArrayObject);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//loading category reponse here
	private boolean getRegionsResponse(String url){
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
				mCache.put("regions", mydata);
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
