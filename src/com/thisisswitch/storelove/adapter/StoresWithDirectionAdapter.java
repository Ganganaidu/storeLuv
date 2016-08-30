package com.thisisswitch.storelove.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.models.StoreModel;
import com.thisisswitch.storelove.preferences.AppPreferences;

public class StoresWithDirectionAdapter extends BaseAdapter  {

	//private static final String TAG = "StoreListAdapter";
	Activity mContext;
	LayoutInflater inflater;

	ArrayList<StoreModel> storeList;
	AppPreferences mAppPreferences ; 

	//for item animation positions
	int mLastPosition = -1;
	Animation zoomin, zoomout; //declared as public
	DisplayImageOptions options ;
	ViewHolder mViewHolder = null;
	//ImageView imageView1;

	public StoresWithDirectionAdapter(Activity context,ArrayList<StoreModel> storeList) {
		inflater = LayoutInflater.from(context) ;
		this.mContext = context;
		this.storeList = storeList;
		//http://wptrafficanalyzer.in/blog/showing-nearby-places-and-place-details-using-google-places-api-and-google-maps-android-api-v2/
	}

	@Override
	public int getCount() {
		return storeList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position,  View convertView, ViewGroup parent) {

		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.row_store_directions,null);

			mViewHolder.store_name_textView = (TextView) convertView .findViewById(R.id.store_name_textView);
			mViewHolder.store_address_textView  = (TextView) convertView .findViewById(R.id.store_address_textView);
			mViewHolder.distance_textView  = (TextView) convertView .findViewById(R.id.distance_textView);

			//mViewHolder.imageView1.setTag(mViewHolder);
			convertView.setTag(mViewHolder);
		} else {
			// Reuse existing row view
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		mViewHolder.store_name_textView.setText(storeList.get(position).getStore_name());
		mViewHolder.store_address_textView.setText(storeList.get(position).getStore_address());
		try{
			mViewHolder.distance_textView.setText(storeList.get(position).getStore_distance().substring(0, 4)+" Km");
		}catch(Exception e) {
			mViewHolder.distance_textView.setText(storeList.get(position).getStore_distance()+" Km");
		}

		//animate the item
		TranslateAnimation animation = null;
		if (position > mLastPosition) {
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 1.0f,Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(600);
			convertView.startAnimation(animation);
			mLastPosition = position;
		}
		return convertView;
	}

	public class ViewHolder {
		TextView store_name_textView;
		TextView store_address_textView;
		TextView distance_textView;
	}
}
