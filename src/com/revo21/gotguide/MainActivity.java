package com.revo21.gotguide;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.revo21.gotguide.utils.Callback;
import com.revo21.gotguide.utils.CustomDialog;
import com.revo21.gotguide.utils.Utils;

public class MainActivity extends Activity {
	private WebView mWebView;
	private ImageView mSplashImage;
	private static final String URL = "http://viewers-guide.hbo.com/";

	private static final String FAILED_URL = "file:///android_asset/error/error-screen.html";
	private static final String FIRST_TIME_KEY = "first_time";
	private static final int _firstTimeCount = 3;
	private static final long SPLASH_TIME = 7000;
	private View mProgressView;
	private LayoutParams mProgressBarLayoutParams;
	private boolean _triggerHint = false;
	private boolean _urlLoaded = false;
	private boolean _canLoadURL = false;
	private boolean _pageNotFound = false;
	private long _startTime = 0;
	
	private ActionBar mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// first hide action bar on splash screen
		mActionBar = getActionBar(); 
		mActionBar.hide();
		
		// init action bar
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);		
		mActionBar.setCustomView(R.layout.action_bar_custom_home);
		
		setContentView(R.layout.activity_main);
		_canLoadURL = checkNetworkConnection();
		if (_canLoadURL) {
			initControlViews();
			checkFirstTime();
			_startTime = System.currentTimeMillis();
			mWebView.loadUrl(URL);
			// remove splash after xx seconds
			mSplashImage.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (mSplashImage != null) {
						hideSplash();
						getActionBar().show();
					}
				}
			}, SPLASH_TIME);
		}
	}

	private boolean checkNetworkConnection() {
		boolean canLoadUrl = Utils.isNetworkEnabled(getApplicationContext());
		if (!canLoadUrl) {
			final CustomDialog errorDialog = new CustomDialog(this, getResources()
					.getString(R.string.network_require),
					R.drawable.error_pop_up_bg, null);
			errorDialog.setCallback(new Callback.AlertCallback() {

						@Override
						public void onPressButton() {
							errorDialog.dismiss();
							finish();
						}
					});
			errorDialog.show();
		}
		return canLoadUrl; 
	}

	private void checkFirstTime() {
		if (_canLoadURL) {
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			int firstTime = preferences.getInt(FIRST_TIME_KEY, 0);
			if (firstTime < _firstTimeCount) {
				if (firstTime == 0) {
					_triggerHint = true;
				}
				mSplashImage.setImageResource(R.drawable.splash_first_time);
				preferences.edit().putInt(FIRST_TIME_KEY, ++firstTime).commit();
			} else {
				mSplashImage.setImageResource(R.drawable.splash);
			}
		}
	}

	private void hideSplash() {
		mSplashImage.setVisibility(View.GONE);
		mSplashImage = null;
		if (_triggerHint) {
			mWebView.postDelayed(new Runnable() {

				@Override
				public void run() {
					showHint();
				}
			}, 500);
		}
	}

	private void showHint() {		
		final CustomDialog hintDialog = new CustomDialog(this, getResources().getString(R.string.hint), R.drawable.hint_pop_up_bg, null);
		hintDialog.setCallback(new Callback.AlertCallback() {
			
			@Override
			public void onPressButton() {
				hintDialog.dismiss();
			}
		});
		hintDialog.show();
	}

	private void setProgressBarPercent(int percent) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = metrics.widthPixels;		
		mProgressBarLayoutParams.width = (int)((float)screenWidth * (float)percent/100);
		mProgressView.setLayoutParams(mProgressBarLayoutParams);
		
	}
	
	private void initControlViews() {
		mSplashImage = (ImageView) findViewById(R.id.main_splash);
		mProgressView = (View) findViewById(R.id.progress_bar);
		mProgressBarLayoutParams = mProgressView.getLayoutParams();
		mWebView = (WebView) findViewById(R.id.main_webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				setProgressBarPercent(0);
				mProgressView.setVisibility(View.VISIBLE);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				
				mProgressView.setVisibility(View.INVISIBLE);
				
				// removed feature!
				/*
				_urlLoaded = true;
				long currentTime = System.currentTimeMillis();
				if (mSplashImage != null
						&& (currentTime - _startTime > SPLASH_TIME)) {
					hideSplash();
				}
				
				*/
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				mProgressView.setVisibility(View.INVISIBLE);
				view.stopLoading();
				view.loadUrl(FAILED_URL);
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				CharSequence pnotfound = "The page cannot be found";
				if (title.contains(pnotfound)) {
					_pageNotFound = true;
					mProgressView.setVisibility(View.INVISIBLE);
					view.stopLoading();
					view.loadUrl(FAILED_URL);
				}
			}

			public void onProgressChanged(WebView view, int progress) {
				setProgressBarPercent(progress);
			}
		});

	}

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWebView != null) {
			// mWebView.clearCache(true);
		}		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_refresh:
	            mWebView.reload();
	            return true;
	        case R.id.action_episode:
	            // TODO openRightDrawer();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
