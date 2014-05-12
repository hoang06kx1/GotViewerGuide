package co.k2lab.gotguide.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Utils {
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in {@link #setId(int)}. This value will
	 * not collide with ID values generated at build time by aapt for R.id.
	 * 
	 * @return a generated ID value
	 */
	public static int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range
			// under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	public static boolean isNullOrEmpty(String s) {
		return (s == null || s.isEmpty());
	}

	private static ConnectionType checkConnectionType(Context context) {
		try {
			ConnectivityManager conMan = ((ConnectivityManager) context.getSystemService(
							Context.CONNECTIVITY_SERVICE));
			boolean isWifiEnabled = conMan.getNetworkInfo(
					ConnectivityManager.TYPE_WIFI).isConnected();
			if (isWifiEnabled)
				return ConnectionType.WIFI;

			boolean isNetworkDisconnect = (conMan.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED);
			boolean isDataDisable = conMan
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getReason().equals("dataDisabled");
			boolean is3GEnabled = !(isNetworkDisconnect && isDataDisable);
			if (is3GEnabled)
				return ConnectionType.CELLULAR;

			return ConnectionType.NO_NETWORK;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("Network", "Network hardware-unidentified problem!");
			return ConnectionType.NO_NETWORK;
		}
	}

	/**
	 * Return network status
	 * network status.
	 * 
	 * @return true: network is enabled; false: network is unavailable
	 */
	public static boolean isNetworkEnabled(Context context) {
		if (checkConnectionType(context) == ConnectionType.NO_NETWORK) {
			return false;
		}
		return true;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static Point getScreenDpResolution(Context context) {
		Point res = new Point();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT < 13) {
			res.x = display.getWidth(); // deprecated
			res.y = display.getHeight(); // deprecated
		} else {
			display.getSize(res);
		}
		res.x = convertPixelToDp(context, res.x);
		res.y = convertPixelToDp(context, res.y);
		return res;
	}

	public static int convertPixelToDp(Context context, int pixel) {
		DisplayMetrics metrics = context.getResources()
				.getDisplayMetrics();
		return (pixel / metrics.densityDpi) * 160;
	}

	public static int convertDpToPixel(Context context, int dp) {
		DisplayMetrics metrics = context.getResources()
				.getDisplayMetrics();
		return (int)(((float)dp / 160) * metrics.densityDpi);
	}
	
	public static Long getCurrentUnixTime() {
		return System.currentTimeMillis() / 1000;
	}
}