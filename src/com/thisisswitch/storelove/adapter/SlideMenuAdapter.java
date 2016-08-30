package com.thisisswitch.storelove.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.Globals;

public class SlideMenuAdapter extends BaseAdapter {

	private LayoutInflater inflate;
	AppPreferences mAppPreferences;

	public SlideMenuAdapter(Activity activity,AppPreferences mAppPreferences){
		inflate = LayoutInflater.from(activity);
		this.mAppPreferences = mAppPreferences;
	}

	@Override
	public int getCount() {
		return Globals.slide_menu_titles.length;
	}

	@Override
	public Object getItem(int arg0) {
		return Globals.slide_menu_titles[arg0];
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

			convertView = inflate.inflate(R.layout.row_slide_menu_layout, null); 
			holder = new ViewHolder();

			holder.slde_row_textView1 = (TextView) convertView.findViewById(R.id.slidemenu_textView1);		
			holder.slide_imageView = (ImageView) convertView.findViewById(R.id.slide_imageView);
			convertView.setTag(holder);	
		}
		else{
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder=(ViewHolder)convertView.getTag();
		}
//		if(position == 5) {
//			if(mAppPreferences.getUserImageUrl().length()>2) {
//				holder.slde_row_textView1.setText("Sign out");	
//			} else {
//				holder.slde_row_textView1.setText(Globals.slide_menu_titles[position]);	
//			}
//		} else {
			holder.slde_row_textView1.setText(Globals.slide_menu_titles[position]);
//		}
		holder.slide_imageView.setImageResource(Globals.slide_menu_icons[position]);
		//holder.slde_row_textView1.setCompoundDrawablesWithIntrinsicBounds(Globals.slide_menu_icons[position], 0, 0, 0);
		return convertView;
	}

	//Holds the views
	private class ViewHolder {			
		TextView slde_row_textView1;
		ImageView slide_imageView;
	}
}
