package com.thisisswitch.storelove.utils;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Environment;

import com.thisisswitch.storelove.R;

public class Globals {

	//	506249494088

	public static String gTracker_id = "UA-46057192-1";
	public static final String MIXPANEL_TOKEN = "eb78c52fe3d2b0f3354f7eebc5ebd8fa";

	public static String extStorageDirectory = Environment.getExternalStorageDirectory() + "/BlueHawk";
    public static String PAGE_COUNT_VALUE = "5";

	//	InsertPushDevice?Device_Id={Device_Id}&Device_Type={Device_Type}
	public static final int readTimeOut = 20000;//10sec
	public static String mNewtWorkState = "No network available";
	public static String mTimeOutConnection = "Connection timed out Please Try again later";
	public static String connectionError = "Connection error";
	public static String appLink = "https://play.google.com/store/apps/details?id=com.thisisswitch.storelove" +" \n Get into the stores you love.";
	public static int updateStatus = 0;

	public static String [] slide_menu_titles = {

		"Featured",//0
		"My Feed",//1
		"Stores",//2
		"Categories",//3
		"Special Categories",//4
		"Settings"//5
	};

	public static int [] slide_menu_icons = {

		R.drawable.slide_home_icon,
		R.drawable.slide_myfeed_icon,
		R.drawable.slide_stores_icon,
		R.drawable.slide_myfeed_icon,
		R.drawable.slide_featured,
		R.drawable.slide_settings_icon
	};

	//presetn color code ...#1cd385

	/** 	is like service running  
	 * if true means runing false means not running*/ 
	public static boolean isLikeServiceRunning(Context mContext){

		boolean serviceRunning = false;
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
		Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
		while (i.hasNext()) {
			ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo)i.next();
			if(runningServiceInfo.service.getClassName().equals("com.thisisswitch.storelove.services.SendDataService")){
				serviceRunning = true;
			}
		}
		return serviceRunning;
	}
	
	public static Bitmap getRoundedShape(Bitmap bitmap) {
		
		Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		BitmapShader shader = new BitmapShader (bitmap,  TileMode.CLAMP, TileMode.CLAMP);
		Paint paint = new Paint();
		        paint.setShader(shader);

		Canvas c = new Canvas(circleBitmap);
		c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

		//myImageView.setImageBitmap(circleBitmap);
//	    int targetWidth = 50;
//	    int targetHeight = 50;
//	    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
//	                        targetHeight,Bitmap.Config.ARGB_8888);
//
//	    Canvas canvas = new Canvas(targetBitmap);
//	    Path path = new Path();
//	    path.addCircle(((float) targetWidth - 1) / 2,
//	        ((float) targetHeight - 1) / 2,
//	        (Math.min(((float) targetWidth), 
//	        ((float) targetHeight)) / 2),
//	        Path.Direction.CCW);
//
//	    canvas.clipPath(path);
//	    Bitmap sourceBitmap = scaleBitmapImage;
//	    canvas.drawBitmap(sourceBitmap, 
//	        new Rect(0, 0, sourceBitmap.getWidth(),
//	        sourceBitmap.getHeight()), 
//	        new Rect(0, 0, targetWidth, targetHeight), null);
	    return circleBitmap;
	}
	//screen height and width
	//	DisplayMetrics metrics = getResources().getDisplayMetrics();
	//	int width = metrics.widthPixels;
	//	int height = metrics.heightPixels;
}
