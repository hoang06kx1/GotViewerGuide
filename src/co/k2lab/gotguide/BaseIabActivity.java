package co.k2lab.gotguide;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

public class BaseIabActivity extends Activity {
	IInAppBillingService mService;
	protected static final String oneDollarId = "";
	protected static final String twoDollarsId = "";
	protected static final String fiveDollarsId = "";
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

	protected void purchaseAnProduct(String sku) {
		try {
			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					sku, "inapp", PAYLOAD_STRING);
			if (buyIntentBundle.getInt("RESPONSE_CODE") == 0) { // TODO: implement else condition
				PendingIntent purchaseIntent = buyIntentBundle
						.getParcelable("BUY_INTENT");
				startIntentSenderForResult(purchaseIntent.getIntentSender(),
						REQUEST_CODE, new Intent(), Integer.valueOf(0),
						Integer.valueOf(0), Integer.valueOf(0));
			}
		} catch (RemoteException | SendIntentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
					// TODO: implement Notification here
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

}
