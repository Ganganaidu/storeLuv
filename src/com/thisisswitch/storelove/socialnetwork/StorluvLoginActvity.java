package com.thisisswitch.storelove.socialnetwork;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.thisisswitch.storelove.CropImageActivity;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.StoreLoveActivity;
import com.thisisswitch.storelove.models.UserRegModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.utils.ACache;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;
import com.thisisswitch.storelove.widgets.CircleImageView;

public class StorluvLoginActvity extends SherlockFragmentActivity implements OnClickListener,
ConnectionCallbacks, OnConnectionFailedListener,PlusClient.OnPeopleLoadedListener{

	private String TAG = "SocialNetworkActvity";

	private static final int PICK_FROM_CAMERA = 5001;
	private static final int CROP_SELECTED_PHOTO = 5002;
	private static final int PICK_FROM_GALLERY = 5003;
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private static Uri mImageCaptureUri = null;
	private byte[] mUserImage = null;

	//login
	private EditText login_username_editText,login_password_editText;
	private Button login_button;
	private TextView skip_textView2,signup_textView2;

	//main page login
	//private RelativeLayout main_page_loginscreen;
	private ScrollView login_scrollView;
	private Button facebook_button;
	private SignInButton gpluse_button;

	//Registration
	private ScrollView reg_scrollView;
	private CircleImageView user_image_imageView;
	private EditText username_editText,user_firstname_editText,
	user_lastname_editText,user_password_editText,
	user_confirm_password_editText,user_mail_id_editText,
	user_phone_editText,user_location_editText;
	private Button reg_submit_button,user_dob_editText;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private UserRegModel  mUserRegModel ;

	private ProgressDialog mProgressDialog ;
	private AppPreferences mAppPreferences;
	private ACache mCache = null;

	private String userRegUrl = "";
	private int skipviewEnable = 0;

	//========= for date picker ===============
	private static final int DATE_DIALOG_ID = 1;
	private int myYear;
	private int myMonth;
	private int myDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_socialnetwork);

		getSupportActionBar().setTitle("Log in");
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		mUserRegModel = new UserRegModel();
		mAppPreferences = new AppPreferences(this);
		mCache = ACache.get(this);

		intiliszeUi();
		gpluseLogin();
		//hide keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	private void intiliszeUi() {
		//login
		//normal_login_layout = (RelativeLayout)findViewById(R.id.normal_login_layout);
		skip_textView2 = (TextView)findViewById(R.id.skip_textView2);
		signup_textView2 = (TextView)findViewById(R.id.signup_textView2);

		login_username_editText = (EditText)findViewById(R.id.login_username_editText);
		login_password_editText = (EditText)findViewById(R.id.login_password_editText);
		login_button = (Button)findViewById(R.id.login_button);

		//main page login
		//main_page_loginscreen = (RelativeLayout)findViewById(R.id.main_page_loginscreen);
		login_scrollView = (ScrollView)findViewById(R.id.login_scrollView);
		login_scrollView.setVisibility(View.VISIBLE);
		facebook_button = (Button)findViewById(R.id.facebook_button);
		gpluse_button = (SignInButton)findViewById(R.id.gpluse_button);

		//Registration
		reg_scrollView = (ScrollView)findViewById(R.id.reg_scrollView);
		reg_scrollView.setVisibility(View.GONE);
		user_image_imageView = (CircleImageView)findViewById(R.id.user_image_imageView);
		username_editText = (EditText)findViewById(R.id.username_editText);
		user_firstname_editText = (EditText)findViewById(R.id.user_firstname_editText);
		user_lastname_editText = (EditText)findViewById(R.id.user_lastname_editText);
		user_password_editText  = (EditText)findViewById(R.id.user_password_editText);
		user_confirm_password_editText = (EditText)findViewById(R.id.user_confirm_password_editText);
		user_mail_id_editText = (EditText)findViewById(R.id.user_mail_id_editText);
		user_phone_editText = (EditText)findViewById(R.id.user_phone_editText);
		user_location_editText = (EditText)findViewById(R.id.user_location_editText);
		user_dob_editText = (Button)findViewById(R.id.user_dob_editText);
		reg_submit_button = (Button)findViewById(R.id.reg_submit_button);

		facebook_button.setOnClickListener(this);
		gpluse_button.setOnClickListener(this);
		user_dob_editText.setOnClickListener(this);
		login_button.setOnClickListener(this);
		reg_submit_button.setOnClickListener(this);
		user_image_imageView.setOnClickListener(this);

		signup_textView2.setOnClickListener(this);
		skip_textView2.setOnClickListener(this);

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBack();
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case DATE_DIALOG_ID:

			final Calendar c1 = Calendar.getInstance();

			//adding previous selected date to calendar
			myYear = c1.get(Calendar.YEAR);
			myMonth = c1.get(Calendar.MONTH);
			myDay = c1.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(this, dateSetListener, myYear, myMonth, myDay);
		}
		return null;
	}

	DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {

			myYear = year;
			myMonth = monthOfYear;
			myDay = dayOfMonth;

			updateDate();
		}
	};

	//updating date
	private void updateDate() {

		//Log.v(TAG,"date select-->"+(myMonth + 1));
		StringBuilder dob_str_Builder = null;
		if (myDay < 10)  {
			//if selected month is more than 10 removing zero  
			if((myMonth + 1) <10) {
				dob_str_Builder = new StringBuilder();
				dob_str_Builder.append(myYear).append("-").append("0")
				.append(myMonth + 1).append("-").append("0")
				.append(myDay).append("").toString();				
			} else {
				dob_str_Builder = new StringBuilder();
				dob_str_Builder.append(myYear).append("-")
				.append(myMonth + 1).append("-").append("0")
				.append(myDay).append("").toString();				
			}
		} else {
			dob_str_Builder = new StringBuilder();
			dob_str_Builder.append(myYear).append("-")
			.append(myMonth + 1).append("-")
			.append(myDay).append("").toString();
		}
		String date = dob_str_Builder.toString();
		user_dob_editText.setText(date);
	}

	//TODO
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.skip_textView2:

			Intent in = new Intent(StorluvLoginActvity.this,StoreLoveActivity.class);
			startActivity(in);
			finish();
			overridePendingTransition(0, R.anim.pull_out);

			break;

		case R.id.signup_textView2:

			reg_scrollView.setVisibility(View.VISIBLE);
			login_scrollView.setVisibility(View.GONE);

			break;
		case R.id.user_image_imageView:
			getSnapImage();
			break;

		case R.id.login_button:

			mUserRegModel.user_name  = login_username_editText.getText().toString().trim();
			mUserRegModel.user_password = login_password_editText.getText().toString().trim();
			if(mUserRegModel.user_name.length() == 0) {

				Toast.makeText(StorluvLoginActvity.this, "Please enter username", Toast.LENGTH_SHORT).show();

			} else if(mUserRegModel.user_password.length() == 0) {

				Toast.makeText(StorluvLoginActvity.this, "Please enter password", Toast.LENGTH_SHORT).show();

			} else {

				userRegUrl = AppUrls.userLogin(mUserRegModel.user_name, mUserRegModel.user_password, "10001");
				new LoginAsyncTask().execute();

			}
			break;
		case R.id.reg_submit_button:

			validRegValues();

			break;
		case R.id.facebook_button:
			if(Networking.isNetworkAvailable(this)) {

				//Log.d(TAG,"social id-->"+mAppPreferences.getUserid());
				if(mAppPreferences.getUserid().equals("")){
					mAppPreferences.saveUserid("0");
				}
				if(!mAppPreferences.getUserid().equals("0")) {
					Toast.makeText(StorluvLoginActvity.this, "Please log out", Toast.LENGTH_SHORT).show();
				} else {
					Session session = Session.getActiveSession();
					if(session == null || !session.isOpened()) {
						fbLogin();				
					} else {
						//Toast.makeText(SocialNetworkActvity.this, ""+session.isOpened(), Toast.LENGTH_SHORT).show();
						//session.closeAndClearTokenInformation();
					}
				}
			} else {
				Toast.makeText(StorluvLoginActvity.this, Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.gpluse_button:
			//			 * check google plus application available or not in device
			//			int errorCode = GooglePlusUtil.checkGooglePlusApp(this);
			//			if (errorCode != GooglePlusUtil.SUCCESS) {
			//				GooglePlusUtil.getErrorDialog(errorCode, this, 0).show();
			//			} else {
			//			}
			//			if(mPlusClient != null){
			//				mPlusClient.disconnect();
			//			}
			if(Networking.isNetworkAvailable(this)) {

				if(mAppPreferences.getUserid().equals("")){
					mAppPreferences.saveUserid("0");
				}
				if(!mAppPreferences.getUserid().equals("0")) {
					//Toast.makeText(SocialNetworkActvity.this, "", Toast.LENGTH_SHORT).show();
				} else {
					if (!mPlusClient.isConnected()) {
						if (mConnectionResult == null) {
							mPlusClient.connect();
							mConnectionProgressDialog.show();
						} else {
							try {
								mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
							} catch (SendIntentException e) {
								// Try connecting again.
								mConnectionResult = null;
								mPlusClient.connect();
							}
						}
					}
				}
			} else {
				Toast.makeText(StorluvLoginActvity.this, Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.user_dob_editText:
			showDialog(DATE_DIALOG_ID);
			break;
		default:
			break;
		}		
	}

	private void validRegValues() {

		mUserRegModel.user_name = username_editText.getText().toString().trim();
		mUserRegModel.user_fname = user_firstname_editText.getText().toString().trim();
		mUserRegModel.user_lname = user_lastname_editText.getText().toString().trim();
		mUserRegModel.user_password = user_password_editText.getText().toString().trim();
		String confirmPassword = user_confirm_password_editText .getText().toString().trim();
		mUserRegModel.user_mail = user_mail_id_editText.getText().toString().trim();
		mUserRegModel.user_phonenumber = user_phone_editText.getText().toString().trim();
		mUserRegModel.user_location = user_location_editText.getText().toString().trim();
		mUserRegModel.user_dob = user_dob_editText .getText().toString().trim();

		if(mUserRegModel.user_name.length() == 0 ) {

			Toast.makeText(StorluvLoginActvity.this, "Please enter username", Toast.LENGTH_SHORT).show();

		} else if(mUserRegModel.user_password.length() == 0 ) {

			Toast.makeText(StorluvLoginActvity.this, "Please enter password", Toast.LENGTH_SHORT).show();

		} else if(!confirmPassword.equals(mUserRegModel.user_password)) {

			Toast.makeText(StorluvLoginActvity.this, "Password not matching ", Toast.LENGTH_SHORT).show();

		} else if(mUserRegModel.user_mail.length() == 0 || !isEmailValid(mUserRegModel.user_mail)) {

			Toast.makeText(StorluvLoginActvity.this, "Please enter valid mail id", Toast.LENGTH_SHORT).show();

		}  else if(mUserRegModel.user_phonenumber.length() == 0 || mUserRegModel.user_phonenumber.length() < 10 ) {

			Toast.makeText(StorluvLoginActvity.this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();

		}  else {
			userRegUrl = AppUrls.registerGeneralaUserUrl(mUserRegModel);
			mAppPreferences.saveLoginType("Normal");
			new UserRegistrationAsyncTask().execute();
		}
	}

	/** */
	private void gpluseLogin(){

		mPlusClient = new PlusClient.Builder(this, this, this)
		.setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
		.setScopes(Scopes.PLUS_LOGIN)  // recommended login scope for social features
		//.setScopes("profile")       // alternative basic login scope
		.build();
		// Progress bar to be displayed if the connection failure is not resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
	}

	//TODO
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.v(TAG,"requestCode-->'"+requestCode);
		Log.v(TAG,"data-->'"+data);

		if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		} 

		switch (requestCode) {
		case 64206:
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
			break;
			// Message for retrieving the camera image which was captured
		case PICK_FROM_CAMERA:
			// using this if statement as the path of camera was changed after
			if (android.os.Build.VERSION.SDK_INT <= 7) {
				mImageCaptureUri = data.getData(); // this line was missing for
			}
			doCrop();
			break;

		case PICK_FROM_GALLERY:
			mImageCaptureUri = data.getData();
			doCrop();
			break;

		case CROP_SELECTED_PHOTO:

			try {
				Bundle extras1 = data.getExtras();
				if (extras1 != null) {

					//commentPhoto = extras.getParcelable("data");
					String uri = extras1.getString("data");
					//Log.v(TAG,"uri-->'"+uri);

					mImageCaptureUri = Uri.parse(uri);
					// removing the background image
					user_image_imageView.setBackgroundResource(0);
					//user_image_imageView.setImageURI(Uri.parse(new File(uri).toString()));

					Bitmap bmp;
					bmp = BitmapFactory.decodeFile(new File(uri).toString());
					//mCache.put("profilepic", bmp);//saving in local cache for displaying as profile picture
					user_image_imageView.setImageBitmap(bmp);

					//encoding the image to string so that it can be saved into database
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
					mUserImage = baos.toByteArray();
					bmp = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	//crop image after selecting image from gallery or native camera 
	private void doCrop() {

		Intent intent = new Intent(this, CropImageActivity.class);
		intent.setData(mImageCaptureUri);
		intent.putExtra("return-data", true);
		intent.putExtra("scale", true);
		startActivityForResult(intent, CROP_SELECTED_PHOTO);
	}

	//alert for user to select image from gallery or camera
	private void getSnapImage() {

		// final exit of application
		AlertDialog.Builder builder = new AlertDialog.Builder(StorluvLoginActvity.this);
		builder.setTitle(getResources().getString(R.string.app_name));
		builder.setMessage("Pick Image");

		builder.setPositiveButton("Gallery",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				Intent galleryIntent = new Intent();
				galleryIntent.setType("image/*");
				galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(galleryIntent, "Complete action using"),PICK_FROM_GALLERY);

				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Camera",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();

				// Opens the camera so that image is captured, image is
				// retrieved through onActivity result
				// with PICK_FROM_CAMERA ID
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(Globals.extStorageDirectory);
				if (file.exists()) {
				} else if (file.mkdirs()) {}

				mImageCaptureUri = Uri.fromFile(new File(Globals.extStorageDirectory,"capturedPhoto" + String.valueOf(System.currentTimeMillis())+ ".jpg"));

				// Log.d(TAG,"getSnapImage-->"+mImageCaptureUri);
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, PICK_FROM_CAMERA);
				//startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}


	//TODO
	private void fbLogin() {
		// start Facebook Login
		Session.openActiveSession(this, true, new Session.StatusCallback() {
			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				//Toast.makeText(StorluvLoginActvity.this, "session opend-->"+session.isOpened(), Toast.LENGTH_SHORT).show();
				if (session.isOpened()) {
					Session.setActiveSession(session);
					// make request to the /me API
					Request.newMeRequest(session, new Request.GraphUserCallback() {
						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user, Response response) {

							//Log.d(TAG,"userRegUrl-->"+user);
							if (user != null) {

								mUserRegModel.user_name = user.getName();
								mUserRegModel.social_user_id  = user.getId();
								mUserRegModel.user_fname  = user.getFirstName();
								mUserRegModel.user_lname  = user.getLastName();
								mUserRegModel.user_dob  = user.getBirthday();
								mUserRegModel.user_location  = user.getLocation().getCity();
								mUserRegModel.user_mail = (String) response.getGraphObject().getProperty("email");
								mUserRegModel.user_location = user.getLocation().getCity();

								mUserRegModel.user_profile_image_url = "https://graph.facebook.com/"+mUserRegModel.social_user_id+"/picture?type=large";//?width=350&height=350
								mAppPreferences.saveUserName(user.getName());
								mAppPreferences.saveUserImageUrl(mUserRegModel.user_profile_image_url);
								mAppPreferences.saveUserSocialid(mUserRegModel.social_user_id);

								userRegUrl = AppUrls.registerSocialUserUrl(mUserRegModel);
								mAppPreferences.saveLoginType("FB");
								Log.d(TAG,"userRegUrl-->"+userRegUrl);

								new SocialRegistrationAsyncTask().execute();
							}
						}
					}).executeAsync();
				}
			}
		});
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
		// Save the result and resolve the connection failure upon a user click.
		mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mUserRegModel.user_mail = mPlusClient.getAccountName();
		//Toast.makeText(this, mUserRegModel.user_mail + " is connected.", Toast.LENGTH_LONG).show();
		mPlusClient.loadPeople(StorluvLoginActvity.this, "me");
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer,
			String nextPageToken) {

		if(mConnectionProgressDialog != null){
			mConnectionProgressDialog.dismiss();
		}

		if (status.getErrorCode() == ConnectionResult.SUCCESS) {
			try {
				int count = personBuffer.getCount();
				for (int i = 0; i < count; i++) {
					try {
						Log.e(TAG,"total-loop ->"+personBuffer.get(i));
						mUserRegModel.user_name = personBuffer.get(i).getDisplayName();
						if(mUserRegModel.user_name == null) {
							mUserRegModel.user_name = "";
						}

						mUserRegModel.user_profile_image_url = personBuffer.get(i).getImage().getUrl();
						if(mUserRegModel.user_profile_image_url == null) {
							mUserRegModel.user_profile_image_url = "";
						}

						mUserRegModel.user_dob = personBuffer.get(i).getBirthday();
						if(mUserRegModel.user_dob == null){
							mUserRegModel.user_dob = "";
						}

						mUserRegModel.social_user_id = personBuffer.get(i).getId();
						if(mUserRegModel.social_user_id == null){
							mUserRegModel.social_user_id = "";
						}

						mUserRegModel.user_location = personBuffer.get(i).getCurrentLocation();
						if(mUserRegModel.user_location == null){
							mUserRegModel.user_location  = "";
						}

						//mUserRegModel.user_mail = personBuffer.get(i).get

						mUserRegModel.user_profile_image_url = mUserRegModel.user_profile_image_url.replace("?sz=50", "?sz=200");
						mAppPreferences.saveUserName(mUserRegModel.user_name);
						mAppPreferences.saveUserImageUrl(mUserRegModel.user_profile_image_url);
						mAppPreferences.saveUserSocialid(mUserRegModel.social_user_id);
						mAppPreferences.saveLoginType("GPLUS");

						userRegUrl = AppUrls.registerSocialUserUrl(mUserRegModel);
						Log.v(TAG,"userRegUrl-->"+userRegUrl);

						new SocialRegistrationAsyncTask().execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} finally {
				personBuffer.close();
			}
		} else {
		}		
	}

	public boolean isEmailValid(String email) {
		String regExpn =
				"^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
						+"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
						+"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
						+"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
						+"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
						+"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		CharSequence inputStr = email;
		Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if(matcher.matches())
			return true;
		else
			return false;
	}

	//////////////////////////////////// sending values to server.//////////////////////////////////
	//TODO Social reg and normal reg classes
	private class UserRegistrationAsyncTask extends AsyncTask<String, Void, String>{
		private String resultres = "";

		@Override
		protected void onPreExecute() {
			//Log.d(TAG,"userRegUrl-->"+userRegUrl);
			mProgressDialog = new ProgressDialog(StorluvLoginActvity.this);
			mProgressDialog.setMessage("loading");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
		@Override
		protected String doInBackground(String... arg0) {
			String response = "";

			try {
				DefaultHttpClient hc = new DefaultHttpClient();  
				ResponseHandler <String> res = new BasicResponseHandler();  
				HttpPost postMethod = new HttpPost(userRegUrl.replace(" ","%20")); 

				ByteArrayEntity regEbtity = null ;
				if(mUserImage == null) {
					Bitmap croppedPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.card_avatar_bar);
					//encoding the image to string so that it can be saved into database
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					croppedPhoto.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
					mUserImage = baos.toByteArray();
					croppedPhoto = null;
				} 
				regEbtity = new ByteArrayEntity(mUserImage);
				//Sending data
				postMethod.setEntity(regEbtity);
				response = hc.execute(postMethod,res);
				Log.e(TAG,"result-->"+response);

				if(response != null) {
					mCache.put("login resonse", response);

					JSONObject reponseObject = new JSONObject(response);
					JSONObject resultObject = reponseObject.getJSONObject("Insert_UserInfoResult");
					response = resultObject.getString("UserId").trim();
					resultres = resultObject.getString("Msg");

					mAppPreferences.saveUserImageUrl(resultObject.getString("Photo"));
					mAppPreferences.saveUserName(resultObject.getString("UserName"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}
		@Override
		protected void onPostExecute(String response) {
			//Log.e(TAG,"result-->"+result);

			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
			if(response.equalsIgnoreCase("0") || response.equalsIgnoreCase("")) {
				Toast.makeText(StorluvLoginActvity.this, resultres, Toast.LENGTH_SHORT).show();
			} else {
				mAppPreferences.saveUserid(response);
				onBack();
			}
		}
		@Override
		protected void onCancelled() {
			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
			this.cancel(true);
		}
	}

	//Social reg and normal reg classes
	private class SocialRegistrationAsyncTask extends AsyncTask<String, Void, String>{
		private String resultres = "";

		@Override
		protected void onPreExecute() {

			mProgressDialog = new ProgressDialog(StorluvLoginActvity.this);
			mProgressDialog.setMessage("loading");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
		@Override
		protected String doInBackground(String... arg0) {

			String response = "";
			URLConnection tc1;
			try {

				URL tableConnect = new URL(userRegUrl.replace(" ","%20"));
				tc1 = tableConnect.openConnection(); 
				tc1.setReadTimeout(Globals.readTimeOut);
				tc1.setConnectTimeout(Globals.readTimeOut);
				BufferedReader input = new BufferedReader(new InputStreamReader(tc1.getInputStream()));
				String line2;
				String mydata = "";
				//reading the content of the result
				while ((line2 = input.readLine()) != null) {
					//Gets all the characters and adding them into myData string
					mydata = mydata + line2;
				}
				Log.e(TAG,"mydata--"+mydata);
				if(mydata.length() != 0) {
					mCache.put("login resonse", mydata);

					JSONObject reponseObject = new JSONObject(mydata);
					JSONObject resultObject = reponseObject.getJSONObject("Insert_UserInfoDetailsResult");
					response = resultObject.getString("UserId").trim();
					resultres = resultObject.getString("Msg");

					mAppPreferences.saveUserImageUrl(resultObject.getString("Photo"));
					mAppPreferences.saveUserName(resultObject.getString("UserName"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}
		@Override
		protected void onPostExecute(String response) {

			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
			if(response.equalsIgnoreCase("0") || response.equalsIgnoreCase("")) {
				Toast.makeText(StorluvLoginActvity.this, resultres, Toast.LENGTH_SHORT).show();
			} else {
				mAppPreferences.saveUserid(response);
				if(skipviewEnable == 1){
					Intent in = new Intent(StorluvLoginActvity.this,StoreLoveActivity.class);
					startActivity(in);
					onBack();
				} else {
					onBack();					
				}
			}
		}
		@Override
		protected void onCancelled() {
			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
			this.cancel(true);
		}
	}

	//Social reg and normal reg classes
	private class LoginAsyncTask extends AsyncTask<String, Void, String>{

		String resultres = "";

		@Override
		protected void onPreExecute() {

			mProgressDialog = new ProgressDialog(StorluvLoginActvity.this);
			mProgressDialog.setMessage("loading");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
		@Override
		protected String doInBackground(String... arg0) {

			String response = "";
			//Declaring the URL connection object
			URLConnection tc1;
			try {

				URL tableConnect = new URL(userRegUrl.replace(" ","%20"));
				tc1 = tableConnect.openConnection(); 
				tc1.setReadTimeout(Globals.readTimeOut);
				tc1.setConnectTimeout(Globals.readTimeOut);
				BufferedReader input = new BufferedReader(new InputStreamReader(tc1.getInputStream()));
				String line2;
				String mydata = "";
				//reading the content of the result
				while ((line2 = input.readLine()) != null) {
					//Gets all the characters and adding them into myData string
					mydata = mydata + line2;
				}

				Log.e(TAG,"mydata--"+mydata);
				if(mydata.length() != 0) {
					mCache.put("login resonse", mydata);

					JSONObject reponseObject = new JSONObject(mydata);
					JSONObject resultObject = reponseObject.getJSONObject("GET_USERCREDENTIALSResult");
					response = resultObject.getString("UserId").trim();
					resultres = resultObject.getString("Msg");

					mAppPreferences.saveUserImageUrl(resultObject.getString("Photo"));
					mAppPreferences.saveUserName(resultObject.getString("UserName"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}
		@Override
		protected void onPostExecute(String response) {

			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
			if(response.equalsIgnoreCase("0") || response.equalsIgnoreCase("")) {
				Toast.makeText(StorluvLoginActvity.this, resultres, Toast.LENGTH_SHORT).show();
			} else {
				mAppPreferences.saveUserid(response);
				onBack();
			}
		}
		@Override
		protected void onCancelled() {
			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}
			this.cancel(true);
		}
	}
	//https://github.com/m3n0R/EasyFacebookConnect/blob/master/EasyFacebookConnect-Samples/src/com/menor/easyfacebookconnect/samples/HomeActivity.java

	@Override
	public void onBackPressed(){
		super.onBackPressed();

		if(reg_scrollView.getVisibility() == View.VISIBLE) {

			reg_scrollView.setVisibility(View.GONE);
			login_scrollView.setVisibility(View.VISIBLE);

		} else {
			onBack();
		}
	}

	private void onBack(){
		finish();
		overridePendingTransition(0, R.anim.pull_out);
	}

	//	private void getHashKey(){
	//		try {
	//			PackageInfo info = getPackageManager().getPackageInfo(
	//					"com.thisisswitch.storelove", 
	//					PackageManager.GET_SIGNATURES);
	//			for (Signature signature : info.signatures) {
	//				MessageDigest md = MessageDigest.getInstance("SHA");
	//				md.update(signature.toByteArray());
	//				
	//				Log.v("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	//			}
	//		} catch (NameNotFoundException e) {
	//
	//		} catch (NoSuchAlgorithmException e) {
	//
	//		}
	//	}
}
