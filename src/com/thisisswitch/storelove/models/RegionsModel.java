package com.thisisswitch.storelove.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegionsModel {

	public String region_id = "";
	public String region_name = "";
	public String msg = "";


	public RegionsModel(){
	}


	/**
	 * @param region_id
	 * @param region_name
	 * @param msg
	 */
	public RegionsModel(String region_id, String region_name) {
		this.region_id = region_id;
		this.region_name = region_name;
	}

	private RegionsModel addRegions(RegionsModel mRegionsModel){
		return new RegionsModel(mRegionsModel.region_id,mRegionsModel.region_name);
	}
	
	/**
	 * @return the region_id
	 */
	public String getRegion_id() {
		return region_id;
	}


	/**
	 * @param region_id the region_id to set
	 */
	public void setRegion_id(String region_id) {
		this.region_id = region_id;
	}


	/**
	 * @return the region_name
	 */
	public String getRegion_name() {
		return region_name;
	}


	/**
	 * @param region_name the region_name to set
	 */
	public void setRegion_name(String region_name) {
		this.region_name = region_name;
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

	public ArrayList<RegionsModel>  parseRegionsJson(String result){
		ArrayList<RegionsModel>  regionsList = new ArrayList<RegionsModel>();

		try{
			JSONObject mainObject = new JSONObject(result);
			JSONObject regionsObject = mainObject.getJSONObject("GetRegionsResult");
			JSONArray regionArray = regionsObject.getJSONArray("GET_REGIONS");
			int count = regionArray.length();
			if(count != 0) {
				for(int i=0; i<count; i++) {
					
					msg = regionArray.getJSONObject(i).getString("Msg");
					region_id = regionArray.getJSONObject(i).getString("Region_Id");
					region_name = regionArray.getJSONObject(i).getString("Region_Name");
					
					regionsList.add(addRegions(this));
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		return regionsList;
	}
}

