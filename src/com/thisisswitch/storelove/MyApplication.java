package com.thisisswitch.storelove;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

public class MyApplication extends Application {

	//private static final String TAG = "MyApplication";

	public static Context mContext;
	private static MyApplication instance = null;
	public static DisplayImageOptions options;
	public static ImageLoader imageLoader;
	public static ImageLoaderConfiguration config;

	public static MyApplication getInstance() {
		if(instance != null) {
			return instance;
		} else {
			return new MyApplication();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mContext = this;

		initImageLoader(getApplicationContext());
		//Image loader instance
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.stub_background)
		.showImageForEmptyUri(R.drawable.ic_stub_icon_3)
		.showImageOnFail(R.drawable.ic_stub_icon_4)
		.delayBeforeLoading(0)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public static void initImageLoader(Context context) {
		//This configuration tuning is custom. You can tune every option, you may tune some of them,
		//or you can create default configuration by
		//ImageLoaderConfiguration.createDefault(this);
		//method.
		config = new ImageLoaderConfiguration.Builder(context)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.FIFO)
		.denyCacheImageMultipleSizesInMemory()
		.build();

		L.disableLogging();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onLowMemory(){  
		//clear all memory cached images when system is in low memory
		//note that you can configure the max image cache count, see CONFIGURATION
		//BitmapAjaxCallback.clearCache();
	}
}
