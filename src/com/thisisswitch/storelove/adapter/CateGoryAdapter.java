package com.thisisswitch.storelove.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.models.CategoriesModel;

public class CateGoryAdapter extends BaseAdapter {

	private LayoutInflater inflate;
	private ArrayList<CategoriesModel> catList ;
	private ImageLoadingListener animateFirstListener;
	private static boolean firstDisplay = false;

	public CateGoryAdapter(Activity activity,ArrayList<CategoriesModel> catList ) {

		animateFirstListener = new AnimateFirstDisplayListener();
		inflate = LayoutInflater.from(activity);
		this.catList = catList;
	}

	@Override
	public int getCount() {
		return catList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return catList.get(arg0);
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

			convertView = inflate.inflate(R.layout.row_category_list, null); 
			holder = new ViewHolder();
			holder.slde_row_textView1 = (TextView) convertView.findViewById(R.id.slidemenu_textView1);		
			holder.categores_imageView = (ImageView) convertView.findViewById(R.id.categores_imageView);
			holder.trasaparent_background = (ImageView) convertView.findViewById(R.id.trasaparent_background);
			
			holder.trasaparent_background.setVisibility(View.VISIBLE);
			holder.categores_imageView.setVisibility(View.VISIBLE);
			
			convertView.setTag(holder);	
		}
		else{
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.slde_row_textView1.setText(catList.get(position).getMain_cat_name());
		MyApplication.imageLoader.displayImage(catList.get(position).getMain_cat_imageurl(), holder.categores_imageView, MyApplication.options, animateFirstListener);
		
		return convertView;
	}

	//Holds the views
	private class ViewHolder {			
		TextView slde_row_textView1;
		ImageView categores_imageView,trasaparent_background;
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				} 
			}
		}
	}
}
