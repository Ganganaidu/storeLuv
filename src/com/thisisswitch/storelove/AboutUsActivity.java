package com.thisisswitch.storelove;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AboutUsActivity extends SherlockActivity implements OnClickListener{

	//	LinearLayout storeluv_linearLayout1,switch_logo_linearLayout;
	//	TextView desc_textView2;
	ImageView logo_imageView3;
	//MixpanelAPI mMixpanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);

		//getSupportActionBar().hide();
		getSupportActionBar().setTitle("About Us");
		//getSupportActionBar().setIcon(icon);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		logo_imageView3 = (ImageView)findViewById(R.id.logo_imageView3);
		logo_imageView3.setOnClickListener(this);
//		mMixpanel = MixpanelAPI.getInstance(this, Globals.MIXPANEL_TOKEN);
//		JSONObject props = new JSONObject();
//		try {
//			props.put("AboutUs", "AboutUs");
//			//props.put("Gender", "Female");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		mMixpanel.track("AboutUs", props);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) { 

			finish();
			overridePendingTransition (0, R.anim.pull_out);
		}
		return false;
	}
	@Override
	public void onClick(View v) {
		Intent browserIntent = null;
		switch (v.getId()) {

		case R.id.logo_imageView3:
			browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thisisswitch.com"));
			startActivity(browserIntent);
			break;
			//	case R.id.switch_logo_linearLayout:
			//	browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thisisswitch.com"));
			//	startActivity(browserIntent);
			//	break;
		default:
			break;
		}
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
