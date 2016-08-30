package com.thisisswitch.storelove.utils;

import com.thisisswitch.storelove.models.UserRegModel;

public class AppUrls {

	public static String BASE_URL = "http://thisisswitch.com:8084/StoreLuvService/Service.svc/";
	//public static String BASE_URL = "http://192.168.0.171:8093/Service.svc/";

	public static String SPECIAL_CATEGORY = BASE_URL+"Get_MainCategory_Special";
	//GCM code
	public static String registerGCMInfo(String deviceid){
		String constructedURL = "";
		constructedURL = BASE_URL + "InsertPushDevice?Device_Id=" + deviceid + "&Device_Type=10002";
		return constructedURL;
	}

	/**URL for getting location names 
	 * @param timeStamp */
	public static String getRegionsUrl(String timeStamp){
		String url = BASE_URL+"GetRegions?SDT="+timeStamp;
		return url;
	} 

	/**URL for getting store names 
	 * @param timeStamp */
	public static String getStores(String regionId,String userId,String timeStamp){
		String url  = BASE_URL+"GetStores?regionid="+regionId+"&StoreUser_Id="+userId+"&SDT="+timeStamp;
		return url;
	}//GetStores?regionid={regionid}&StoreUser_Id={StoreUser_Id}&SDT={SDT}

	/**URL for getting main cetegories details 
	 * @param timeStamp */
	public static String getMainCaregoryUrl(String timeStamp){
		String url = BASE_URL+"Get_MainCategory?SDT="+timeStamp;
		return url;
	}

	/**URL for getting main cetegories details 
	 * @param timeStamp */
	public static String getSpecialCaregoryUrl(String timeStamp){
		String url = BASE_URL+"Get_MainCategory_Special";
		return url;
	}

	/**URL for getting sub categories details based on main category 
	 * @param main_catid
	 * @param timeStamp */
	public static String getSubCategoriesUrl(String main_catid,String timeStamp){
		String url = BASE_URL+"GetSubCate?maincatid="+main_catid+"&SDT="+timeStamp;
		return url;
	}

	/** creating URL for noraml registration*/
	public static String registerGeneralaUserUrl(UserRegModel mUserRegModel) {

		String url = BASE_URL+"InsertUserInfo?User_Name="+mUserRegModel.user_name+"&User_Pwd="+mUserRegModel.user_password
				+"&User_FName="+mUserRegModel.user_fname
				+"&User_LName="+mUserRegModel.user_lname
				+"&User_Mail="+mUserRegModel.user_mail
				+"&User_Phno="+mUserRegModel.user_phonenumber
				+"&User_Address="+mUserRegModel.user_location
				+"&User_Dob="+mUserRegModel.user_dob
				+"&User_Questions="+mUserRegModel.user_question
				+"&User_Ans="+mUserRegModel.user_answer;

		return url;
	}

	/** creating URL for soical reg user like Facebook and G+ registration*/
	public static String registerSocialUserUrl(UserRegModel mUserRegModel) {

		String url = BASE_URL+"InsertUserInfoDetails?SocialID=" +mUserRegModel.social_user_id
				+"&User_Name=" +mUserRegModel.user_name
				+"&User_Pwd="+mUserRegModel.user_password
				+"&User_FName="+mUserRegModel.user_fname
				+"&User_LName="+mUserRegModel.user_lname 
				+"&User_Mail="+mUserRegModel.user_mail
				+"&User_Phno="+mUserRegModel.user_phonenumber 
				+"&User_Address="+mUserRegModel.user_location
				+"&User_Dob=" +mUserRegModel.user_dob
				+"&User_Questions="+mUserRegModel.user_question 
				+"&User_Ans=" +mUserRegModel.user_answer
				+"&ImageUrl="+mUserRegModel.user_profile_image_url;
		return url;
	}

	/** Login url
	 * @param  userName
	 * @param password
	 * @param isSocial 10001 normal login
	 *  10002  social login for isSocial*/
	public static String userLogin(String userName,String password,String isSocial){
		String url = BASE_URL+"getUserLogin?UserName="+userName+"&Pwd="+password+"&IsSocial="+isSocial;
		return url;
	}

	/** creating URL for getting product details 
	 * @param pageCount Count for getting quantity 
	 * @param formCount last value of the array 
	 * @param Region_Id location id  
	 * @param userId  login (or) register user id 
	 * @param pageOrder 10001 for old products, and 10002 for updates*/
	public static String getProductsUrl(String pageCount,String fromCount,String regionId,String userId,String pageOrder) {
		String url = BASE_URL+"GetFeaturedProducts?PageCount="+pageCount+"&fromCount="+fromCount
				+"&Region_Id="+regionId+"&StoreUser_Id="+userId+"&ProductOrder="+pageOrder;
		//GetFeaturedProducts?PageCount={PageCount}&fromCount={fromCount}&Region_Id={Region_Id}&StoreUser_Id={StoreUser_Id}&ProductOrder={ProductOrder}
		return url;
	}

	/** Creating URL for product details
	 * @param productId id of the selected product*/
	public static String getProductDetailsUrl(String productId) {
		String url = BASE_URL+"GetProductDetails?Product_Id="+productId;
		//http://192.168.1.124:8085/Service.svc/GetProductDetails?Product_Id=1
		return url;
	}

	/** Creating URL for store product list
	 * @param productId id of the selected product
	 * @param storeUserId 
	 * @param fromCount 
	 * @param prodcutOrder 10001 for normal order and 10002 for reverse order*/
	public static String getStoreProductUrl(String store_id,String storeUserId,String fromCount,String prodcutOrder) {
		String url = BASE_URL+"GetStoreProducts?Store_ID="+store_id+"&StoreUser_Id="+storeUserId+
				"&PageCount="+Globals.PAGE_COUNT_VALUE+"&fromCount="+fromCount+"&ProductOrder="+prodcutOrder;
		//GetStoreProducts?Store_ID={Store_ID}&StoreUser_Id={StoreUser_Id}&PageCount={PageCount}&fromCount={fromCount}&ProductOrder={ProductOrder}
		return url;
	}

	/** Creating URL for category product list
	 * @param sub_catid id of the selected category*/
	public static String getCategoryProductUrl(String sub_catid,String region_id,String userId,String fromCount,String productOrder) {
		String url = BASE_URL+"GetSubcategoryProductDetails?SubCategory_Id="+sub_catid+"&Region_Id="+region_id+
				"&StoreUser_Id="+userId+"&PageCount="+Globals.PAGE_COUNT_VALUE+
				"&fromCount="+fromCount+"&ProductOrder="+productOrder;
		//GetSubcategoryProductDetails?SubCategory_Id={SubCategory_Id}&Region_Id={Region_Id}&StoreUser_Id={StoreUser_Id}&PageCount={PageCount}&fromCount={fromCount}&ProductOrder={ProductOrder}
		return url;
	}

	/** Creating URL for inserting stores likes
	 * @param storeId id of the selected store
	 * @param userId id of the user(Mandatory)
	 * @param likeStatus 10001 for like, 10002 for unlike*/
	public static String insertStoresLikesUrl(String storeId,String userId,String likeStatus){
		String url = BASE_URL+"StoreLike?Store_Id="+storeId+"&StoreUser_Id="+userId+"&LikeStatus="+likeStatus;
		return url;
		//StoreLike?Store_Id={Store_Id}&StoreUser_Id={StoreUser_Id}&LikeStatus={LikeStatus}
	}

	/** Creating URL for inserting stores likes
	 * @param productId id of the selected product
	 * @param userId id of the user(Mandatory)
	 * @param likeStatus 10001 for like, 10002 for unlike*/
	public static String insertProductLikesUrl(String productId,String userId,String likeStatus){
		String url = BASE_URL+"ProductLike?Product_Id="+productId+"&StoreUser_Id="+userId+"&LikeStatus="+likeStatus;
		return url;
		//ProductLike?Product_Id={Product_Id}&StoreUser_Id={StoreUser_Id}&LikeStatus={LikeStatus}
	}
	/** @param userId
	 *   @param fromCount
	 *   @param productOrder */
	public static String getUserLikedStores(String userId,String fromCount,String productOrder){
		String url = BASE_URL+"GetUserStoreLikeProducts?StoreUser_Id="+userId+
				"&PageCount="+Globals.PAGE_COUNT_VALUE+"&fromCount="+fromCount+"&ProductOrder="+productOrder;
		return url;
		//GetUserStoreLikeProducts?StoreUser_Id={StoreUser_Id}&PageCount={PageCount}&fromCount={fromCount}&ProductOrder={ProductOrder}
	}

	/** @param userId
	 *   @param fromCount
	 *   @param productOrder */
	public static String getUserLikedProducts(String userId,String fromCount,String productOrder){
		String url = BASE_URL+"GetUserLikeProducts?StoreUser_Id="+userId+
				"&PageCount="+Globals.PAGE_COUNT_VALUE+"&fromCount="+fromCount+"&ProductOrder="+productOrder;
		//GetUserLikeProducts?StoreUser_Id={StoreUser_Id}&PageCount={PageCount}&fromCount={fromCount}&ProductOrder={ProductOrder}
		return url;
	}

	/** Url for disaplying stores based on location 
	 * @param regionId location id
	 * @param userId login user id
	 * @param timeStamp time stamp for fresh updates
	 * @param latLang current location latitude and longitude */
	public static String getNearByStores(String regionId,String userId,String timeStamp,String latLang){
		String url = BASE_URL+"GetStoresLocation?regionid="+regionId+"&StoreUser_Id="+userId+"" +
				"&SDT="+timeStamp+"&StroreLatLang="+latLang;
		return url;
	}

	/** @param fromCount
	 *   @param regionId
	 *   @param userId 
	 *   @param productTag 
	 *   @param productOrder*/
	public static String getTagSearchUrl(String fromCount,String regionId,String userId,
			String productTag,String productOrder){
		String url = BASE_URL+"GetFeaturedProductsTags?PageCount="+Globals.PAGE_COUNT_VALUE+"&fromCount=" +fromCount+
				"&Region_Id="+regionId+"&StoreUser_Id="+userId+"&" +
				"Product_tag="+productTag+"&ProductOrder="+productOrder;
		return url;
	}
	
	/** @param fromCount
	 *   @param regionId
	 *   @param userId 
	 *   @param productTag 
	 *   @param productOrder*/
	public static String getSearchUrl(String fromCount,String regionId,String userId,String productTag,String productOrder){
		String url = BASE_URL+"GetFeaturedProductsSearch?PageCount="+Globals.PAGE_COUNT_VALUE+"&fromCount=" +fromCount+
				"&Region_Id="+regionId+"&StoreUser_Id="+userId+"&" +
				"Product_tag="+productTag+"&ProductOrder="+productOrder;
		return url;
	}
}
