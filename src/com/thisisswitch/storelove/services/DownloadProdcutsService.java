//package com.thisisswitch.storelove.services;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLEncoder;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import android.app.IntentService;
//import android.content.Intent;
//import android.util.Log;
//
//import com.thisisswitch.storelove.database.SQLHelper;
//import com.thisisswitch.storelove.models.ProdcutModel;
//import com.thisisswitch.storelove.utils.AppPreferences;
//import com.thisisswitch.storelove.utils.Globals;
//import com.thisisswitch.storelove.utils.Networking;
//
//public class DownloadProdcutsService extends IntentService{
//
//	private static final String TAG = "DownloadProdcutsService";
//	AppPreferences mAppPreferences = null;
//	ProdcutModel mProdcutModel = null;
//
//	String prodcutId = "";
//	String likeuUrl = "";
//	int row_id = 0;
//
//	public DownloadProdcutsService() {
//		super("DownloadProdcutsService");
//
//	}
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		mAppPreferences = new AppPreferences(this);
//		mProdcutModel = new ProdcutModel();
//	}
//
//	@Override
//	protected void onHandleIntent(Intent intent) {
//		if(Networking.isNetworkAvailable(getApplicationContext())) {
//			sendLikeStatusToserver();
//		} else {
//			stopSelf();
//		}
//	}
//
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//
//		stopSelf();
//		SQLHelper.getInstance().close();
//	}
//
//
//	private void sendLikeStatusToserver() {
//
//		String url = Globals.getProdcutsUrl("10", mAppPreferences.getMaxCount());
//		Log.d(TAG,"url-->"+url);
//		//Declaring the JSON object
//		JSONObject jsonObject;
//		//Declaring the URL connection object
//		URLConnection tc1;
//
//		try {
//			URL tableConnect = new URL(url);
//			tc1 = tableConnect.openConnection(); 
//			tc1.setReadTimeout(Globals.readTimeOut);
//			tc1.setConnectTimeout(Globals.readTimeOut);
//			BufferedReader input = new BufferedReader(new InputStreamReader(tc1.getInputStream()));
//			String line2;
//			String mydata = "";
//			//reading the content of the result
//			while ((line2 = input.readLine()) != null) {
//				//Gets all the characters and adding them into myData string
//				mydata = mydata + line2;
//			}
//
//			if(mydata.length() != 0) {
//
//				//Log.e(TAG,"getProdcutsResponse mydata-->"+mydata);
//				jsonObject = new JSONObject(mydata);	
//				JSONObject subcat_object = jsonObject.getJSONObject("GET_PRODUCTPageResult");
//				JSONArray sub_mainArray = subcat_object.getJSONArray("GET_PRODUCT");
//
//				int prodcutTimeStamp = Integer.parseInt(subcat_object.getString("MaxProductId"));
//				if(prodcutTimeStamp != 0){
//					prodcutTimeStamp = prodcutTimeStamp+1;
//					mAppPreferences.saveMaxCount(String.valueOf(prodcutTimeStamp));
//				} 
//
//				if(sub_mainArray.length() != 0){
//					//SQLHelper.getInstance().deleteProducts();
//					//JSONObject subcat_object = sub_mainArray.getJSONObject(0);
//					//JSONArray sub_catArray = subcat_object.getJSONArray("SubCategory");
//					int length = sub_mainArray.length();
//
//					for (int i = 0; i <length; i++) {
//
//						mProdcutModel.product_desc = sub_mainArray.getJSONObject(i).getString("PDescription");
//						mProdcutModel.product_url = sub_mainArray.getJSONObject(i).getString("PImage");
//						mProdcutModel.prodcut_name  = sub_mainArray.getJSONObject(i).getString("ProductName");
//						mProdcutModel.store_id = sub_mainArray.getJSONObject(i).getString("StoreId");
//						mProdcutModel.store_name  = sub_mainArray.getJSONObject(i).getString("StoreName");
//						mProdcutModel.product_price  = sub_mainArray.getJSONObject(i).getString("Price");
//						mProdcutModel.product_id  = sub_mainArray.getJSONObject(i).getString("Product_Id");
//
//						SQLHelper.getInstance().insertinto_produtTable(mProdcutModel);
//					}
//					sendLikeStatusToserver();
//				} else {
//					//stopSelf();
//					getProductLikesResponse();
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	//loading category reponse here
//	private boolean getProductLikesResponse(){
//
//		String time_stamp = mAppPreferences.getLikeTimestamp();
//		try {
//			time_stamp = URLEncoder.encode(time_stamp,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		String url = Globals.getLikeProdcutUrl(time_stamp, mAppPreferences.getDeviceId());
//
//		//Declaring the JSON object
//		JSONObject jsonObject;
//		//Declaring the URL connection object
//		URLConnection tc1;
//
//		try{
//
//			URL tableConnect = new URL(url);
//			tc1 = tableConnect.openConnection(); 
//			tc1.setReadTimeout(Globals.readTimeOut);
//			tc1.setConnectTimeout(Globals.readTimeOut);
//			BufferedReader input = new BufferedReader(new InputStreamReader(tc1.getInputStream()));
//			String line2;
//			String mydata = "";
//			//reading the content of the result
//			while ((line2 = input.readLine()) != null) {
//				//Gets all the characters and adding them into myData string
//				mydata = mydata + line2;
//			}
//			if(mydata.length() != 0) {
//
//				//Log.e(TAG,"getProductLikesResponse mydata-->"+mydata);
//				jsonObject = new JSONObject(mydata);	
//				JSONObject subcat_object = jsonObject.getJSONObject("GetProductLikesResult");
//				JSONArray sub_mainArray = subcat_object.getJSONArray("PLikes");
//
//				String likeTimeStamp = subcat_object.getString("SDT");
//				mAppPreferences.saveLikeTimestamp(likeTimeStamp);
//
//				if(sub_mainArray.length() != 0){
//					int length = sub_mainArray.length();
//
//					for (int i = 0; i <length; i++) {
//
//						//String msg = sub_mainArray.getJSONObject(i).getString("Msg");
//						int product_like_count = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("ProductCount"));
//						int device_like_status  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("DeviceLikeStatus"));
//						String pr_id  = sub_mainArray.getJSONObject(i).getString("Product_Id");
//
//						SQLHelper.getInstance().updateLikeCount(pr_id,product_like_count,device_like_status,"0");
//					}
//					getProductLikesResponse();
//				} else {
//					stopSelf();
//				}
//			}
//
//		}catch (Exception e){
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//}
