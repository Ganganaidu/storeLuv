package com.thisisswitch.storelove.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;

public class GCMAsyncTask extends AsyncTask<String, Void, Boolean> {

	JSONObject jsonObject;

	protected Boolean doInBackground(String... params) {
		return getRespose(AppUrls.registerGCMInfo(params[0]));
	}

	protected void onPostExecute(Boolean result) {
		if (result) {
			Log.d("","insert push service result from the server" + jsonObject.toString());
		} else {
		}
	}

	protected void onPreExecute() {
	}

	private boolean getRespose(String AuthenticationUrl) {

		// Declaring the JSON object
		// Declaring the URL connection object
		URLConnection tc1;
		try {
			// connecting to the URL and retrieving the response
			URL tableConnect = new URL(AuthenticationUrl);
			tc1 = tableConnect.openConnection();
			tc1.setReadTimeout(Globals.readTimeOut);
			tc1.setConnectTimeout(Globals.readTimeOut);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					tc1.getInputStream()));
			String line2;
			String mydata = "";
			// reading the content of the result
			while ((line2 = input.readLine()) != null) {
				// Gets all the characters and adding them into myData string
				mydata = mydata + line2;
			}
			// placing the result into a JSON object
			//System.out.println("result from the server is"+mydata);
			jsonObject = new JSONObject(mydata);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
