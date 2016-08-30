package com.thisisswitch.storelove.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.models.RegionsModel;

public class RegionListAdapter extends BaseAdapter {

	private LayoutInflater inflate;
	private ArrayList<RegionsModel> regionList ;

	public RegionListAdapter(Activity activity,ArrayList<RegionsModel> regionList ){
		inflate = LayoutInflater.from(activity);
		this.regionList = regionList;
	}

	@Override
	public int getCount() {
		return regionList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return regionList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unnecessary calls
		// to findViewById() on each row.
		ViewHolder holder = null;

		// When convertView is not null, we can reuse it directly, there is no need
		// to re inflate it. We only inflate a new View when the convertView supplied
		// by ListView is null.
		if(convertView == null) {

			convertView = inflate.inflate(R.layout.row_sub_categories, null); 
			holder = new ViewHolder();
			holder.slde_row_textView1 = (TextView) convertView.findViewById(R.id.slidemenu_textView1);		
			convertView.setTag(holder);	
		}
		else{
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder=(ViewHolder)convertView.getTag();
		}
		holder.slde_row_textView1.setText(regionList.get(position).getRegion_name());
		return convertView;
	}

	//Holds the views
	private class ViewHolder{			
		TextView slde_row_textView1;
	}
}
