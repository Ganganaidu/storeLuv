package com.thisisswitch.storelove.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.SearchActivity;

/** {@link SearchActivity} */
public class StoreSearchListAdapter extends BaseAdapter{

	Activity mContext;
	LayoutInflater inflater;
	ArrayList<String> storeNamesList;

	public StoreSearchListAdapter(Activity mContext,ArrayList<String> storeNamesList) {
		inflater = LayoutInflater.from(mContext) ;
		this.storeNamesList = storeNamesList;
	}

	@Override
	public int getCount() {
		return 0;
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
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder mViewHolder = null;
		if (convertView == null) {

			mViewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.row_staggered,null);
			mViewHolder.store_name_textView = (TextView) convertView .findViewById(R.id.store_name_textView);

		} else {
			// Reuse existing row view
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		mViewHolder.store_name_textView.setText(storeNamesList.get(position));
		return convertView;
	}

	public class ViewHolder {
		TextView store_name_textView;
	}
}
