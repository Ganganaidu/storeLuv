package com.thisisswitch.storelove.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class ProdcutModel {

	public String product_url = "";
	public String product_desc = "";
	public String prodcut_name = "";
	public String product_type = "";
	public String product_price = "";
	public String product_tags = "";
	public String product_id = "";
	public int product_like_count = 0;
	public int device_like_status = 0;
	public String product_timeStamp = "";
	public int image_width = 0;
	public int image_height = 0;
	public int image_loaded_flag = 0;
	public String product_status = "";

	public String mainCategoryName = "";
	public String store_name = "";
	public String store_id = "";
	public String store_address = "";
	public String store_eamilid = "";
	public String store_lat_lang = "";
	public String store_mobile_number = "";
	public String sub_cat_name = "";
	public int store_user_like = 0;

	public String keyword_id = "";
	public String keyword_name = "";
	public String msg = "";

	public ProdcutModel(){}

	public ProdcutModel(String product_url, String product_desc,
			String prodcut_name, String product_type, String store_name,
			String store_id,String product_price,
			int product_like_count,String product_id,
			int device_like_status,int image_width,int image_height,
			int image_loaded_flag,String product_tags,int store_user_like) {

		this.product_url = product_url;
		this.product_desc = product_desc;
		this.prodcut_name = prodcut_name;
		this.product_type = product_type;
		this.store_name = store_name;
		this.store_id = store_id;
		this.product_price = product_price;
		this.product_like_count = product_like_count;
		this.product_id = product_id;
		this.device_like_status = device_like_status;
		this.image_width = image_width;
		this.image_height = image_height;
		this.image_loaded_flag = image_loaded_flag;
		this.product_tags = product_tags;
		this.store_user_like = store_user_like; 
	}

	public ProdcutModel addProdcuts(ProdcutModel mProdcutModel){
		return new ProdcutModel(mProdcutModel.product_url,mProdcutModel.product_desc,
				mProdcutModel.prodcut_name,mProdcutModel.product_type,mProdcutModel.store_name,
				mProdcutModel.store_id,mProdcutModel.product_price,
				mProdcutModel.product_like_count,mProdcutModel.product_id,
				mProdcutModel.device_like_status,mProdcutModel.image_width,
				mProdcutModel.image_height,mProdcutModel.image_loaded_flag,
				mProdcutModel.product_tags,mProdcutModel.store_user_like);
	}

	public String getProduct_price() {
		return product_price;
	}

	public void setProduct_price(String product_price) {
		this.product_price = product_price;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public int getProduct_like_count() {
		return product_like_count;
	}

	public void setProduct_like_count(int product_like_count) {
		this.product_like_count = product_like_count;
	}

	/**
	 * @return the product_tags
	 */
	public String getProduct_tags() {
		return product_tags;
	}

	/**
	 * @param product_tags the product_tags to set
	 */
	public void setProduct_tags(String product_tags) {
		this.product_tags = product_tags;
	}

	public int getDevice_like_status() {
		return device_like_status;
	}

	public void setDevice_like_status(int device_like_status) {
		this.device_like_status = device_like_status;
	}

	public String getProdcut_price() {
		return product_price;
	}

	public void setProdcut_price(String product_price) {
		this.product_price = product_price;
	}

	public String getProduct_url() {
		return product_url;
	}
	public void setProduct_url(String product_url) {
		this.product_url = product_url;
	}
	public String getProduct_desc() {
		return product_desc;
	}
	public void setProduct_desc(String product_desc) {
		this.product_desc = product_desc;
	}
	public String getProdcut_name() {
		return prodcut_name;
	}
	public void setProdcut_name(String prodcut_name) {
		this.prodcut_name = prodcut_name;
	}
	public String getProduct_type() {
		return product_type;
	}
	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getProduct_timeStamp() {
		return product_timeStamp;
	}

	public void setProduct_timeStamp(String product_timeStamp) {
		this.product_timeStamp = product_timeStamp;
	}

	public int getImage_width() {
		return image_width;
	}

	public void setImage_width(int image_width) {
		this.image_width = image_width;
	}

	public int getImage_height() {
		return image_height;
	}

	public void setImage_height(int image_height) {
		this.image_height = image_height;
	}

	public int getImage_loaded_flag() {
		return image_loaded_flag;
	}

	public void setImage_loaded_flag(int image_loaded_flag) {
		this.image_loaded_flag = image_loaded_flag;
	}

	/**
	 * @return the product_status
	 */
	public String getProduct_status() {
		return product_status;
	}

	/**
	 * @param product_status the product_status to set
	 */
	public void setProduct_status(String product_status) {
		this.product_status = product_status;
	}

	/**
	 * @return the mainCategoryName
	 */
	public String getMainCategoryName() {
		return mainCategoryName;
	}

	/**
	 * @param mainCategoryName the mainCategoryName to set
	 */
	public void setMainCategoryName(String mainCategoryName) {
		this.mainCategoryName = mainCategoryName;
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
	 * @return the store_eamilid
	 */
	public String getStore_eamilid() {
		return store_eamilid;
	}

	/**
	 * @param store_eamilid the store_eamilid to set
	 */
	public void setStore_eamilid(String store_eamilid) {
		this.store_eamilid = store_eamilid;
	}

	/**
	 * @return the store_lat_lang
	 */
	public String getStore_lat_lang() {
		return store_lat_lang;
	}

	/**
	 * @param store_lat_lang the store_lat_lang to set
	 */
	public void setStore_lat_lang(String store_lat_lang) {
		this.store_lat_lang = store_lat_lang;
	}

	/**
	 * @return the store_mobile_number
	 */
	public String getStore_mobile_number() {
		return store_mobile_number;
	}

	/**
	 * @param store_mobile_number the store_mobile_number to set
	 */
	public void setStore_mobile_number(String store_mobile_number) {
		this.store_mobile_number = store_mobile_number;
	}

	/**
	 * @return the sub_cat_name
	 */
	public String getSub_cat_name() {
		return sub_cat_name;
	}

	/**
	 * @param sub_cat_name the sub_cat_name to set
	 */
	public void setSub_cat_name(String sub_cat_name) {
		this.sub_cat_name = sub_cat_name;
	}

	/**
	 * @return the store_user_like
	 */
	public int getStore_user_like() {
		return store_user_like;
	}

	/**
	 * @param store_user_like the store_user_like to set
	 */
	public void setStore_user_like(int store_user_like) {
		this.store_user_like = store_user_like;
	}

	/**
	 * @return the keyword_id
	 */
	public String getKeyword_id() {
		return keyword_id;
	}

	/**
	 * @param keyword_id the keyword_id to set
	 */
	public void setKeyword_id(String keyword_id) {
		this.keyword_id = keyword_id;
	}

	/**
	 * @return the keyword_name
	 */
	public String getKeyword_name() {
		return keyword_name;
	}

	/**
	 * @param keyword_name the keyword_name to set
	 */
	public void setKeyword_name(String keyword_name) {
		this.keyword_name = keyword_name;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	//parsing json values
	public ArrayList<ProdcutModel> parseProductJsonResponse(JSONArray sub_mainArray) {
		ArrayList<ProdcutModel>  productList = new ArrayList<ProdcutModel>();

		int length = sub_mainArray.length();
		try {
			for (int i = 0; i <length; i++) {

				//image_width  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("Image1_Width"));
				//image_height  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("Image1_Height"));
				product_like_count  = sub_mainArray.getJSONObject(i).getInt("ProductLikes");
				product_desc = sub_mainArray.getJSONObject(i).getString("Product_Description");
				product_id  = sub_mainArray.getJSONObject(i).getString("Product_Id");
				product_url = sub_mainArray.getJSONObject(i).getString("Product_Image1");
				prodcut_name  = sub_mainArray.getJSONObject(i).getString("Product_Name");
				product_price  = sub_mainArray.getJSONObject(i).getString("Product_Price");
				store_name  = sub_mainArray.getJSONObject(i).getString("Store_Name");
				device_like_status = sub_mainArray.getJSONObject(i).getInt("UserLike");

				productList.add(addProdcuts(this));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return productList;
	}
}
