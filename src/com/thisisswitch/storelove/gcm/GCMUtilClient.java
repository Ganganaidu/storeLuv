package com.thisisswitch.storelove.gcm;

import android.content.Context;

import com.google.android.gcm.GCMRegistrar;
import com.thisisswitch.storelove.preferences.AppPreferences;
/**
 * This class contains methods to register a device
 * to GCM server, making sure the device supports
 * GCM service.
 *
 */
public class GCMUtilClient {
	/**
	 * GCM Sender ID.
	 * 
	 * todo
	 */
	public static final String SENDER_ID = "506249494088";
	//private static final String TAG = "GCMUtilClient";
     private static AppPreferences mAppPreferences;
	/**
	 * Registers the device to the GCM server.
	 */
	public static void register(Context context) {
		try {
			//GCMRegistrar.checkDevice(context);
			//GCMRegistrar.checkManifest(context);
			
			mAppPreferences = new AppPreferences(context);
			final String registrationId = GCMRegistrar.getRegistrationId(context);
			mAppPreferences.savePushRegid(registrationId);
			
			if (registrationId.equals("")) {
				GCMRegistrar.register(context, SENDER_ID);
			} else {
				//Log.d(TAG, "The device has already been registered");
			}
		} catch (UnsupportedOperationException uoe) {
			uoe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//end of register()

	public static void unRegister(Context context) {
		GCMRegistrar.unregister(context);
	}//end of register()

}//end of class
