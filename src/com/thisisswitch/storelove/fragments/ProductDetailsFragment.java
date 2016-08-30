package com.thisisswitch.storelove.fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.appkraft.parallax.ParallaxScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thisisswitch.storelove.DetailedImageviewActivity;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.socialnetwork.StorluvLoginActvity;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;

public class ProductDetailsFragment extends SherlockFragment implements OnClickListener{

	private ParallaxScrollView parallax;
	private ImageView main_imageView1,share_imageview;
	private TextView call_textview,mail_textview,following_store_textView;
	private LinearLayout tag_container_layout;
	//private ImageView like_imageView2;

	private TextView prodcut_title,price_textView1,
	product_des,like_product_textView,
	store_name_textview;
	private TextView[] product_tags_textview,
	separtor_textview;

	private Tracker tracker = null;
	private Bitmap mSaveBit = null;
	//private Animation growAnim;
	private AppPreferences mAppPreferences ;
	private MixpanelAPI mMixpanel;
	private JSONObject props;

	private int  position = 0;

	private ArrayList<String> productUrlsList;
	private ArrayList<String> tagsList;
	private ProdcutModel mProdcutModel;

	//callback listners for displaying article details fragment 
	private ProductOnclickListener listener;

	public static ProductDetailsFragment newInstance(int position,ArrayList<String> productDetailsList,ProdcutModel mProdcutModel) {

		ProductDetailsFragment fragment = new ProductDetailsFragment();
		fragment.position = position;
		fragment.productUrlsList = productDetailsList;
		fragment.mProdcutModel = mProdcutModel;

		return fragment;
	}

	//Listeners for displaying news details page
	public interface ProductOnclickListener {
		/** call back method for sending like status to server 
		 * @param url URL for inserting product like status
		 * {@link ProductDetailsFragment}*/
		public void onLikeChanged(String url);
		public void onProductTagSearch(String tagName);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ProductOnclickListener) {
			listener = (ProductOnclickListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MyListFragment.OnItemSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppPreferences = new AppPreferences(getActivity()); 
		tagsList = new ArrayList<String>();
	}
	//second this view called
	// Initialise all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		View view = inflater.inflate(R.layout.fragment_product_details, container, false);

		parallax = (ParallaxScrollView)view.findViewById(R.id.scrollView1);
		main_imageView1 = (ImageView)view.findViewById(R.id.main_imageView1);
		like_product_textView = (TextView)view.findViewById(R.id.like_product_textView);
		store_name_textview = (TextView)view.findViewById(R.id.store_name_textview);
		following_store_textView = (TextView)view.findViewById(R.id.following_store_textView);
		tag_container_layout = (LinearLayout)view.findViewById(R.id.tag_container_layout);

		prodcut_title = (TextView)view.findViewById(R.id.prodcut_title);
		product_des = (TextView)view.findViewById(R.id.product_des);
		price_textView1 = (TextView)view.findViewById(R.id.price_textView1);

		call_textview = (TextView)view.findViewById(R.id.call_textview);
		mail_textview = (TextView)view.findViewById(R.id.mail_textview);
		share_imageview = (ImageView)view.findViewById(R.id.share_imageview);

		return view;
	}
	//TODO https://github.com/ManuelPeinado/FadingActionBar
	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		call_textview.setOnClickListener(this);
		mail_textview.setOnClickListener(this);
		share_imageview.setOnClickListener(this);
		like_product_textView.setOnClickListener(this);
		following_store_textView.setOnClickListener(this);
		main_imageView1.setOnClickListener(this);

		prodcut_title.setText(mProdcutModel.prodcut_name);
		price_textView1.setText("Rs. "+mProdcutModel.product_price);
		product_des.setText(mProdcutModel.product_desc);
		store_name_textview.setText(mProdcutModel.store_name);
		like_product_textView.setText(String.valueOf(mProdcutModel.product_like_count));
		//product_tags_textview.setText(mProdcutModel.product_tags);

		displayTags();
		MyApplication.imageLoader.displayImage(productUrlsList.get(position),main_imageView1, MyApplication.options);
		FadeInBitmapDisplayer.animate(main_imageView1, 500);
		parallax.setImageViewToParallax(main_imageView1);

		parallax.post(new Runnable() {
			@Override
			public void run() {
				parallax.setViewsBounds(5);
			}
		});

		storeLikeStatus();
		tracker = GoogleAnalytics.getInstance(getActivity()).getTracker(Globals.gTracker_id);
		tracker.set(Fields.SCREEN_NAME, "Detail Screen");
		//growAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.layout_anim);
		//mixpanel for product page details track
		mMixpanel = MixpanelAPI.getInstance(getActivity(), Globals.MIXPANEL_TOKEN);
		JSONObject props = new JSONObject();
		try {
			props.put("IMAGE_DETAILS",mProdcutModel.store_name);
			//props.put("Gender", "Female");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mMixpanel.track("IMAGE_DETAILS", props);
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.main_imageView1:

			in = new Intent(getActivity(),DetailedImageviewActivity.class);
			in.putExtra("image_url", productUrlsList.get(position));
			startActivity(in);
			getActivity().overridePendingTransition(R.anim.pull_up, R.anim.btn_anim);

			break;
		case R.id.call_textview:

			// Sending a screen view in v3 using MapBuilder.
			tracker.send(MapBuilder.createEvent("UX", "touch", "call", null).build());
			props = new JSONObject();
			try {
				props.put("CALL", mProdcutModel.store_name);
				//props.put("Gender", "Female");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mMixpanel.track("CALL", props);

			String number = "tel:" + mProdcutModel.store_mobile_number .trim();
			Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number)); 
			startActivity(callIntent);

			break;
		case R.id.mail_textview:
			// Sending a screenview in v3 using MapBuilder.
			tracker.send(MapBuilder.createEvent("UX", "touch", "mail", null).build());
			props = new JSONObject();
			try {
				props.put("MAIL", mProdcutModel.store_name);
				//props.put("Gender", "Female");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mMixpanel.track("MAIL", props);

			if(Networking.isNetworkAvailable(getActivity())) {
				sendMail(); 
			} else {
				Toast.makeText(getActivity(), Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.like_product_textView:

			if(Networking.isNetworkAvailable(getActivity())) {
				if(mAppPreferences.getUserid().equals("") || mAppPreferences.getUserid().equals("0")) {
					in = new Intent(getActivity(),StorluvLoginActvity.class);
					startActivity(in);
					getActivity().overridePendingTransition(R.anim.pull_up, R.anim.pull_out);
				} else {
					Globals.updateStatus = 1;
					tracker.send(MapBuilder.createEvent("UX", "touch", "like", null).build());
					changeLikeStatus();
					storeLikeStatus();
				} } else {
					Toast.makeText(getActivity(), Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
				}
			break;

		case R.id.share_imageview:

			shareDetails();
			
			break;
		case R.id.following_store_textView:

			if(Networking.isNetworkAvailable(getActivity())) {
				if(mAppPreferences.getUserid().equals("") || mAppPreferences.getUserid().equals("0")) {
					in = new Intent(getActivity(),StorluvLoginActvity.class);
					startActivity(in);
					getActivity().overridePendingTransition(R.anim.pull_up, 0);
				} else {
					tracker.send(MapBuilder.createEvent("UX", "touch", "like", null).build());
					changeStoreLikeStatus(); 
					storeLikeStatus();
				} } else {
					Toast.makeText(getActivity(), Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
				}
			break;
		default:
			break;
		}
	}

	private void storeLikeStatus(){

		//for store like status
		if(mProdcutModel.store_user_like == 10002) {
			following_store_textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_following, 0, 0);
		} else {
			following_store_textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_following_oc, 0, 0);
		}

		//prodcut like status
		if(mProdcutModel.device_like_status == 10002) {
			like_product_textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_following, 0, 0);
		} else {
			like_product_textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_following_oc, 0, 0);
		}
	}
	//TODO
	private void displayTags() {
		if(mProdcutModel.product_tags.length() != 0){
			StringTokenizer strDivider = new StringTokenizer(mProdcutModel.product_tags, ",");
			tagsList.clear();
			while (strDivider.hasMoreTokens()) {
				tagsList.add(strDivider.nextToken());
				//System.out.println(strDivider.nextToken());
			}
			product_tags_textview = new TextView[tagsList.size()];
			separtor_textview = new TextView[tagsList.size()];

			for(int i=0;i<tagsList.size(); i++) {
				product_tags_textview[i] = new TextView(getActivity());
				separtor_textview[i] = new TextView(getActivity());
				separtor_textview[i].setBackgroundColor(Color.WHITE);
				separtor_textview[i].setWidth(10);

				product_tags_textview[i].setId(i);
				product_tags_textview[i].setText(" "+tagsList.get(i)+" ");
				product_tags_textview[i].setTextColor(Color.WHITE);
				product_tags_textview[i].setTextSize(20);
				product_tags_textview[i].setPadding(5, 0, 0, 0);
				product_tags_textview[i].setTypeface(null, Typeface.BOLD);
				product_tags_textview[i].setBackgroundColor(R.drawable.tag_background);
				product_tags_textview[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//Log.e("","name  "+tagsList.get(v.getId()));
						listener.onProductTagSearch(tagsList.get(v.getId()));
					}
				});
				tag_container_layout.addView(product_tags_textview[i]);
				tag_container_layout.addView(separtor_textview[i]);
			}
		}
	}
	//using this for sharing image, saving image file in local sdcard
	private File saveImage(Bitmap mSaveBit){

		File imageFile = null;
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/StorLuv");    
		myDir.mkdirs();

		String fname = mProdcutModel.prodcut_name+".jpg";
		imageFile = new File (myDir, fname);
		if (imageFile.exists ()) imageFile.delete (); 
		try {

			FileOutputStream fo = new FileOutputStream(imageFile);
			mSaveBit.compress(CompressFormat.JPEG, 100, fo);
			mSaveBit = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageFile;
	}

	//TODO sending mail 
	private void sendMail() {

		MyApplication.imageLoader.loadImage(productUrlsList.get(position), MyApplication.options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// Do whatever you want with Bitmap
				mSaveBit = loadedImage;
			}
		});
		Uri uri = Uri.fromFile(saveImage(mSaveBit));
		mSaveBit = null;//release bitmap memory
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{mProdcutModel.store_eamilid});		  
		email.putExtra(Intent.EXTRA_SUBJECT, "Contact: "+mProdcutModel.store_name);
		email.putExtra(Intent.EXTRA_TEXT, "This mail is sent from Storluv app.");
		email.setType("image/jpeg");
		email.putExtra(Intent.EXTRA_STREAM, uri);

		PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> matches = pm.queryIntentActivities(email, 0);
		ResolveInfo best = null;
		for(ResolveInfo info : matches)
			if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
				best = info;
		if (best != null)
			email.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		startActivity(email);
	}

	/**social network share */
	private void shareDetails() {

		MyApplication.imageLoader.loadImage(productUrlsList.get(position), MyApplication.options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// Do whatever you want with Bitmap
				mSaveBit = loadedImage;
			}
		});
		Uri uri = Uri.fromFile(saveImage(mSaveBit));
		mSaveBit = null;
		List<ApplicationInfo> packages = getActivity().getPackageManager().getInstalledApplications(PackageManager.MATCH_DEFAULT_ONLY);
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("image/jpeg");

		for (ApplicationInfo appInfo : packages) {

			//Log.v(TAG,"appInfo-->"+appInfo.packageName);
			if ("com.facebook.katana".equals(appInfo.packageName)){

				//Log.e(TAG,"Facebook");
				intent.putExtra(Intent.EXTRA_TEXT, mProdcutModel.prodcut_name);
				intent.putExtra(Intent.EXTRA_TITLE, mProdcutModel.store_name);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			}
			else if ("com.twitter.android".equals(appInfo.packageName)) {

				//Log.e(TAG,"Twitter");
				intent.putExtra(Intent.EXTRA_TEXT,   mProdcutModel.prodcut_name);
				intent.putExtra(Intent.EXTRA_SUBJECT, mProdcutModel.store_name);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			} 
			else if ("com.android.email".equals(appInfo.packageName)) {							

				//Log.e(TAG,"Mail");
				//Log.v(TAG,"appInfo-->"+appInfo.packageName);
				intent.putExtra(Intent.EXTRA_TEXT,    mProdcutModel.prodcut_name);
				intent.putExtra(Intent.EXTRA_SUBJECT, mProdcutModel.store_name);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			}
		}
		startActivity(Intent.createChooser(intent, "Choice App to send email:"));
	}

	//change like status and saving in local db
	private void changeLikeStatus() {
		if(mProdcutModel.device_like_status == 10002) {
			mProdcutModel.device_like_status =  10001;
			//particular device prodcut count
			mProdcutModel.product_like_count = 1;
			//total prodcut count
			mProdcutModel.product_like_count = mProdcutModel.product_like_count+1;
			//like_imageView2.setImageResource(R.drawable.ic_rating_favorite);
			like_product_textView.setText(String.valueOf(mProdcutModel.product_like_count));

		} else {
			mProdcutModel.device_like_status =  10002;
			//total prodcut count
			if(mProdcutModel.product_like_count !=0 ) {
				mProdcutModel.product_like_count = mProdcutModel.product_like_count-1;
			}
			//like_imageView2.setImageResource(R.drawable.ic_rating_favorite_oc);
			like_product_textView.setText(String.valueOf(mProdcutModel.product_like_count));
		}

		if(Networking.isNetworkAvailable(getActivity())) {
			String url = AppUrls.insertProductLikesUrl(mProdcutModel.product_id, mAppPreferences.getUserid(), 
					String.valueOf(mProdcutModel.device_like_status));
			listener.onLikeChanged(url);			
		}
	}

	//change like status and saving in local db
	private void changeStoreLikeStatus() {
		if(mProdcutModel.store_user_like == 10002) {
			mProdcutModel.store_user_like =  10001;
		} else {
			mProdcutModel.store_user_like =  10002;
		}

		if(Networking.isNetworkAvailable(getActivity())) {
			String url = AppUrls.insertStoresLikesUrl(mProdcutModel.store_id, mAppPreferences.getUserid(), 
					String.valueOf(mProdcutModel.store_user_like)); 
			listener.onLikeChanged(url);			
		}
	}
}
