package com.thisisswitch.storelove.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**Saving data across the application */
public class AppPreferences {

	private static final String APP_SHARED_PREFS = "com.thisisswitch.storelove"; 
	private SharedPreferences appSharedPrefs;
	private Editor prefsEditor;

	/** Saving data in shared preferences which will store life time of Application */
	public AppPreferences(Context context)
	{
		this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}

	public void deletePref() {
		this.prefsEditor.clear();
		this.prefsEditor.commit();
	}

	//======== title
	/** subcat title */
	public void saveProdcutTimestamp(String prodcutTimeStamp){

		prefsEditor.putString("saveProdcutTimeStamp", prodcutTimeStamp);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public String getProdcutTimestamp(){
		return appSharedPrefs.getString("saveProdcutTimeStamp", "");
	}

	//======== main category time stamp
	/** saving category time stamp  */
	public void saveCategoryTimeStamp(String categoryTimeStamp){

		prefsEditor.putString("categoryTimeStamp", categoryTimeStamp);
		prefsEditor.commit();
	}
	/** get category time stamp*/
	public String getCategoryTimeStamp(){
		return appSharedPrefs.getString("categoryTimeStamp", "");
	}

	//======== sub category time stamp
	/** saving category time stamp  */
	public void saveSubCategoryTimeStamp(String subCategoryTimeStamp){

		prefsEditor.putString("subCategoryTimeStamp", subCategoryTimeStamp);
		prefsEditor.commit();
	}
	/** get category time stamp*/
	public String getSubCategoryTimeStamp(){
		return appSharedPrefs.getString("subCategoryTimeStamp", "");
	}

	//======== push reg id
	/** subcat title */
	public void savePushRegid(String pusgregid){

		prefsEditor.putString("pusgregid", pusgregid);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public String getPusRegId(){
		return appSharedPrefs.getString("pusgregid", "");
	}

	//======== splash state
	/** subcat title */
	public void saveSplashState(int state){
		prefsEditor.putInt("saveSplashState", state);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public int getSplashState(){
		return appSharedPrefs.getInt("saveSplashState", 0);
	}

	//======== title
	/** subcat title */
	public void saveStoreTimestamp(String storeTimeStamp){

		prefsEditor.putString("saveStoreTimestamp", storeTimeStamp);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public String getStoreTimestamp(){
		return appSharedPrefs.getString("saveStoreTimestamp", "");
	}

	//======== title
	/** subcat title */
	public void saveRegionsTimestamp(String regionTimeStamp){

		prefsEditor.putString("regionTimeStamp", regionTimeStamp);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public String getRegionsTimestamp() {
		return appSharedPrefs.getString("regionTimeStamp", "");
	}
	//======== title
	/** subcat title */
	public void saveDeviceId(String pusgregid){

		prefsEditor.putString("saveDeviceId", pusgregid);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public String getDeviceId(){
		return appSharedPrefs.getString("saveDeviceId", "");
	}

	//======== username
	/**saving username */
	public void saveUserName(String username){

		prefsEditor.putString("saveUserName", username);
		prefsEditor.commit();
	}
	/** get username*/
	public String getUserName(){
		return appSharedPrefs.getString("saveUserName", "");
	}

	//======== saving user id after login success
	/** subcat title */
	public void saveUserid(String userId){

		prefsEditor.putString("userId", userId);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public String getUserid(){
		return appSharedPrefs.getString("userId", "0");
	}

	//======== saving user social or not
	/** subcat title */
	public void saveUserSocialid(String social){

		prefsEditor.putString("social", social);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public String getSocialid(){
		return appSharedPrefs.getString("social", "");
	}

	//======== image url
	/** subcat title */
	public void saveUserImageUrl(String imageUrl){

		prefsEditor.putString("imageUrl", imageUrl);
		prefsEditor.commit();
	}
	/** get subcat title*/
	public String getUserImageUrl(){
		return appSharedPrefs.getString("imageUrl", "");
	}

	//======== Region Id
	/** region(location) id */
	public void saveRegionId(String regionId){

		prefsEditor.putString("regionId", regionId);
		prefsEditor.commit();
	}
	/** get region(location) id*/
	public String getRegionId(){
		return appSharedPrefs.getString("regionId", "");
	}

	//======== Region Id
	/** region(location) id */
	public void savePreviousRegionId(String regionId){

		prefsEditor.putString("savePreviousRegionId", regionId);
		prefsEditor.commit();
	}
	/** get region(location) id*/
	public String getPreviousRegionId(){
		return appSharedPrefs.getString("savePreviousRegionId", "");
	}

	/** region(location) id */
	public void saveRegionName(String regionName){

		prefsEditor.putString("regionName", regionName);
		prefsEditor.commit();
	}
	/** get region(location) id*/
	public String getRegionName(){
		return appSharedPrefs.getString("regionName", "");
	}
	//======== from count 
	/** from count*/
	public void saveFromCount(String fromCount){

		prefsEditor.putString("fromCount", fromCount);
		prefsEditor.commit();
	}
	/** get from count*/
	public String getFromCount(){
		return appSharedPrefs.getString("fromCount", "0");
	}

	//======== login type 
	/** saving login type FB,G+,notmal login*/
	public void saveLoginType(String loginType){

		prefsEditor.putString("loginType", loginType);
		prefsEditor.commit();
	}
	/** get from count*/
	public String getLoginType(){
		return appSharedPrefs.getString("loginType", "");
	}

	//======== latitude and longitude
	/** saving login type FB,G+,notmal login*/
	public void saveLatLang(String latLang){

		prefsEditor.putString("saveLatLang", latLang);
		prefsEditor.commit();
	}
	/** get from count*/
	public String getLatLang(){
		return appSharedPrefs.getString("saveLatLang", "");
	}
}

