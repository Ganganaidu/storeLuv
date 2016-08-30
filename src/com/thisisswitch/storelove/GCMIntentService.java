package com.thisisswitch.storelove;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.thisisswitch.storelove.gcm.GCMUtilClient;
import com.thisisswitch.storelove.services.GCMAsyncTask;

/**
 * IntentService responsible for handling GCM messages.
 */

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	
	Bundle extras = null;
	String  alertMsg = null;
	String tickerText = "";

	public GCMIntentService() {
		super(GCMUtilClient.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		//Log.i(TAG, "Device registered: regId = " + registrationId);
		new GCMAsyncTask().execute(registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		//Log.i(TAG, "Device unregistered");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			//ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			//Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		//Bundle[{collapse_key=do_not_collapse, from=506249494088, badge=1, alert=ganga, sound=sound.caf}]
		Log.e(TAG, "Received message--"+intent.getExtras());
		extras = intent.getExtras();
		String message = extras.getString("alert");
		//tickerText  = extras.getString("tickerText");
		//String contentTitle  = extras.getString("contentTitle");
		//String contentText  = extras.getString("contentText");
		
//		if(Networking.isNetworkAvailable(this)) {
//			if(!Globals.isNotificationServiceRunning(this)) {
//				startService(new Intent(this, NotificationService.class));
//			}
//		} else {}	
		// notifies user
		generateNotification(context, message,tickerText,getResources().getString(R.string.app_name));
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		//Log.i(TAG, "Received deleted messages notification"+total);
		// notifies user
		//	displayNotification(context, "Received deleted messages notification"+total,tickerText);
	}

	@Override
	public void onError(Context context, String errorId) {
		//Log.i(TAG, "Received error: " + errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message,String tickerText,String contentTitle) {

		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		int notification_icon = R.drawable.ic_launcher;
		//long when = System.currentTimeMillis();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(notification_icon)
		.setContentTitle(contentTitle)
		.setAutoCancel(true)
		.setSound(soundUri)
		.setContentText(message);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, SplashScreenActivity.class);
		resultIntent.putExtra("pushticker", tickerText);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(SplashScreenActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}
}
