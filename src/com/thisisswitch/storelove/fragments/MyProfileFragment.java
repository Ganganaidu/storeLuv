package com.thisisswitch.storelove.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.RegionsListActivity;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.ACache;
import com.thisisswitch.storelove.widgets.CircleImageView;

public class MyProfileFragment extends SherlockFragment implements OnClickListener{

	CircleImageView profile_imageView1;
	TextView username_textView1;
	Button location_button;

	AppPreferences mAppPreferences;
	ACache mCache = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppPreferences = new AppPreferences(getActivity());
		mCache = ACache.get(getActivity());
	}
	//second this view called
	// Initialize all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

		profile_imageView1 = (CircleImageView)view.findViewById(R.id.profile_imageView1);
		username_textView1 = (TextView)view.findViewById(R.id.username_textView1);
		location_button = (Button)view.findViewById(R.id.location_button);
		location_button.setVisibility(View.GONE);
		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		location_button.setOnClickListener(this);
		if(mAppPreferences.getUserImageUrl().length()>2){
			MyApplication.imageLoader.loadImage(mAppPreferences.getUserImageUrl(), MyApplication.options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					// Do whatever you want with Bitmap
					profile_imageView1.setImageBitmap(loadedImage);
					loadedImage = null;
				}
			});
		}
		username_textView1.setText(mAppPreferences.getUserName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.location_button:

			Intent in = new Intent(getActivity(),RegionsListActivity.class);
			startActivity(in);
			getActivity().overridePendingTransition(R.anim.pull_up, 0);
			
			break;
		default:
			break;
		}
	}
}
