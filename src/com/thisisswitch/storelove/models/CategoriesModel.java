package com.thisisswitch.storelove.models;

import java.util.ArrayList;

import org.json.JSONArray;

public class CategoriesModel {

	public String main_cat_id = "";
	public String main_cat_name = "";
	public String main_cat_imageurl = "";
	public String main_cat_isspecial = "";

	public String sub_cat_name = "";
	public String sub_cat_id = "";

	public CategoriesModel(){
	}

	/**
	 * @param main_cat_id
	 * @param main_cat_name
	 * @param mina_cat_imageurl
	 * @param main_cat_isspecial
	 */
	public CategoriesModel(String main_cat_id, String main_cat_name,
			String main_cat_imageurl, String main_cat_isspecial) {

		this.main_cat_id = main_cat_id;
		this.main_cat_name = main_cat_name;
		this.main_cat_imageurl = main_cat_imageurl;
		this.main_cat_isspecial = main_cat_isspecial;
	}

	/**
	 * @param main_cat_id
	 * @param sub_cat_name
	 * @param sub_cat_id
	 */
	public CategoriesModel(String main_cat_id, String sub_cat_name,
			String sub_cat_id) {

		this.main_cat_id = main_cat_id;
		this.main_cat_name = sub_cat_name;
		this.sub_cat_id = sub_cat_id;
	}

	/** */
	public CategoriesModel addSubCategories(CategoriesModel mCategoriesModel){
		return new CategoriesModel(mCategoriesModel.main_cat_id, 
				mCategoriesModel.main_cat_name,
				mCategoriesModel.sub_cat_id);
	}

	/** */
	public CategoriesModel addCategories(CategoriesModel mCategoriesModel){
		return new CategoriesModel(mCategoriesModel.main_cat_id, mCategoriesModel.main_cat_name,
				mCategoriesModel.main_cat_imageurl, mCategoriesModel.main_cat_isspecial);
	}

	/**
	 * @return the main_cat_id
	 */
	public String getMain_cat_id() {
		return main_cat_id;
	}

	/**
	 * @param main_cat_id the main_cat_id to set
	 */
	public void setMain_cat_id(String main_cat_id) {
		this.main_cat_id = main_cat_id;
	}

	/**
	 * @return the main_cat_name
	 */
	public String getMain_cat_name() {
		return main_cat_name;
	}

	/**
	 * @param main_cat_name the main_cat_name to set
	 */
	public void setMain_cat_name(String main_cat_name) {
		this.main_cat_name = main_cat_name;
	}

	/**
	 * @return the main_cat_imageurl
	 */
	public String getMain_cat_imageurl() {
		return main_cat_imageurl;
	}

	/**
	 * @param main_cat_imageurl the mina_cat_imageurl to set
	 */
	public void setMain_cat_imageurl(String mina_cat_imageurl) {
		this.main_cat_imageurl = mina_cat_imageurl;
	}

	/**
	 * @return the main_cat_isspecial
	 */
	public String getMain_cat_isspecial() {
		return main_cat_isspecial;
	}

	/**
	 * @param main_cat_isspecial the main_cat_isspecial to set
	 */
	public void setMain_cat_isspecial(String main_cat_isspecial) {
		this.main_cat_isspecial = main_cat_isspecial;
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
	 * @return the sub_cat_id
	 */
	public String getSub_cat_id() {
		return sub_cat_id;
	}

	/**
	 * @param sub_cat_id the sub_cat_id to set
	 */
	public void setSub_cat_id(String sub_cat_id) {
		this.sub_cat_id = sub_cat_id;
	}

	//TODO parsing and adding main ctegories in list
	public ArrayList<CategoriesModel> parseCategories(JSONArray categoriesArray){
		ArrayList<CategoriesModel> categoriesList =  new ArrayList<CategoriesModel>();

		try {
			if(categoriesArray.length() != 0) {
				int length = categoriesArray.length();

				for (int i = 0; i <length; i++) {

					main_cat_name  = categoriesArray.getJSONObject(i).getString("Category_Name");
					main_cat_imageurl = categoriesArray.getJSONObject(i).getString("ImagePath");
					main_cat_isspecial  = categoriesArray.getJSONObject(i).getString("IsSpecial");
					main_cat_id = categoriesArray.getJSONObject(i).getString("MianCategory_Id");

					categoriesList.add(addCategories(this));
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return categoriesList;
	}

	//TODO parsing and adding subctegories in list
	public ArrayList<CategoriesModel> parseSubCategories(JSONArray categoriesArray){
		ArrayList<CategoriesModel> subCategoriesList =  new ArrayList<CategoriesModel>();

		try {
			if(categoriesArray.length() != 0) {
				int length = categoriesArray.length();

				for (int i = 0; i <length; i++) {

					main_cat_name  = categoriesArray.getJSONObject(i).getString("Category_Name");
					main_cat_id = categoriesArray.getJSONObject(i).getString("MainCategory_Id");
					sub_cat_id = categoriesArray.getJSONObject(i).getString("SubCategory_Id");

					subCategoriesList.add(addSubCategories(this));
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return subCategoriesList;
	}

}
