package com.revo21.gotguide.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.revo21.gotguide.R;

public class Alert {
	/**
	 * Alert dialog maker helper
	 * 
	 * @param activity
	 * @param title
	 * @param message
	 * @param buttonMessage
	 * @param onClick
	 */
	public static void AlertMessage(Activity activity, String title, String message, String buttonMessage, DialogInterface.OnClickListener onClick) {
		if (message != null && activity != null && !activity.isFinishing()) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
			alertBuilder.setTitle(title).setMessage(message)
					.setCancelable(true);
			if (buttonMessage == null) buttonMessage = "OK";
			alertBuilder.setNeutralButton(buttonMessage, onClick);
			alertBuilder.create().show();
		}
	}

	public static void AlertIfNoNetworkConnection(Activity activity, String title, final Callback.AlertCallback callback) {
		if (!Utils.isNetworkEnabled(activity)) {
			AlertMessage(activity, title, activity.getResources().getString(R.string.network_require), "Got it!", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (callback != null) {
						callback.onPressButton();
					}
				}
			});
		}
	}
}
