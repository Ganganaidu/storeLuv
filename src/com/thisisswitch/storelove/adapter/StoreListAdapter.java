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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.models.StoreModel;
import com.thisisswitch.storelove.preferences.AppPreferences;

public class StoreListAdapter extends BaseAdapter  {

	//private static final String TAG = "StoreListAdapter";
	Activity mContext;
	LayoutInflater inflater;

	ArrayList<StoreModel> storeList;
	AppPreferences mAppPreferences ; 
	ImageLoadingListener animateFirstListener;

	//for item animation positions
	int mLastPosition = -1;
	Animation zoomin, zoomout; //declared as public
	DisplayImageOptions options ;
	static boolean firstDisplay = false;
	ViewHolder mViewHolder = null;
	//ImageView imageView1;

	public StoreListAdapter(Activity context,ArrayList<StoreModel> storeList) {

		animateFirstListener = new AnimateFirstDisplayListener();
		inflater = LayoutInflater.from(context) ;
		this.mContext = context;
		this.storeList = storeList;

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
			convertView = inflater.inflate(R.layout.row_stores_list,null);

			mViewHolder.store_imageView = (ImageView) convertView .findViewById(R.id.store_imageView);
			mViewHolder.store_name_textView = (TextView) convertView .findViewById(R.id.store_name_textView);
			mViewHolder.like_count_textView = (TextView) convertView .findViewById(R.id.like_count_textView);

			//mViewHolder.imageView1.setTag(mViewHolder);
			convertView.setTag(mViewHolder);
		} else {
			// Reuse existing row view
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		//		mViewHolder.store_imageView.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//
		////				Intent intent = new Intent(mContext,ProductDetailsActivity.class);
		////				intent.putExtra("product_id", storeList.get(position).getStore_id());
		////				mContext.startActivity(intent);
		////				mContext.overridePendingTransition(R.anim.pull_in, R.anim.zoom_out);
		//			}
		//		});

		mViewHolder.store_name_textView.setText(storeList.get(position).getStore_name());
		mViewHolder.like_count_textView.setText(storeList.get(position).getStore_like_count()+" followers ");
		
		MyApplication.imageLoader.displayImage(storeList.get(position).getStore_imge_url(), mViewHolder.store_imageView, MyApplication.options, animateFirstListener);
		
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
		ImageView store_imageView;
		TextView store_name_textView,like_count_textView;
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				firstDisplay = !displayedImages.contains(imageUri);
				//Log.d(TAG,"firstDisplay-->"+firstDisplay);

				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				} 
			}
		}
	}
}
