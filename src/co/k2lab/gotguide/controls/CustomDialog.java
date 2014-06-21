package co.k2lab.gotguide.controls;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import co.k2lab.gotguide.R;
import co.k2lab.gotguide.utils.Callback;

public class CustomDialog extends Dialog {
	private TextView mTextView;
	private Button mButton;	
	private Callback.AlertCallback mCallback;
	private ImageView mImageView;
	private String mContent;
	private int mDrawableId;
	
	public CustomDialog(Context context, String content, int drawableId, Callback.AlertCallback callback) {
		super(context, false, null);		
		mCallback = callback;
		mContent = content;
		mDrawableId = drawableId;
	}

	public void setCallback(Callback.AlertCallback callback) {
		mCallback = callback;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_error);
		mImageView = (ImageView) findViewById(R.id.background_image);
		mImageView.setImageResource(mDrawableId);		
		mTextView = (TextView) findViewById(R.id.textview);
		mTextView.setText(mContent);
		mButton = (Button) findViewById(R.id.button);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onPressButton();
				}
			}
		});
	}
}
