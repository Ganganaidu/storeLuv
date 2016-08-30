package com.thisisswitch.storelove.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.switchsoft.maplibrary.util.Networking;
import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.ProductDetailsActivity;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.paginlistview.PagingBaseAdapter;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.services.SendLikesToserverTask;
import com.thisisswitch.storelove.socialnetwork.StorluvLoginActvity;
import com.thisisswitch.storelove.utils.AppUrls;

public class ProductsListAdapter extends PagingBaseAdapter<ProdcutModel>  {

	//private static final String TAG = "ProdcutDisplayAdapter";
	Activity mContext;
	LayoutInflater inflater;

	ArrayList<ProdcutModel> productList;
	AppPreferences mAppPreferences ; 
	ImageLoadingListener animateFirstListener;

	//for item animation positions
	int mLastPosition = -1;
	//Animation zoomin, zoomout; //declared as public
	//DisplayImageOptions options ;
	static boolean firstDisplay = false;
	ViewHolder mViewHolder = null;
	//ImageView imageView1;

	public ProductsListAdapter(Activity context,ArrayList<ProdcutModel> productList,
			AppPreferences mAppPreferences) {

		animateFirstListener = new AnimateFirstDisplayListener();
		inflater = LayoutInflater.from(context) ;
		this.mContext = context;
		this.productList = productList;
		this.mAppPreferences = mAppPreferences;
	}

	@Override
	public int getCount() {
		return productList.size();
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
			convertView = inflater.inflate(R.layout.row_staggered,null);

			mViewHolder.imageView1 = (ImageView) convertView .findViewById(R.id.row_imageView1);
			mViewHolder.title_textView1 = (TextView) convertView .findViewById(R.id.title_textView1);
			mViewHolder.price_textView1 = (TextView) convertView .findViewById(R.id.price_textView1);
			mViewHolder.like_count_textView = (TextView) convertView .findViewById(R.id.like_count_textView);

			//mViewHolder.imageView1.setTag(mViewHolder);
			convertView.setTag(mViewHolder);
		} else {
			// Reuse existing row view
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		mViewHolder.imageView1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext,ProductDetailsActivity.class);
				intent.putExtra("product_id", productList.get(position).getProduct_id());
				intent.putExtra("totalproducts", productList.get(position).getProduct_like_count());
				intent.putExtra("likestatus", productList.get(position).getDevice_like_status());
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.pull_up, R.anim.btn_anim);
			}
		});

		//mageView.setImageResource(isLiked? R.drawable.heart_oo : R.drawable.heart_oc);
		mViewHolder.like_count_textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//Log.d("ProductAdapter","userId-->"+mAppPreferences.getUserid());
				if(mAppPreferences.getUserid().equals("") || mAppPreferences.getUserid().equals("0")) {
					
					Intent in = new Intent(mContext,StorluvLoginActvity.class);
					mContext.startActivity(in);
					mContext.overridePendingTransition(R.anim.pull_up, R.anim.slide_bottom_out);
					
				} else {
					
					int prodcutCount = productList.get(position).getProduct_like_count();
					if(productList.get(position).getDevice_like_status() == 10002) {
						//particular device prodcut count
						productList.get(position).setDevice_like_status(10001);
						//total prodcut count
						productList.get(position).setProduct_like_count(prodcutCount+1);
					} else {
						productList.get(position).setDevice_like_status(10002);
						if(prodcutCount !=0) {
							productList.get(position).setProduct_like_count(prodcutCount-1);	
						}
					}
					//TODO need to send to server
					if(Networking.isNetworkAvailable(mContext)) {
						String url = AppUrls.insertProductLikesUrl(productList.get(position).getProduct_id(),
								mAppPreferences.getUserid(), String.valueOf(productList.get(position).getDevice_like_status()));
						new SendLikesToserverTask(url).execute();
					}
				}
				notifyDataSetChanged();
			}
		});

		//changing total count image based on count value
		if(productList.get(position).getDevice_like_status() == 10002) {
			mViewHolder.like_count_textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_icon, 0, 0);	
		} else {
			mViewHolder.like_count_textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_like_oc, 0, 0);
		}
		
		mViewHolder.title_textView1.setText(productList.get(position).getStore_name());
		mViewHolder.price_textView1.setText("Rs."+productList.get(position).getProdcut_price());
		mViewHolder.like_count_textView.setText(String.valueOf(productList.get(position).getProduct_like_count()));

       //displaying image 
		MyApplication.imageLoader.displayImage(productList.get(position).getProduct_url(), mViewHolder.imageView1, MyApplication.options, animateFirstListener);			
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

		ImageView imageView1;
		TextView title_textView1;
		TextView price_textView1,like_count_textView;
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
