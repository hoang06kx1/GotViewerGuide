package co.k2lab.gotguide.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import co.k2lab.gotguide.noads.R;

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
			if (buttonMessage == null) buttonMessage = activity.getResources().getString(R.string.ok);
			alertBuilder.setNeutralButton(buttonMessage, onClick);
			alertBuilder.create().show();
		}
	}

	public static void AlertIfNoNetworkConnection(Activity activity, String title, final Callback.AlertCallback callback) {
		if (!Utils.isNetworkEnabled(activity)) {
			Resources res = activity.getResources();
			AlertMessage(activity, title, res.getString(R.string.network_required_text), res.getString(R.string.got_it), new DialogInterface.OnClickListener() {
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
