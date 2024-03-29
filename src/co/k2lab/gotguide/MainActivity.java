package co.k2lab.gotguide;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.TextView;
import co.k2lab.gotguide.controls.CustomDialog;
import co.k2lab.gotguide.controls.VideoEnabledWebChromeClient;
import co.k2lab.gotguide.controls.VideoEnabledWebView;
import co.k2lab.gotguide.model.Episode;
import co.k2lab.gotguide.model.Season;
import co.k2lab.gotguide.utils.Callback;
import co.k2lab.gotguide.utils.Utils;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.banner.Banner;

public class MainActivity extends Activity implements OnChildClickListener,
		OnGroupClickListener {
	// controls
	private VideoEnabledWebView mWebView;
	private VideoEnabledWebChromeClient mWebChromeClient;
	private WebView mErrorWebview;
	private View mSplashView;
	private ActionBar mActionBar;
	private View mProgressView;
	private LayoutParams mProgressBarLayoutParams;
	private RightDrawerAdapter mRightDrawerAdapter;
	private LeftDrawerAdapter mLeftDrawerAdapter;
	private DrawerLayout mDrawerLayout;
	private ExpandableListView mRightExpandableListView;
	private ExpandableListView mLeftExpandableListView;
	private CustomDialog mReviewDialog;

	// Const
	private static final int SPLASH_DURATION = 7000;
	private static final long BANNER_AD_DURATION = 60000; // 1 minute
	private static final long INTER_AD_DURATION = 900000; // 10 minutes
	private static final long INTER_AD_ON_RESUME_DURATION = 300000; // 5 minutes
	private static final long REVIEW_INTERVAL = 864000000; // 10 days
	// private static final long BANNER_AD_START_DELAY = 30000; // 30 seconds 
	
	// Only for testing
	// private static final int SPLASH_DURATION = 7000;
	// private static final long BANNER_AD_DURATION = 5000; // 10 minutes 
	// private static final long INTER_AD_DURATION = 1800000; // 30 minutes
	// private static final long REVIEW_INTERVAL = 864000000; // 10 days
	//private static final long INTER_AD_ON_RESUME_DURATION = 10000; // 10 minutes
	
	private static final String FIRST_TIME_KEY = "got.first";
	private static final String REVIEW_NOTIFICATION_TIME = "got.notif";
	private static final String REVIEW_STATUS = "got.review";
	
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

	// data
	private ArrayList<Season> mSeasons;

	// ads
	private long mAdDisplayTime = System.currentTimeMillis();
	private InterstitialAd mAdmobIad;
	private AdRequest mAdmobIadRequest; //
	private AdView mAdmobBannerAd;
	// private Banner mStartAppBannerAd;
	private AdRequest mAdmobBannerAdRequest;
	private StartAppAd mStartAppAd;
	private ViewGroup mAdZone;
	private View mAdCloseButton;
	// private boolean mFirstAdReceived;
	private boolean mAdBannerShowing = true;
	private int mAdBannerFlag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Fix bug https://code.google.com/p/android/issues/detail?id=26658 
		if (!isTaskRoot()) {
			finish();
			return;
		}
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
				else
					mWebView.loadUrl(URL_DEFAULT_EPISODE);
				mIsSpoilerAlertOn = cookieJson.optBoolean("spoilerAlerts");
				mIsLanguageEn = cookieJson.optString("lang", "en").equals("en");
			} else {
				mWebView.loadUrl(URL_DEFAULT_EPISODE);
			}

			// remove splash after xx seconds
			mSplashView.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (mSplashView != null) {
						hideSplash();
						if (mActionBar != null) {
							mActionBar.show();
						}
					}
					mEnableDrawer = true;
				}
			}, SPLASH_DURATION);

			// notify about review
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					notifyReview();
				}
			}, 10000);
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
		// Show ad banner
		if (mAdZone == null) {
			mAdZone = (ViewGroup) findViewById(R.id.ads_zone); 
		}
		mAdZone.setVisibility(View.VISIBLE);
		/*
		mAdZone.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mAdZone.setVisibility(View.VISIBLE);
			}
		}, BANNER_AD_START_DELAY);
		*/
		// ADMOB

		// Create the interstitial.
		mAdmobIad = new InterstitialAd(this);
		mAdmobIad.setAdUnitId("ca-app-pub-7553716895560169/4470836331");

		// Create ad interstitial request.
		mAdmobIadRequest = new AdRequest.Builder()
			.addTestDevice("248798ED195F56341EA0C23B2B76BBFB")
			.addTestDevice("2842078051443556C35681847AD817A9")
			.addTestDevice(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR)
			.build();

		// Begin loading your interstitial.
		mAdmobIad.loadAd(mAdmobIadRequest);

		// Banner
		mAdmobBannerAd = (com.google.android.gms.ads.AdView) findViewById(R.id.admob_view);
		if (mAdmobBannerAd != null) {
			mAdmobBannerAd.setAdListener(new MyAdmobListener());
			mAdmobBannerAdRequest = new com.google.android.gms.ads.AdRequest.Builder()
					// .addTestDevice("248798ED195F56341EA0C23B2B76BBFB")
					// .addTestDevice("2842078051443556C35681847AD817A9")
					.addTestDevice(
							com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR)
					.build();
			mAdmobBannerAd.loadAd(mAdmobBannerAdRequest);
			
		}

		// startApp
		mStartAppAd = new StartAppAd(this);
		mStartAppAd.loadAd();
		
		// close button
		mAdCloseButton = findViewById(R.id.ad_close_button);
		mAdCloseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mAdZone == null) {
					mAdZone = (ViewGroup) findViewById(R.id.ads_zone);
				}
				mAdZone.setVisibility(View.GONE);
				// switchAdBanners();
				mAdmobBannerAd.loadAd(mAdmobBannerAdRequest);
				// StartAppSDK.init(MainActivity.this, "106324371", "206307211");
				mAdBannerShowing = false;
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mAdZone.setVisibility(View.VISIBLE);
						mAdBannerShowing = true;
					}
				}, BANNER_AD_DURATION);
			}
		});
		
		
		// Show only 1 banner ads
		// mStartAppBannerAd = (Banner) findViewById(R.id.startAppBanner);
		// mAdBannerFlag = randomizer.nextInt(2);
		// if (mAdBannerFlag == 0) {
		mAdmobBannerAd.setVisibility(View.VISIBLE);
		// } else {
		// mStartAppBannerAd.hideBanner();
		// }
		// appFlood
		// AppFlood.initialize(this, "B6hLvqjSghRCwNUP", "RqLXGvnb47e6L53b963ab", AppFlood.AD_FULLSCREEN);				
	}

	/*
	private void switchAdBanners() {		
		if (mAdmobBannerAd != null && mStartAppBannerAd != null) {
			mAdBannerFlag = Math.abs(mAdBannerFlag - 1);
			if (mAdBannerFlag == 0) {
				mAdmobBannerAd.setVisibility(View.INVISIBLE);
				mStartAppBannerAd.showBanner();
			} else {
				mAdmobBannerAd.setVisibility(View.VISIBLE);
				mStartAppBannerAd.hideBanner();
			}
		}
	}
	*/
	
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
		
		ImageView logo = (ImageView) mSplashView.findViewById(R.id.splash_logo);
		TextView t1 = (TextView) mSplashView.findViewById(R.id.splash_text_1);
		TextView t2 = (TextView) mSplashView.findViewById(R.id.splash_text_2);
		TextView t3 = (TextView) mSplashView.findViewById(R.id.splash_text_3);
		Typeface trajanFont = Typeface.createFromAsset(getAssets(), "fonts/trajan.otf");
		t1.setTypeface(trajanFont);
		t2.setTypeface(trajanFont);
		t3.setTypeface(trajanFont);		
		
		if (firstTime < mFirstTimeCount) {
			if (firstTime == 0)
				mShouldTriggerHint = true;
			
			t1.setText(getResources().getString(R.string.splash_text_first_time_1));
			t2.setText(getResources().getString(R.string.splash_text_first_time_2));
			t2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.splash_text_size_normal));
			
			switch (randomizer.nextInt(4) + 1) {
			case 2:
				logo.setImageResource(R.drawable.splash_2);
				break;
			case 3:
				logo.setImageResource(R.drawable.splash_3);
				break;
			case 4:
				logo.setImageResource(R.drawable.splash_4);
				break;
			}
		} else {			
			/*
			 * if (firstTime > mFirstTimeCount + 1) { showAds(); // show ads
			 * after 5 times app start }
			 */
			switch (randomizer.nextInt(4) + 1) {
			case 2:
				logo.setImageResource(R.drawable.splash_2);
				t1.setText(getResources().getString(R.string.splash_text_1_2));
				break;
			case 3:
				logo.setImageResource(R.drawable.splash_3);
				t1.setText(getResources().getString(R.string.splash_text_1_3));
				break;
			case 4:
				logo.setImageResource(R.drawable.splash_4);
				t1.setText(getResources().getString(R.string.splash_text_1_4));
				break;
			}
		}
		preferences.edit().putInt(FIRST_TIME_KEY, ++firstTime).commit();
	}

	private void notifyReview() {
		final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		long time = preferences.getLong(REVIEW_NOTIFICATION_TIME, 0);
		boolean isReview = preferences.getBoolean(REVIEW_STATUS, false);
		if (time == 0) {
			preferences
					.edit()
					.putLong(REVIEW_NOTIFICATION_TIME,
							System.currentTimeMillis()).commit();
			return;
		} else {
			if (!isReview
					&& System.currentTimeMillis() - time > REVIEW_INTERVAL) {
				preferences
						.edit()
						.putLong(REVIEW_NOTIFICATION_TIME,
								System.currentTimeMillis()).commit();
				mReviewDialog = new CustomDialog(
						this,
						getResources().getString(R.string.review_reminder_text),
						R.drawable.hint_pop_up_bg,
						new Callback.AlertCallback() {

							@Override
							public void onPressButton() {
								feedbackToDev();
								preferences.edit()
										.putBoolean(REVIEW_STATUS, true)
										.commit();
								dismissReviewDialog();
							}
						}, true);
				mReviewDialog.show();
			}
		}
	}

	private void dismissReviewDialog() {  // damn eclipse!
		if (mReviewDialog != null) {
			mReviewDialog.dismiss();
		}
	}

	private void hideSplash() {
		// remove splash
		mSplashView.setVisibility(View.GONE);
		mDrawerLayout.removeView(mSplashView);
		mSplashView = null;

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
						add(new Episode(
								res.getString(R.string.s4_10),
								"http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-10/home/40",
								R.drawable.s4_e10, false, true));
					}
				}, R.drawable.bg_season_4);

		// New season list
		ArrayList<Season> seasonsList = new ArrayList<>();
		seasonsList.addAll(Arrays.asList(seasons));
		return seasonsList;
	}

	private void initControlViews() {
		// misc
		mSplashView = (View) findViewById(R.id.splash_view);
		mAdZone = (ViewGroup) findViewById(R.id.ads_zone);
		mAdCloseButton = findViewById(R.id.ad_close_button);
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
				if (System.currentTimeMillis() - mAdDisplayTime > INTER_AD_DURATION) {
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

		// StartApp ads comming here
		// mStartAppAd.onBackPressed();

		// AirPush ad coming here
		/*
		 * if (!mAdDisplayed || System.currentTimeMillis() - mAdDisplayTime >
		 * LONG_AD_DURATION) { try { prm.runCachedAd(this, AdType.smartwall); //
		 * This will display the ad // but it wont close the // app. } catch
		 * (Exception e) { finish(); } }
		 */

		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*
		 * if (mWebView != null) { mWebView.clearCache(true); }
		 */
		// AppFlood.destroy();
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
			/* else if (groupPosition == mRightDrawerAdapter.getGroupCount() - 2) {
				buyNoAdsVersion();
				return true;
			}*/
			// TODO donate = remove ad
			// else if (groupPosition == mRightDrawerAdapter.getGroupCount() - 1) {
			//	Dialog donateDialog = new DonateDialog(MainActivity.this, true, null, MainActivity.this);
			//	donateDialog.show(); return true;
			//}
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
	
	/*
	private void buyNoAdsVersion() {
		// redirect user to play store for buying no ads version 
		Uri uri = Uri.parse("market://details?id=" + getPackageName()+ ".noads");
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
							+ getPackageName() + ".noads")));
		}
	}
	*/
	
	private boolean mIsLastSettingShown = false;

	public boolean isSettingsReady() {
		return mWebView != null && mWebviewLoadingFinished
				&& mWebView.getUrl() != null
				&& mWebView.getUrl().startsWith(URL_HOME)
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
		// TODO remove true
		if (System.currentTimeMillis() - mAdDisplayTime > INTER_AD_ON_RESUME_DURATION) { 
			showAds();
		}
		// startAppAd.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// startAppAd.onPause();
	}
	
	private static Random randomizer = new Random();

	private void showAds() {
		// Playing dime on our income...
		int luckyNumber = randomizer.nextInt(3);
		if ((luckyNumber == 0) && mStartAppAd != null && mStartAppAd.isReady()) {
			mStartAppAd.showAd();		
		} else {
			if (mAdmobIad != null && mAdmobIad.isLoaded()) {
				mAdmobIad.show();
			} else {
				Log.d("Ads Error", "No ads loaded!");
			}
		}
		// Reload all ads
		try {
			mStartAppAd.loadAd();
			mAdmobIad.loadAd(mAdmobIadRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdDisplayTime = System.currentTimeMillis();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// mAdZoneView.invalidate();
	}

	/*
	 * public void setLocate(String lang) { Locale myLocale = new Locale(lang);
	 * Resources res = getResources(); DisplayMetrics dm =
	 * res.getDisplayMetrics(); Configuration conf = res.getConfiguration();
	 * conf.locale = myLocale; res.updateConfiguration(conf, dm); Intent refresh
	 * = new Intent(this, MainActivity.class); startActivity(refresh); }
	 */
	
	public class MyAdmobListener extends AdListener {
		@Override
		public void onAdClosed() {
			Log.d("AdmobBanner", "Ad close");
			super.onAdClosed();
		}
		@Override
		public void onAdLoaded() {
			Log.d("AdmobBanner", "Ad load");
			super.onAdLoaded();
			if (mAdBannerShowing) {
				mAdCloseButton.postDelayed(new Runnable() {
					@Override
					public void run() {
						mAdCloseButton.setVisibility(View.VISIBLE);
					}
				}, 10000);
			}
		}
		
		@Override
		public void onAdOpened() {
			Log.d("AdmobBanner", "Ad opened");
			super.onAdOpened();
		}
		
		@Override
		public void onAdFailedToLoad(int errorCode) {
			super.onAdFailedToLoad(errorCode);
			Log.d("AdmobBanner", "Load fail:" + String.valueOf(errorCode));
			if (mAdCloseButton != null) {
				mAdCloseButton.setVisibility(View.GONE);
			} 
			if (mAdmobBannerAd != null && mAdmobBannerAdRequest!= null) {
				mAdmobBannerAd.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mAdmobBannerAd.loadAd(mAdmobBannerAdRequest);
					}
				}, 3000);
			}
		}
	}
}
