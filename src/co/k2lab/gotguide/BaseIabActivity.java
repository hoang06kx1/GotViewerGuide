package co.k2lab.gotguide;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import co.k2lab.gotguide.utils.Alert;

import com.android.vending.billing.IInAppBillingService;

public class BaseIabActivity extends Activity {
	IInAppBillingService mService;
	public static final String SKU_ONE_DOLLAR = "onedollar";
	// protected static final String SKU_ONE_DOLLAR = "android.test.purchased";	
	public static final String SKU_TWO_DOLLARS = "twodollars";
	public static final String SKU_FIVE_DOLLARS = "fivedollars";
	private static final String PAYLOAD_STRING = "1234567890qwertyuiop+_)(*&^%$#@!";
	private static final int REQUEST_CODE = 32145;

	ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
		}
	};

	public BaseIabActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindService(new Intent(
				"com.android.vending.billing.InAppBillingService.BIND"),
				mServiceConn, Context.BIND_AUTO_CREATE);
	}

	public void purchaseProduct(String sku) {
		try {
			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					sku, "inapp", PAYLOAD_STRING);
			int responseCode = buyIntentBundle.getInt("RESPONSE_CODE");
			if (responseCode == 0) { // TODO: implement else condition
				PendingIntent purchaseIntent = buyIntentBundle
						.getParcelable("BUY_INTENT");
				startIntentSenderForResult(purchaseIntent.getIntentSender(),
						REQUEST_CODE, new Intent(), Integer.valueOf(0),
						Integer.valueOf(0), Integer.valueOf(0));
			} else {
				Log.d("IAB", responseCode + "");
			}
		} catch (RemoteException | SendIntentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected int consumeProduct(String token) {
		if (token != null && !token.equals("")) {			 
			try {
				return mService.consumePurchase(3, getPackageName(), token);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}	            
		}
		return -1;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			//if  data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			// String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
			if (resultCode == RESULT_OK) {
				try {
					JSONObject jo = new JSONObject(purchaseData);
					String sku = jo.getString("productId");
					String token = jo.getString("purchaseToken");
					String donationMessage = "";
					if (sku.contains(SKU_ONE_DOLLAR)) {
						donationMessage = getResources().getString(R.string.one_dollar_message);
					} else if (sku.contains(SKU_TWO_DOLLARS)) {
						donationMessage = getResources().getString(R.string.two_dollars_message);
					} else if (sku.contains(SKU_FIVE_DOLLARS)) {
						donationMessage = getResources().getString(R.string.five_dollars_message);
					}
					Alert.AlertMessage(this, getResources().getString(R.string.donation_title), donationMessage, "Continue...", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					// TODO: implement Notification here
					// Consume purchase 
					new consumeProductAsync().execute(token);
				} catch (JSONException e) {
					// TODO: notify about failed purchase
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mService != null) {
			unbindService(mServiceConn);
		}
	}
	
	public class consumeProductAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {			
			return Integer.valueOf(consumeProduct(params[0]));
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result == -1) {
				Log.d("IAB", "Consume purchase failed");
			} else {
				Log.d("IAB", "Consume purchase status:" + result);
			}
		}
	}
}
