package co.k2lab.gotguide;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import co.k2lab.gotguide.controls.CustomDialog;
import co.k2lab.gotguide.controls.VideoEnabledWebChromeClient;
import co.k2lab.gotguide.controls.VideoEnabledWebView;
import co.k2lab.gotguide.model.Episode;
import co.k2lab.gotguide.model.Season;
import co.k2lab.gotguide.utils.Callback;
import co.k2lab.gotguide.utils.Utils;

import com.startapp.android.publish.StartAppSDK;
import com.xhydo.qfmoy192870.AdListener;
import com.xhydo.qfmoy192870.AdListener.AdType;
import com.xhydo.qfmoy192870.Prm;

public class MainActivity extends Activity implements OnChildClickListener,
		OnGroupClickListener {
	// controls
	private VideoEnabledWebView mWebView;
	private VideoEnabledWebChromeClient mWebChromeClient;
	private WebView mErrorWebview;
	private ImageView mSplashImage;
	private ActionBar mActionBar;
	private View mProgressView;
	private LayoutParams mProgressBarLayoutParams;
	private RightDrawerAdapter mRightDrawerAdapter;
	private LeftDrawerAdapter mLeftDrawerAdapter;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mRightExpandableListView;
	private ExpandableListView mLeftExpandableListView;

	// const
	private static final int SPLASH_TIME = 7000;
	private static final long AD_DURATION = 1800000; // 30 minutes
	// private static final long AD_DURATION = 10000; // 10 seconds.
	private static final long LONG_AD_DURATION = 3600000; // 1 hour
	// private static final long LONG_AD_DURATION = 20000; // 20 seconds
	private static final String FIRST_TIME_KEY = "first_time";

	private static final String URL_HOME = "http://viewers-guide.hbo.com/";
	private static final String URL_DEFAULT_EPISODE = "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-10/home/40";
	private static final String URL_MAP = "http://viewers-guide.hbo.com/game-of-thrones/map";
	private static final String URL_HOUSES = "http://viewers-guide.hbo.com/game-of-thrones/houses";
	private static final String URL_PEOPLE = "http://viewers-guide.hbo.com/game-of-thrones/people";
	private static final String URL_APPENDIX = "http://viewers-guide.hbo.com/game-of-thrones/appendix";

	private static final String JS_SET_LANG_EN = "javascript:Chaplin.mediator.publish('site:changelanguage','en');void 0";
	private static final String JS_SET_LANG_ES = "javascript:Chaplin.mediator.publish('site:changelanguage','es');void 0";
	private static final String JS_SET_SPOILER_ON = "javascript:Chaplin.mediator.publish('global-episodes:toggleSpoiler',true);void 0";
	private static final String JS_SET_SPOILER_OFF = "javascript:Chaplin.mediator.publish('global-episodes:toggleSpoiler',false);void 0";

	private static final String JS_TOGGLE_MENU = "javascript:$('body').toggleClass('side-nav-opened');Chaplin.mediator.publish('nav:closeEpisodeSelector');Chaplin.mediator.publish('app:hidenav');void 0";
	private static final String JS_REMOVE_NAV_BAR = "javascript:if(typeof removeNavBar!='function'){function removeNavBar(){var e=10;var t=document.querySelector('.global-nav');if(t){if(!t.style.display){t.style.display='none';document.querySelector('.page-container>div:first-child').style.marginTop=0;document.querySelector('.close-icon.sprites-close').style.display='none'}}else if(e--)setTimeout(removeNavBar,1e3)}}removeNavBar();void 0";
	private static final String JS_ADD_URL_CHANGE_LISTENER = "javascript:if(typeof removeNavBar!='function'){var removeNavBar=function(){var e=10;var t=document.querySelector('.global-nav');if(t){if(!t.style.display){t.style.display='none';document.querySelector('.page-container>div:first-child').style.marginTop=0;document.querySelector('.close-icon.sprites-close').style.display='none'}}else if(e--){setTimeout(removeNavBar,1e3)}}}if(typeof removePaddingMap!='function'){var removePaddingMap=function(){var e=10;var t=document.querySelector('.page-container>div:first-child');if(t){if(t.style.top){t.style.top=0;$('#map').height($(window).height())}}else if(e--)setTimeout(removePaddingMap,1e3)}}var lastLocation;if(typeof checkUrl!='function'){var checkUrl=function(){if(window.location.href!=lastLocation){lastLocation=window.location.href;removeNavBar();if(lastLocation.indexOf('/map')>-1)removePaddingMap()}}}window.setInterval(checkUrl,1e3);void 0";

	private static final String URL_HBO_COM = "http://www.hbo.com/";
	private static final String URL_HBO_GO = "http://www.hbogo.com/";
	private static final String URL_HBO_CONNECT = "http://connect.hbo.com/";
	private static final String URL_HBO_STORE = "http://store.hbo.com/game-of-thrones/";
	private static final String URL_FB = "https://www.facebook.com/GameOfThrones";
	private static final String URL_TB = "http://gameofthrones.tumblr.com/";
	private static final String URL_TW = "https://twitter.com/GameOfThrones";
	private static final String URL_YT = "https://www.youtube.com/user/GameofThrones";
	private static final String URL_IG = "http://instagram.com/gameofthrones";

	private static final String URL_ERROR = "file:///android_asset/error/error-screen.html";

	// flags
	private static final int mFirstTimeCount = 3;
	private boolean mShouldTriggerHint = false;
	private boolean mWebviewLoadingFinished = false;
	private boolean mIsSpoilerAlertOn = true;
	private boolean mIsLanguageEn = true;
	private boolean mEnableDrawer = false;
	// private boolean mAppStart = true;

	// data
	private ArrayList<Season> mSeasons;

	// ads
	// private StartAppAd startAppAd = new StartAppAd(this);
	private Prm prm;
	private long mAdDisplayTime = System.currentTimeMillis();
	private boolean mAdDisplayed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isNetWorkAvailable()) {
			// getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

			// start StartApp banner ad
			StartAppSDK.init(this, "106324371", "206307211");
			setContentView(R.layout.activity_main);
			
			// fix bug: drawer can be opened in Splash screen
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerLayout.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!mEnableDrawer) {
						return true;
					} else {
						return false;
					}
				}
			});
			
			initActionBar();
			initControlViews();
			initAds();
			checkFirstTime();

			JSONObject cookieJson = getCookie();
			if (cookieJson != null) {
				if (!cookieJson.optString("id").isEmpty())
					mWebView.loadUrl(URL_HOME);
				mIsSpoilerAlertOn = cookieJson.optBoolean("spoilerAlerts");
				mIsLanguageEn = cookieJson.optString("lang", "en").equals("en");
			} else {
				mWebView.loadUrl(URL_DEFAULT_EPISODE);
			}

			// remove splash after xx seconds
			mSplashImage.postDelayed(new Runnable() {

				@Override
				public void run() {					
					if (mSplashImage != null) {
						hideSplash();
						if (mActionBar != null) {
							mActionBar.show();
						}
					}
					mEnableDrawer = true;
				}
			}, SPLASH_TIME);
		}
	}

	private void initActionBar() {
		// first hide action bar (will display again afer splash)
		mActionBar = getActionBar();
		mActionBar.hide();

		// handle home click
		View actionBarHomeArea = LayoutInflater.from(this).inflate(
				R.layout.action_bar_custom_home, null);
		actionBarHomeArea.findViewById(R.id.action_bar_home_area)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
							if (mLeftDrawerAdapter != null
									&& mIsLastSettingShown != isSettingsReady()) {
								mLeftDrawerAdapter.notifyDataSetChanged();
								mIsLastSettingShown = isSettingsReady();
								// mLeftExpandableListView.requestLayout();
							}

							if (mLeftExpandableListView != null) {
								mLeftExpandableListView.expandGroup(0, false);
								mLeftExpandableListView.expandGroup(1, false);
							}
						}
						toggleLeftDrawer();
					}
				});

		// set custom home area into view
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mActionBar.setCustomView(actionBarHomeArea);
	}

	private void initAds() {
		// generate Airpush Interstitial ad
		if (prm == null)
			prm = new Prm(this, new AdListener() {

				@Override
				public void onSmartWallAdShowing() {
					Log.d("Ads", "onSmartWallAdShowing");
					mAdDisplayTime = System.currentTimeMillis();
					mAdDisplayed = true;
				}

				@Override
				public void onSmartWallAdClosed() {
					Log.d("Ads", "onSmartWallAdClosed");
				}

				@Override
				public void onSDKIntegrationError(String arg0) {
					// TODO Auto-generated method stub
					Log.d("Ads", "SDK integration error");
				}

				@Override
				public void onAdError(String arg0) {
					Log.d("Ads", "AdError " + arg0 == null ? "" : arg0);
				}

				@Override
				public void onAdCached(AdType arg0) {
					Log.d("Ads", "AdCached");
				}

				@Override
				public void noAdAvailableListener() {
					Log.d("Ads", "noAdsAvailable");

				}
			}, true);
		// cache ad
		prm.runSmartWallAd();

		// AdMob
		com.google.android.gms.ads.AdView adView = (com.google.android.gms.ads.AdView) findViewById(R.id.admob_view);

		// add device to test
		if (adView != null) {
			com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder()
					.addTestDevice("248798ED195F56341EA0C23B2B76BBFB")
					.addTestDevice("2842078051443556C35681847AD817A9")
					.addTestDevice(
							com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR)
					.build();
			adView.loadAd(adRequest);
		}
	}

	private boolean isNetWorkAvailable() {
		boolean canLoadUrl = Utils.isNetworkEnabled(getApplicationContext());
		if (!canLoadUrl) {
			final CustomDialog errorDialog = new CustomDialog(this,
					getResources().getString(R.string.network_required_text),
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
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		int firstTime = preferences.getInt(FIRST_TIME_KEY, 0);
		if (firstTime < mFirstTimeCount) {
			if (firstTime == 0) {
				mShouldTriggerHint = true;
			}
			mSplashImage.setImageResource(R.drawable.splash_first_time);			
		} else {
			mSplashImage.setImageResource(R.drawable.splash);
			if (firstTime > mFirstTimeCount + 1) {
				showAds(); // show ads after 5 times app start
			}
		}
		preferences.edit().putInt(FIRST_TIME_KEY, ++firstTime).commit();
	}

	private void hideSplash() {
		// remove splash
		mSplashImage.setVisibility(View.GONE);
		((BitmapDrawable) mSplashImage.getDrawable()).getBitmap().recycle();
		mDrawerLayout.removeView(mSplashImage);
		mSplashImage = null;

		if (mShouldTriggerHint) {
			mWebView.postDelayed(new Runnable() {

				@Override
				public void run() {
					showHint();
				}
			}, 500);
		}
	}

	private void showHint() {
		final CustomDialog hintDialog = new CustomDialog(this, getResources()
				.getString(R.string.hint_text), R.drawable.hint_pop_up_bg, null);
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
		mProgressBarLayoutParams.width = (int) ((float) screenWidth
				* (float) percent / 100);
		mProgressView.setLayoutParams(mProgressBarLayoutParams);

	}

	private ArrayList<Season> initSeasonData() {
		final Resources res = getResources();

		Season[] seasons = new Season[4];

		// season 1
		seasons[0] = new Season(res.getString(R.string.s1),
				new ArrayList<Episode>() {
					{
						add(new Episode(
								res.getString(R.string.s1_1),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-1/home/21",
								R.drawable.s1_e1, false, true));
						add(new Episode(
								res.getString(R.string.s1_2),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-2/home/22",
								R.drawable.s1_e2, false, true));
						add(new Episode(
								res.getString(R.string.s1_3),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-3/home/23",
								R.drawable.s1_e3, false, true));
						add(new Episode(
								res.getString(R.string.s1_4),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-4/home/24",
								R.drawable.s1_e4, false, true));
						add(new Episode(
								res.getString(R.string.s1_5),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-5/home/25",
								R.drawable.s1_e5, false, true));
						add(new Episode(
								res.getString(R.string.s1_6),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-6/home/26",
								R.drawable.s1_e6, false, true));
						add(new Episode(
								res.getString(R.string.s1_7),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-7/home/27",
								R.drawable.s1_e7, false, true));
						add(new Episode(
								res.getString(R.string.s1_8),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-8/home/28",
								R.drawable.s1_e8, false, true));
						add(new Episode(
								res.getString(R.string.s1_9),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-9/home/29",
								R.drawable.s1_e9, false, true));
						add(new Episode(
								res.getString(R.string.s1_10),
								"http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-10/home/30",
								R.drawable.s1_e10, false, true));
					}
				}, R.drawable.bg_season_1);

		// season 2
		seasons[1] = new Season(res.getString(R.string.s2),
				new ArrayList<Episode>() {
					{
						add(new Episode(
								res.getString(R.string.s2_1),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-1/home/11",
								R.drawable.s2_e1, false, true));
						add(new Episode(
								res.getString(R.string.s2_2),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-2/home/12",
								R.drawable.s2_e2, false, true));
						add(new Episode(
								res.getString(R.string.s2_3),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-3/home/13",
								R.drawable.s2_e3, false, true));
						add(new Episode(
								res.getString(R.string.s2_4),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-4/home/14",
								R.drawable.s2_e4, false, true));
						add(new Episode(
								res.getString(R.string.s2_5),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-5/home/15",
								R.drawable.s2_e5, false, true));
						add(new Episode(
								res.getString(R.string.s2_6),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-6/home/16",
								R.drawable.s2_e6, false, true));
						add(new Episode(
								res.getString(R.string.s2_7),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-7/home/17",
								R.drawable.s2_e7, false, true));
						add(new Episode(
								res.getString(R.string.s2_8),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-8/home/18",
								R.drawable.s2_e8, false, true));
						add(new Episode(
								res.getString(R.string.s2_9),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-9/home/19",
								R.drawable.s2_e9, false, true));
						add(new Episode(
								res.getString(R.string.s2_10),
								"http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-10/home/20",
								R.drawable.s2_e10, false, true));
					}
				}, R.drawable.bg_season_2);

		// season 3
		seasons[2] = new Season(res.getString(R.string.s3),
				new ArrayList<Episode>() {
					{
						add(new Episode(
								res.getString(R.string.s3_1),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-1/home/1",
								R.drawable.s3_e1, false, true));
						add(new Episode(
								res.getString(R.string.s3_2),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-2/home/2",
								R.drawable.s3_e2, false, true));
						add(new Episode(
								res.getString(R.string.s3_3),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-3/home/3",
								R.drawable.s3_e3, false, true));
						add(new Episode(
								res.getString(R.string.s3_4),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-4/home/4",
								R.drawable.s3_e4, false, true));
						add(new Episode(
								res.getString(R.string.s3_5),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-5/home/5",
								R.drawable.s3_e5, false, true));
						add(new Episode(
								res.getString(R.string.s3_6),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-6/home/6",
								R.drawable.s3_e6, false, true));
						add(new Episode(
								res.getString(R.string.s3_7),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-7/home/7",
								R.drawable.s3_e7, false, true));
						add(new Episode(
								res.getString(R.string.s3_8),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-8/home/8",
								R.drawable.s3_e8, false, true));
						add(new Episode(
								res.getString(R.string.s3_9),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-9/home/9",
								R.drawable.s3_e9, false, true));
						add(new Episode(
								res.getString(R.string.s3_10),
								"http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-10/home/10",
								R.drawable.s3_e10, false, true));
					}
				}, R.drawable.bg_season_3);

		// season 4
		seasons[3] = new Season(res.getString(R.string.s4),
				new ArrayList<Episode>() {
					{
						add(new Episode(
								res.getString(R.string.s4_1),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-1/home/31",
								R.drawable.s4_e1, false, true));
						add(new Episode(
								res.getString(R.string.s4_2),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-2/home/32",
								R.drawable.s4_e2, false, true));
						add(new Episode(
								res.getString(R.string.s4_3),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-3/home/33",
								R.drawable.s4_e3, false, true));
						add(new Episode(
								res.getString(R.string.s4_4),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-4/home/34",
								R.drawable.s4_e4, false, true));
						add(new Episode(
								res.getString(R.string.s4_5),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-5/home/35",
								R.drawable.s4_e5, false, true));
						add(new Episode(
								res.getString(R.string.s4_6),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-6/home/36",
								R.drawable.s4_e6, false, true));
						add(new Episode(
								res.getString(R.string.s4_7),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-7/home/37",
								R.drawable.s4_e7, false, true));
						add(new Episode(
								res.getString(R.string.s4_8),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-8/home/38",
								R.drawable.s4_e8, false, true));
						add(new Episode(
								res.getString(R.string.s4_9),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-9/home/39",
								R.drawable.s4_e9, false, true));

						// check for newly aired episodes
						Calendar airTime = Calendar.getInstance(TimeZone
								.getTimeZone("EST"));
						Calendar currentTime = Calendar.getInstance(TimeZone
								.getTimeZone("EST"));
						airTime.set(2014, Calendar.JUNE, 30, 21, 0, 0);
						if (currentTime.compareTo(airTime) == 1)
							add(new Episode(
									res.getString(R.string.s4_10),
									"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-10/home/40",
									R.drawable.s4_e10, false, true));
						else
							add(new Episode(
									res.getString(R.string.s4_10),
									"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-10/home/40",
									R.drawable.s4_e10, true, true));
					}
				}, R.drawable.bg_season_4);

		// New season list
		ArrayList<Season> seasonsList = new ArrayList<>();
		seasonsList.addAll(Arrays.asList(seasons));
		return seasonsList;
	}

	private void initControlViews() {
		// misc
		mSplashImage = (ImageView) findViewById(R.id.main_splash);
		mProgressView = (View) findViewById(R.id.progress_bar);
		mProgressBarLayoutParams = mProgressView.getLayoutParams();
		mErrorWebview = (WebView) findViewById(R.id.error_webview);

		// webview
		mWebView = (VideoEnabledWebView) findViewById(R.id.main_webview);
		mWebView.getSettings().setJavaScriptEnabled(true);

		// this is for playing video fullscreen on webview
		// View nonVideoLayout = findViewById(R.id.main_webview);
		ViewGroup videoLayout = (ViewGroup) findViewById(R.id.video_container);
		// View loadingView = findViewById(R.id.video_loading);

		mWebChromeClient = new VideoEnabledWebChromeClient(mWebView,
				videoLayout, null, mWebView) {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);

				if (title.contains("error") || title.contains("404")
						|| title.contains("not found")
						|| title.contains("not available")) {
					mProgressView.setVisibility(View.GONE);
					view.stopLoading();
					if (mErrorWebview.getVisibility() == View.GONE) {
						mErrorWebview.setVisibility(View.VISIBLE);
						mErrorWebview.loadUrl(URL_ERROR);
						Log.e("webview error", "based on its title: " + title);
					}
				}
			}

			public void onProgressChanged(WebView view, int progress) {
				setProgressBarPercent(progress);
			}
		};

		mWebChromeClient
				.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
					@Override
					public void toggledFullscreen(boolean fullscreen) {
						if (fullscreen) {
							mActionBar.hide();
							WindowManager.LayoutParams attrs = getWindow()
									.getAttributes();
							attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
							attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
							getWindow().setAttributes(attrs);
							if (android.os.Build.VERSION.SDK_INT >= 14) {
								getWindow()
										.getDecorView()
										.setSystemUiVisibility(
												View.SYSTEM_UI_FLAG_LOW_PROFILE);
							}
						} else {
							mActionBar.show();
							WindowManager.LayoutParams attrs = getWindow()
									.getAttributes();
							attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
							attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
							getWindow().setAttributes(attrs);
							if (android.os.Build.VERSION.SDK_INT >= 14) {
								getWindow().getDecorView()
										.setSystemUiVisibility(
												View.SYSTEM_UI_FLAG_VISIBLE);
							}
						}
					}
				});

		mWebView.setWebChromeClient(mWebChromeClient);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);

				setProgressBarPercent(0);
				mProgressView.setVisibility(View.VISIBLE);
				if (mErrorWebview.getVisibility() == View.VISIBLE) {
					mErrorWebview.setVisibility(View.GONE);
					mErrorWebview.loadUrl("about:blank");
				}

				mWebviewLoadingFinished = false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				mProgressView.setVisibility(View.GONE);

				if (url.startsWith(URL_HOME)) {
					// Log.d("webview", "trying to remove nav bar");
					view.loadUrl(JS_REMOVE_NAV_BAR); // enable this for a little
														// faster trigger, or
														// disable it for
														// slightly better
														// performance
					view.loadUrl(JS_ADD_URL_CHANGE_LISTENER); // the real deal
				}

				mWebviewLoadingFinished = true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				mProgressView.setVisibility(View.GONE);
				view.stopLoading();
				if (mErrorWebview.getVisibility() == View.GONE) {
					mErrorWebview.setVisibility(View.VISIBLE);
					mErrorWebview.loadUrl(URL_ERROR);
					Log.e("webview error", "url: " + failingUrl + "\nerror: "
							+ description);
				}

				mWebviewLoadingFinished = false;
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith(URL_HOME)) {
					// viewer's guide page, open with our webview
					return false;
				} else {
					// not our page, launch another Activity that handles the
					// URL
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					startActivity(intent);
					return true;
				}
			}
		});
		
		
		// long duration ads
		mWebView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (System.currentTimeMillis() - mAdDisplayTime > LONG_AD_DURATION) {
					showAds();
				}
				return false;
			}
		});
			
		// data
		mSeasons = initSeasonData();
		mRightExpandableListView = (ExpandableListView) findViewById(R.id.right_drawer);

		mRightDrawerAdapter = new RightDrawerAdapter(this, mSeasons);
		mRightExpandableListView.setAdapter(mRightDrawerAdapter);
		mRightExpandableListView.setOnChildClickListener(this);
		mRightExpandableListView.setOnGroupClickListener(this);
		mRightExpandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {

					@Override
					public void onGroupExpand(int groupPosition) {
						if (groupPosition < mRightDrawerAdapter.getGroupCount() - 2) {
							mSeasons.get(groupPosition).setExpanded(true);
							mRightDrawerAdapter.notifyDataSetChanged();
						}
					}
				});
		mRightExpandableListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {

					@Override
					public void onGroupCollapse(int groupPosition) {
						if (groupPosition < mRightDrawerAdapter.getGroupCount() - 2) {
							mSeasons.get(groupPosition).setExpanded(false);
							mRightDrawerAdapter.notifyDataSetChanged();
						}
					}
				});

		mLeftExpandableListView = (ExpandableListView) findViewById(R.id.left_drawer_list);
		mLeftDrawerAdapter = new LeftDrawerAdapter(this);
		mLeftExpandableListView.setAdapter(mLeftDrawerAdapter);
		mLeftExpandableListView.setOnChildClickListener(this);
		mLeftExpandableListView.setOnGroupClickListener(this);
		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {
				// Log.d("view", arg0 + "");
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// Log.d("view", arg0.toString());
			}

			@Override
			public void onDrawerOpened(View arg0) {
				if (arg0.getId() == R.id.left_drawer) {
					if (mLeftDrawerAdapter != null
							&& mIsLastSettingShown != isSettingsReady()) {
						mLeftDrawerAdapter.notifyDataSetChanged();
						mIsLastSettingShown = isSettingsReady();
						// mLeftExpandableListView.requestLayout();
					}

					if (mLeftExpandableListView != null) {
						mLeftExpandableListView.expandGroup(0, false);
						mLeftExpandableListView.expandGroup(1, false);
					}
				} else {
					int currentSelected[] = getActiveEpisode();
					if (currentSelected != null) {
						mRightDrawerAdapter.setCurrentSelected(
								currentSelected[0] - 1, currentSelected[1] - 1);
						mRightDrawerAdapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void onDrawerClosed(View arg0) {
				// Log.d("view", arg0.toString());
			}
		});
	}

	@Override
	public void onBackPressed() {
		// go back fromm fullscreen video, if any // does not seem to work
		/*
		 * if (!mWebChromeClient.onBackPressed() && mWebView.canGoBack()) {
		 * mWebView.goBack(); return; }
		 */

		// go back in webview (current url session)
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return;
		}

		// TODO go back in webview (url history stack)
		// WebBackForwardList history = mWebView.copyBackForwardList();

		// startApp ads comming here
		// startAppAd.onBackPressed();

		// AirPush ad coming here
		if (!mAdDisplayed || System.currentTimeMillis() - mAdDisplayTime > LONG_AD_DURATION) {
			try {
				prm.runCachedAd(this, AdType.smartwall); // This will display the ad
															// but it wont close the
															// app.
			} catch (Exception e) {
				finish();
			}
		}
		
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*
		 * if (mWebView != null) { mWebView.clearCache(true); }
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
			toggleRightDrawer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void toggleRightDrawer() {
		if (mDrawerLayout != null) {
			if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			} else {
				mDrawerLayout.closeDrawers();
			}
		}
	}

	private void toggleLeftDrawer() {
		if (mDrawerLayout != null) {
			if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				mDrawerLayout.openDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.closeDrawers();
			}
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if (parent == mRightExpandableListView) { // right drawer clicked
			if (mRightDrawerAdapter != null) {
				mRightDrawerAdapter.setCurrentSelected(groupPosition,
						childPosition);
				mRightDrawerAdapter.notifyDataSetChanged();
			}
			if (mDrawerLayout != null) {
				mDrawerLayout.closeDrawers();
			}
			mWebView.loadUrl(mSeasons.get(groupPosition).getEpisodes()
					.get(childPosition).getUrl());
			return true;
		} else { // left drawer clicked
			if (id == R.id.left_drawer_group_tab_item_home)
				mWebView.loadUrl(URL_HOME);
			else if (id == R.id.left_drawer_group_tab_item_map)
				mWebView.loadUrl(URL_MAP);
			else if (id == R.id.left_drawer_group_tab_item_houses)
				mWebView.loadUrl(URL_HOUSES);
			else if (id == R.id.left_drawer_group_tab_item_people)
				mWebView.loadUrl(URL_PEOPLE);
			else if (id == R.id.left_drawer_group_tab_item_appendix)
				mWebView.loadUrl(URL_APPENDIX);
			else if (id == R.id.left_drawer_group_settings_item_language) {
				CharSequence items[] = { "English", "Español" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						new ContextThemeWrapper(this,
								android.R.style.Theme_Holo_Dialog));
				builder.setTitle(getResources().getString(R.string.language));
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							mWebView.loadUrl(JS_SET_LANG_EN);
							mIsLanguageEn = true;
						} else {
							mWebView.loadUrl(JS_SET_LANG_ES);
							mIsLanguageEn = false;
						}
						mLeftDrawerAdapter.notifyDataSetChanged();
					}
				});
				builder.create().show();
			} else if (id == R.id.left_drawer_group_settings_item_spoiler) {
				mWebView.loadUrl(JS_SET_SPOILER_ON);
				mIsSpoilerAlertOn = !mIsSpoilerAlertOn;
				mLeftDrawerAdapter.notifyDataSetChanged();
			} else if (id == R.id.left_drawer_group_hbo_item_com)
				loadUrlIntent(URL_HBO_COM);
			else if (id == R.id.left_drawer_group_hbo_item_go)
				loadUrlIntent(URL_HBO_GO);
			else if (id == R.id.left_drawer_group_hbo_item_connect)
				loadUrlIntent(URL_HBO_CONNECT);
			else if (id == R.id.left_drawer_group_hbo_item_store)
				loadUrlIntent(URL_HBO_STORE);
			else if (id == R.id.left_drawer_group_social_item_fb)
				loadUrlIntent(URL_FB);
			else if (id == R.id.left_drawer_group_social_item_tb)
				loadUrlIntent(URL_TB);
			else if (id == R.id.left_drawer_group_social_item_tw)
				loadUrlIntent(URL_TW);
			else if (id == R.id.left_drawer_group_social_item_yt)
				loadUrlIntent(URL_YT);
			else
				loadUrlIntent(URL_IG);
			if (id != R.id.left_drawer_group_settings_item_spoiler) {
				mDrawerLayout.closeDrawers();
			}
			return true;
		}
	}

	private void loadUrlIntent(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		if (parent == mRightExpandableListView) { // right drawer clicked
			if (groupPosition == mRightDrawerAdapter.getGroupCount() - 1) {
				feedbackToDev();
				return true;
			}
			/* else if (groupPosition == mRightDrawerAdapter.getGroupCount() - 1) {
				/*
				 * Dialog donateDialog = new DonateDialog(MainActivity.this,
				 * true, null, MainActivity.this); donateDialog.show(); return
				 * true;
				 */// TODO: no more donation
			// }
			return false;

		} else { // left drawer clicked
			if (id == R.id.left_drawer_group_tab
					|| id == R.id.left_drawer_group_settings) {
				return true; // do not expand/collapse this group
			} else {
				return false;
			}
		}
	}

	private void feedbackToDev() {
		/* redirect user to play store review page */
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
				| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
				| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		try {
			startActivity(goToMarket);
		} catch (Exception e) {
			// if Play Store is not installed, open web link
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ getPackageName())));
		}
	}

	private boolean mIsLastSettingShown = false;

	public boolean isSettingsReady() {
		return mWebView != null && mWebviewLoadingFinished 
				&& mWebView.getUrl() != null && mWebView.getUrl().startsWith(URL_HOME)
				&& mErrorWebview.getVisibility() != View.VISIBLE;
	}

	public boolean isLanguageEn() {
		return mIsLanguageEn;
	}

	public boolean isSpoilerAlertOn() {
		return mIsSpoilerAlertOn;
	}

	protected JSONObject getCookie() {
		String s = CookieManager.getInstance().getCookie(
				"http://viewers-guide.hbo.com");
		if (s != null) {
			int f = s.indexOf("got_episode_data=");
			if (f != -1) {
				try {
					f += 17;
					int l = s.indexOf(";", f);
					String jsonStr = s.substring(f, l);
					return new JSONObject(URLDecoder.decode(jsonStr, "UTF-8"));
				} catch (Exception e) {
					Log.e("getCookie", e.getMessage());
				}
			}
		}
		return null;
	}

	protected int[] getActiveEpisode() {
		// if content is loading, do not update active episode
		if (!isSettingsReady())
			return null;

		// get episode data from coookie
		JSONObject cookie = getCookie();
		if (cookie == null)
			return null;
		int[] ep = new int[2];
		try {
			ep[0] = cookie.getInt("season_number");
			ep[1] = cookie.getInt("episode_number");
		} catch (JSONException e) {
			return null;
		}
		return ep;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (System.currentTimeMillis() - mAdDisplayTime > AD_DURATION) {
			showAds();
		}
		// startAppAd.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// startAppAd.onPause();
	}
	
	private void showAds() {
		try {
			prm.runCachedAd(this, AdType.smartwall);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				prm.runSmartWallAd();
				Log.d("Ads", "Load cache again");
			}
		}, 30000);
	}
	/*
	 * public void setLocate(String lang) { Locale myLocale = new Locale(lang);
	 * Resources res = getResources(); DisplayMetrics dm =
	 * res.getDisplayMetrics(); Configuration conf = res.getConfiguration();
	 * conf.locale = myLocale; res.updateConfiguration(conf, dm); Intent refresh
	 * = new Intent(this, MainActivity.class); startActivity(refresh); }
	 */
}
