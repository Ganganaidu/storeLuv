package com.thisisswitch.storelove.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.thisisswitch.storelove.R;

public class SplashFragment4 extends SherlockFragment{

	//callback listners for displaying article details fragment 
	private OnSkipListener listener;
	ImageView skip_button1;
	ImageView imageView1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	//second this view called
	// Initialize all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		View view = inflater.inflate(R.layout.fragment_layout_4, container, false);

		skip_button1 = (ImageView)view.findViewById(R.id.skip_button1);
		imageView1 = (ImageView)view.findViewById(R.id.imageView1);
		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//imageView1.setImageResource(R.drawable.ic_str_help3);
		skip_button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onDoneSelected(0);
			}
		});
	}

	//listners for skip helpview
	public interface OnSkipListener {
		public void onDoneSelected(int result);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof OnSkipListener) {
			listener = (OnSkipListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MyListFragment.OnItemSelectedListener");
		}
	}
}
