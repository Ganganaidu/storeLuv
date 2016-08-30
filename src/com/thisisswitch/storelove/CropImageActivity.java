package com.thisisswitch.storelove;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.edmodo.cropper.CropImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thisisswitch.storelove.utils.Globals;

public class CropImageActivity  extends SherlockFragmentActivity implements OnClickListener{

	protected static final String TAG = "CropImageActivity";

	// Static final constants
	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
	private static final int ROTATE_NINETY_DEGREES = 90;
	//private static final int ON_TOUCH = 1;

	private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
	private int mAspectRatioX = 8;
	Bitmap croppedImage;
	ImageLoader imageLoader;
	DisplayImageOptions options;

	CropImageView cropImageView;
	Button cropButton ;
	Button rotateButton;
	Uri mImageCaptureUri = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_image);

		//getSupportActionBar().hide();
		getSupportActionBar().setTitle("Crop Image");
		//getSupportActionBar().setIcon(icon);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		cropImageView = (CropImageView) findViewById(R.id.CropImageView);
		cropButton = (Button) findViewById(R.id.button_crop);
		rotateButton = (Button) findViewById(R.id.button_rotate);

		//Sets initial aspect ratio to 10/10, for demonstration purposes
		cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
		// Set initial spinner value
		cropImageView.setFixedAspectRatio(false);
		cropImageView.setAspectRatio(mAspectRatioX, mAspectRatioY);

		cropButton.setOnClickListener(this);
		rotateButton.setOnClickListener(this);

		cropImageView.setGuidelines(0);

		Intent intent = getIntent();
		mImageCaptureUri = intent.getData(); // this line was missing for

		Log.e(TAG, "mImageCaptureUri--with rotate--->"+mImageCaptureUri);
		try {
			ContentResolver cr = getContentResolver();
			InputStream imageStream = cr.openInputStream(mImageCaptureUri);
			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

			bmpFactoryOptions.inPurgeable = true;
			bmpFactoryOptions.inTempStorage = new byte[16*1024];
			bmpFactoryOptions.inSampleSize = 4;

			//Decoding the image to be a bitmap
			Bitmap converted_image = BitmapFactory.decodeStream(imageStream, null, bmpFactoryOptions);
			cropImageView.setImageBitmap(converted_image);
			converted_image = null;

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_crop:

			croppedImage = cropImageView.getCroppedImage();
			//croppedImage = scaleDownBitmap(croppedImage,300,this);
			Uri sdCardUri = Uri.parse("file://" +Globals.extStorageDirectory);
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, sdCardUri));
			File file = new File(Globals.extStorageDirectory);
			if (file.exists()) {
			} else if (file.mkdirs()) {}

			OutputStream outStream = null;
			file = new File(Globals.extStorageDirectory + "/" + System.currentTimeMillis() + ".jpg");
			try {
				outStream = new FileOutputStream(file);
				croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
				croppedImage = null;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				//sdCardUri = Uri.parse(file.getAbsolutePath());
				Bundle extras = new Bundle();
				extras.putString("data", file.getAbsolutePath());
				setResult(RESULT_OK,(new Intent()).setAction("inline-data").putExtras(extras));

				//deleting original image after saving new cropped image 
				if(mImageCaptureUri != null){
					file = new File(mImageCaptureUri.getPath());
					//Log.e(TAG,"file-->"+file.exists());
					if (file.exists()) {
						file.delete();
					}
				}
			}catch (Exception e) {}
			finish();

			break;
		case R.id.button_rotate:
			cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
