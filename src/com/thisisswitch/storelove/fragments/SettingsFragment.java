package com.thisisswitch.storelove.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.thisisswitch.storelove.AboutUsActivity;
import com.thisisswitch.storelove.R;
import com.thisisswitch.storelove.RegionsListActivity;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.socialnetwork.StorluvLoginActvity;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;

public class SettingsFragment extends SherlockFragment implements OnClickListener{

	private Button profile_button,cange_region_button,
	about_button,login_button;

	private AppPreferences mAppPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppPreferences = new AppPreferences(getActivity());
	}
	//second this view called
	// Initialize all views here
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		View view = inflater.inflate(R.layout.fragment_settings, container, false);

		profile_button = (Button)view.findViewById(R.id.profile_button);
		cange_region_button = (Button)view.findViewById(R.id.cange_region_button);
		about_button = (Button)view.findViewById(R.id.about_button);
		login_button = (Button)view.findViewById(R.id.login_button);

		return view;
	}

	// after onCreateview this method will call..so put all listeners in this class
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		profile_button.setOnClickListener(this);
		cange_region_button.setOnClickListener(this);
		about_button.setOnClickListener(this);
		login_button.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		Intent in = null;

		switch (v.getId()) {

		case R.id.profile_button:


			break;
		case R.id.cange_region_button:

			if(Networking.isNetworkAvailable(getActivity())) {
				in = new Intent(getActivity(),RegionsListActivity.class);
				startActivity(in);
				getActivity().overridePendingTransition(R.anim.pull_up, R.anim.slide_bottom_out);
			} else {
				Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.about_button:

			if(Networking.isNetworkAvailable(getActivity())) {
				in = new Intent(getActivity(),AboutUsActivity.class);
				startActivity(in);
				getActivity().overridePendingTransition(R.anim.pull_up, R.anim.slide_bottom_out);
			} else {
				Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.login_button:

			if(Networking.isNetworkAvailable(getActivity())) {
				logOut();
			} else {
				Toast.makeText(getActivity(), Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
			}

			break;
		default:
			break;
		}		
	}

	@Override
	public void onResume(){
		super.onResume();
		setValues();
	}

	private void setValues() {

		profile_button.setText(" Profile ");
		cange_region_button.setText(" Change Region -"+mAppPreferences.getRegionName());
		about_button.setText("About");

		if(mAppPreferences.getUserid().equals("") || mAppPreferences.getUserid().equals("0")) {
			mAppPreferences.saveUserid("0");
			login_button.setText("Login");	
		} else {
			login_button.setText("Logout");	
		}
	}

	private void logOut() {

		if(mAppPreferences.getUserid().equals("")){
			mAppPreferences.saveUserid("0");
		}
		if(!mAppPreferences.getUserid().equals("0")) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getResources().getString(R.string.app_name));
			builder.setMessage("Are you sure you want to log out");
			builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					mAppPreferences.saveUserid("");
					mAppPreferences.saveUserName("");
					mAppPreferences.saveUserImageUrl("");
					mAppPreferences.saveUserSocialid("");
					mAppPreferences.saveLoginType("");
					navigateToLoginPage();	
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			navigateToLoginPage();						
		}
	}

	private void navigateToLoginPage() {
		Intent in = new Intent(getActivity(),StorluvLoginActvity.class);
		startActivity(in);
		//getActivity().overridePendingTransition(R.anim.pull_up, R.anim.slide_bottom_out);
		getActivity().overridePendingTransition(R.anim.slide_bottom_in, R.anim.pull_out);
	}
}
