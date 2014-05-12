package com.revo21.gotguide;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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
	// controls
	private WebView mWebView, mErrorWebview;
	private ImageView mSplashImage;
	private ActionBar mActionBar;
	private View mProgressView;
	private LayoutParams mProgressBarLayoutParams;
	
	// const
	private static final long SPLASH_TIME = 7000;
	private static final String URL = "http://viewers-guide.hbo.com/";
	private static final String EPISODE_URL_PREFIX = "http://viewers-guide.hbo.com/game-of-thrones/";

	private static final String FAILED_URL = "file:///android_asset/error/error-screen.html";
	private static final String FIRST_TIME_KEY = "first_time";
	private static final String JS_TOGGLE_MENU = "javascript:$('body').toggleClass('side-nav-opened');Chaplin.mediator.publish('app:hidenav');void(0);";
	private static final String JS_REMOVE_NAV_BAR = "javascript:document.querySelector('.global-nav').style.display='none';document.querySelector('.page-container>div:first-child').style.marginTop=0;void(0);";
	private static final String JS_REMOVE_NAV_BAR_MAP = "javascript:document.querySelector('.page-container>div:first-child').style.top=0;$('#map').height($(window).height());void(0);";
	// flags
	private static final int _firstTimeCount = 3;
	private boolean _triggerHint = false;
	private boolean _urlLoaded = false;
	private boolean _canLoadURL = false;
	private boolean _pageNotFound = false;
	private long _startTime = 0;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		initActionBar();
		
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
	
	private void initActionBar() {
		// first hide action bar (will display again afer splash)
		mActionBar = getActionBar(); 
		mActionBar.hide();
		
		// handle home click
		View actionBarHomeArea = LayoutInflater.from(this).inflate(R.layout.action_bar_custom_home, null);
		actionBarHomeArea.findViewById(R.id.action_bar_home_area).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mWebView.getUrl().startsWith(EPISODE_URL_PREFIX)) {
					mWebView.loadUrl(JS_TOGGLE_MENU);
				}
			}
		});
		
		// set custom home area into view
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		mActionBar.setCustomView(actionBarHomeArea);
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
		mErrorWebview = (WebView) findViewById(R.id.error_webview);
		mErrorWebview.loadUrl(FAILED_URL);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				setProgressBarPercent(0);
				mProgressView.setVisibility(View.VISIBLE);
				if (mErrorWebview.getVisibility() == View.VISIBLE) {
					mErrorWebview.setVisibility(View.INVISIBLE);
					mErrorWebview.loadUrl("about:blank");
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d("webview", "page finished");
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
				
				if (url.startsWith(EPISODE_URL_PREFIX)) {
					view.loadUrl(JS_REMOVE_NAV_BAR);
				}
				
				if (url.endsWith("/map")) {
					view.loadUrl(JS_REMOVE_NAV_BAR_MAP);
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				mProgressView.setVisibility(View.INVISIBLE);
				view.stopLoading();
				if (mErrorWebview.getVisibility() == View.INVISIBLE) {
					mErrorWebview.setVisibility(View.VISIBLE);
					mErrorWebview.loadUrl(FAILED_URL);
				}
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			/*
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
			}*/

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
		/*
		if (mWebView != null) {
			mWebView.clearCache(true);
		}
		*/		
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
