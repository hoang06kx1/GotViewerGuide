package co.k2lab.gotguide.controls;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import co.k2lab.gotguide.BaseIabActivity;
import co.k2lab.gotguide.MainActivity;
import co.k2lab.gotguide.R;

public class DonateDialog extends Dialog implements android.view.View.OnClickListener{
	private Button mButton1, mButton2, mButton5;
	private MainActivity mActivity;
	
	public DonateDialog(Context context) {
		super(context);
	}

	public DonateDialog(Context context, int theme) {
		super(context, theme);
	}

	public DonateDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener, MainActivity activity) {
		super(context, cancelable, cancelListener);
		mActivity = activity;
	}
	
	
	private void initControls() {
		mButton1 = (Button)findViewById(R.id.one_dollar_button);
		mButton2 = (Button)findViewById(R.id.two_dollars_button);
		mButton5 = (Button)findViewById(R.id.five_dollars_button);
		mButton1.setOnClickListener(this);
		mButton2.setOnClickListener(this);
		mButton5.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (mActivity != null) {
			String productId = "";
			if (v.getId() == R.id.one_dollar_button) {
				productId = BaseIabActivity.SKU_ONE_DOLLAR;
			} else if (v.getId() == R.id.two_dollars_button) {
				productId = BaseIabActivity.SKU_TWO_DOLLARS;
			} else if (v.getId() == R.id.five_dollars_button) {
				productId = BaseIabActivity.SKU_FIVE_DOLLARS;
			}
			if (!productId.equals("")) {
				// mActivity.purchaseProduct(productId);
			}
		}
		this.dismiss();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_donate);
		initControls();
	}
	
	
}
