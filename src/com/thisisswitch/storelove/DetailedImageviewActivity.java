package com.thisisswitch.storelove;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.thisisswitch.storelove.widgets.ScaleImageView;

public class DetailedImageviewActivity extends SherlockFragmentActivity {

	ImageView close_button1;
	ScaleImageView detailed_imageView;
	String prodcut_url ;

	//MixpanelAPI mMixpanel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_details_imageview);

		close_button1 = (ImageView)findViewById(R.id.close_button1);
		detailed_imageView = (ScaleImageView)findViewById(R.id.detailed_imageView);
		//mMixpanel = MixpanelAPI.getInstance(this, Globals.MIXPANEL_TOKEN);
		//https://github.com/diegocarloslima/ByakuGallery

		Bundle extras = getIntent().getExtras();
		try {
			prodcut_url =  extras.getString("image_url");
		} catch (Exception e) {}

		MyApplication.imageLoader.displayImage(prodcut_url,    detailed_imageView, MyApplication.options);
		close_button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				finish();
				overridePendingTransition(0, R.anim.pull_out);
			}
		});
	}

	@Override
	public void onBackPressed(){
		super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.pull_out);
	}
	
	@Override
	protected void onDestroy() {
		//mMixpanel.flush();
		super.onDestroy();
	}
}
