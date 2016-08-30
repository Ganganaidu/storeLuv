package com.thisisswitch.storelove.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;

public class StoreModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String store_id = "";
	public String store_name = "";
	public String store_address = "";
	public String store_phone_number = "";
	public String store_emailid = "";
	public String store_timestamp = "";
	public String store_latlang_location = "";
	public String store_distance = "";
	public String description = "";
	public String store_imge_url = "";
	public int store_like_count = 0;
	public int user_like_count = 0;

	/**
	 * 
	 */
	public StoreModel() {
	}

	/**
	 * @param store_id
	 * @param store_name
	 * @param store_address
	 * @param store_phone_number
	 * @param store_emailid
	 * @param store_timestamp
	 * @param store_latlang_location
	 * @param description
	 * @param store_imge_url
	 */
	public StoreModel(String store_id, String store_name, String store_address,
			String store_phone_number, String store_emailid,
			String store_timestamp, String store_latlang_location,
			String description, String store_imge_url,
			int store_like_count,int user_like_count,String store_distance) {

		this.store_id = store_id;
		this.store_name = store_name;
		this.store_address = store_address;
		this.store_phone_number = store_phone_number;
		this.store_emailid = store_emailid;
		this.store_timestamp = store_timestamp;
		this.store_latlang_location = store_latlang_location;
		this.description = description;
		this.store_imge_url = store_imge_url;
		this.store_like_count = store_like_count;
		this.user_like_count = user_like_count;
		this.store_distance = store_distance;
	}

	public StoreModel addStores(StoreModel mStoreModel) {
		return new StoreModel(mStoreModel.store_id, mStoreModel.store_name, mStoreModel.store_address,
				mStoreModel.store_phone_number, mStoreModel.store_emailid,
				mStoreModel.store_timestamp, mStoreModel.store_latlang_location,
				mStoreModel.description, mStoreModel.store_imge_url,
				mStoreModel.store_like_count,mStoreModel.user_like_count,mStoreModel.store_distance);
	}

	public int getStore_like_count() {
		return this.store_like_count;
	}



	public void setStore_like_count(int store_like_count) {
		this.store_like_count = store_like_count;
	}



	public int getUser_like_count() {
		return this.user_like_count;
	}



	public void setUser_like_count(int user_like_count) {
		this.user_like_count = user_like_count;
	}



	/**
	 * @return the store_id
	 */
	public String getStore_id() {
		return store_id;
	}



	/**
	 * @param store_id the store_id to set
	 */
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}



	/**
	 * @return the store_name
	 */
	public String getStore_name() {
		return store_name;
	}



	/**
	 * @param store_name the store_name to set
	 */
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}



	/**
	 * @return the store_address
	 */
	public String getStore_address() {
		return store_address;
	}



	/**
	 * @param store_address the store_address to set
	 */
	public void setStore_address(String store_address) {
		this.store_address = store_address;
	}



	/**
	 * @return the store_phone_number
	 */
	public String getStore_phone_number() {
		return store_phone_number;
	}



	/**
	 * @param store_phone_number the store_phone_number to set
	 */
	public void setStore_phone_number(String store_phone_number) {
		this.store_phone_number = store_phone_number;
	}



	/**
	 * @return the store_emailid
	 */
	public String getStore_emailid() {
		return store_emailid;
	}



	/**
	 * @param store_emailid the store_emailid to set
	 */
	public void setStore_emailid(String store_emailid) {
		this.store_emailid = store_emailid;
	}



	/**
	 * @return the store_timestamp
	 */
	public String getStore_timestamp() {
		return store_timestamp;
	}



	/**
	 * @param store_timestamp the store_timestamp to set
	 */
	public void setStore_timestamp(String store_timestamp) {
		this.store_timestamp = store_timestamp;
	}



	/**
	 * @return the store_latlang_location
	 */
	public String getStore_latlang_location() {
		return store_latlang_location;
	}



	/**
	 * @param store_latlang_location the store_latlang_location to set
	 */
	public void setStore_latlang_location(String store_latlang_location) {
		this.store_latlang_location = store_latlang_location;
	}



	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}



	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}



	/**
	 * @return the store_imge_url
	 */
	public String getStore_imge_url() {
		return store_imge_url;
	}



	/**
	 * @param store_imge_url the store_imge_url to set
	 */
	public void setStore_imge_url(String store_imge_url) {
		this.store_imge_url = store_imge_url;
	}


	/**
	 * @return the store_distance
	 */
	public String getStore_distance() {
		return store_distance;
	}



	/**
	 * @param store_distance the store_distance to set
	 */
	public void setStore_distance(String store_distance) {
		this.store_distance = store_distance;
	}



	//TODO parsing jsonarray and storing in list
	public ArrayList<StoreModel> parseStoreDetails(JSONArray storeResultArray){
		ArrayList<StoreModel> storeList = new ArrayList<StoreModel>();

		try {
			if(storeResultArray.length() != 0) {
				int length = storeResultArray.length();

				for (int i = 0; i <length; i++) {

					description  = storeResultArray.getJSONObject(i).getString("Decription");
					store_emailid = storeResultArray.getJSONObject(i).getString("Email_Id");
					store_phone_number  = storeResultArray.getJSONObject(i).getString("MobileNo");
					store_like_count = storeResultArray.getJSONObject(i).getInt("StorLikeCount");
					store_address = storeResultArray.getJSONObject(i).getString("Store_Address");
					store_id = storeResultArray.getJSONObject(i).getString("Store_Id");
					store_distance = storeResultArray.getJSONObject(i).getString("Store_Distence");
					store_imge_url = storeResultArray.getJSONObject(i).getString("Store_Image");
					store_latlang_location = storeResultArray.getJSONObject(i).getString("Store_Location");
					store_name  = storeResultArray.getJSONObject(i).getString("Store_Name");
					user_like_count  = storeResultArray.getJSONObject(i).getInt("UserLike");

					storeList.add(addStores(this));
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return storeList;
	}
}
