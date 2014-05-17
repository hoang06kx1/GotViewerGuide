package co.k2lab.gotguide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import co.k2lab.gotguide.model.Episode;
import co.k2lab.gotguide.model.Season;
import co.k2lab.gotguide.utils.Callback;
import co.k2lab.gotguide.utils.CustomDialog;
import co.k2lab.gotguide.utils.Utils;

public class MainActivity extends BaseIabActivity implements OnChildClickListener, OnGroupClickListener{
	// controls
	private WebView mWebView, mErrorWebview;
	private ImageView mSplashImage;

	private ActionBar mActionBar;
	private View mProgressView;
	private LayoutParams mProgressBarLayoutParams;

	private ArrayList<Season> mSeasons;
	private NavigationAdapter mNavigationAdapter;
	private DrawerLayout mDrawerLayout;
	
	// const
	private static final long SPLASH_TIME = 7000;
	
	private static final String URL = "http://viewers-guide.hbo.com/";

	private static final String FAILED_URL = "file:///android_asset/error/error-screen.html";
	private static final String FIRST_TIME_KEY = "first_time";
	private static final String JS_TOGGLE_MENU = "javascript:$('body').toggleClass('side-nav-opened');Chaplin.mediator.publish('nav:closeEpisodeSelector');Chaplin.mediator.publish('app:hidenav');void 0";
	public  static final String JS_REMOVE_NAV_BAR = "javascript:if(typeof removeNavBar!='function'){function removeNavBar(){var e=10;var t=document.querySelector('.global-nav');if(t){if(!t.style.display){t.style.display='none';document.querySelector('.page-container>div:first-child').style.marginTop=0;document.querySelector('.close-icon.sprites-close').style.display='none'}}else if(e--)setTimeout(removeNavBar,1e3)}}removeNavBar();void 0";
	private static final String JS_ADD_URL_CHANGE_LISTENER = "javascript:if(typeof removeNavBar!='function'){var removeNavBar=function(){var e=10;var t=document.querySelector('.global-nav');if(t){if(!t.style.display){t.style.display='none';document.querySelector('.page-container>div:first-child').style.marginTop=0;document.querySelector('.close-icon.sprites-close').style.display='none'}}else if(e--){setTimeout(removeNavBar,1e3)}}}if(typeof removePaddingMap!='function'){var removePaddingMap=function(){var e=10;var t=document.querySelector('.page-container>div:first-child');if(t){if(t.style.top){t.style.top=0;$('#map').height($(window).height())}}else if(e--)setTimeout(removePaddingMap,1e3)}}var lastLocation;if(typeof checkUrl!='function'){var checkUrl=function(){if(window.location.href!=lastLocation){lastLocation=window.location.href;removeNavBar();if(lastLocation.indexOf('/map')>-1)removePaddingMap()}}}window.setInterval(checkUrl,1e3);void 0";
	// flags
	private static final int _firstTimeCount = 5;
	private boolean _triggerHint = false;
	private long _startTime = 0;
	private ExpandableListView mExpandableListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isNetWorkAvailable()) {
			getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
			setContentView(R.layout.activity_main);
			initActionBar();
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
				if (mWebView.getUrl().contains("/game-of-thrones/")) {
					// toggle left drawer
					mWebView.loadUrl(JS_TOGGLE_MENU);
					
					// close right drawer if it is opening
					if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
						mDrawerLayout.closeDrawers();
					}
				}
			}
		});
		
		// set custom home area into view
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		mActionBar.setCustomView(actionBarHomeArea);
	}

	private boolean isNetWorkAvailable() {
		boolean canLoadUrl = Utils.isNetworkEnabled(getApplicationContext());
		if (!canLoadUrl) {
			final CustomDialog errorDialog = new CustomDialog(this,
					getResources().getString(R.string.network_require),
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
		final CustomDialog hintDialog = new CustomDialog(this, getResources()
				.getString(R.string.hint), R.drawable.hint_pop_up_bg, null);
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
		Season[] seasons = new Season[4];
		
		// season 1
		seasons[0] = new Season("Season 1",
				new ArrayList<Episode>() {{
					add(new Episode("1. Winter Is Coming", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-1/home/21", R.drawable.s1_e1, false, true));
					add(new Episode("2. The Kingsroad", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-2/home/22", R.drawable.s1_e2, false, true));
					add(new Episode("3. Lord Snow", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-3/home/23", R.drawable.s1_e3, false, true));
					add(new Episode("4. Cripples, Bastards, and Broken Things", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-4/home/24", R.drawable.s1_e4, false, true));
					add(new Episode("5. The Wolf and the Lion", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-5/home/25", R.drawable.s1_e5, false, true));
					add(new Episode("6. A Golden Crown", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-6/home/26", R.drawable.s1_e6, false, true));
					add(new Episode("7. You Win or You Die", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-7/home/27", R.drawable.s1_e7, false, true));
					add(new Episode("8. The Pointy End", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-8/home/28", R.drawable.s1_e8, false, true));
					add(new Episode("9. Baelor", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-9/home/29", R.drawable.s1_e9, false, true));
					add(new Episode("10. Fire and Blood", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-10/home/30", R.drawable.s1_e10, false, true));
				}},	R.drawable.bg_season_1);
		
		// season 2
		seasons[1] = new Season("Season 2",
				new ArrayList<Episode>() {{
					add(new Episode("1. The North Remembers", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-1/home/11", R.drawable.s2_e1, false, true));
					add(new Episode("2. The Night Lands", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-2/home/12", R.drawable.s2_e2, false, true));
					add(new Episode("3. What Is Dead May Never Die", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-3/home/13", R.drawable.s2_e3, false, true));
					add(new Episode("4. Garden of Bones", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-4/home/14", R.drawable.s2_e4, false, true));
					add(new Episode("5. The Ghost of Harrenhal", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-5/home/15", R.drawable.s2_e5, false, true));
					add(new Episode("6. The Old Gods and the New", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-6/home/16", R.drawable.s2_e6, false, true));
					add(new Episode("7. A Man Without Honor", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-7/home/17", R.drawable.s2_e7, false, true));
					add(new Episode("8. The Prince of Winterfell", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-8/home/18", R.drawable.s2_e8, false, true));
					add(new Episode("9. Blackwater", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-9/home/19", R.drawable.s2_e9, false, true));
					add(new Episode("10. Valar Morghulis", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-10/home/20", R.drawable.s2_e10, false, true));
				}},	R.drawable.bg_season_2);
		
		// season 3
		seasons[2] = new Season("Season 3",
				new ArrayList<Episode>() {{
					add(new Episode("1. Valar Dohaeris", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-1/home/1", R.drawable.s3_e1, false, true));
					add(new Episode("2. Dark Wings, Dark Words", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-2/home/2", R.drawable.s3_e2, false, true));
					add(new Episode("3. Walk of Punishment", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-3/home/3", R.drawable.s3_e3, false, true));
					add(new Episode("4. And Now His Watch Is Ended", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-4/home/4", R.drawable.s3_e4, false, true));
					add(new Episode("5. Kissed by Fire", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-5/home/5", R.drawable.s3_e5, false, true));
					add(new Episode("6. The Climb", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-6/home/6", R.drawable.s3_e6, false, true));
					add(new Episode("7. The Bear and the Maiden Fair", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-7/home/7", R.drawable.s3_e7, false, true));
					add(new Episode("8. Second Sons", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-8/home/8", R.drawable.s3_e8, false, true));
					add(new Episode("9. The Rains of Castamere", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-9/home/9", R.drawable.s3_e9, false, true));
					add(new Episode("10. Mhysa", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-10/home/10", R.drawable.s3_e10, false, true));
				}}, R.drawable.bg_season_3);
		
		// season 4
		seasons[3] = new Season("Season 4",
				new ArrayList<Episode>() {{
					add(new Episode("1. Two Swords", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-1/home/31", R.drawable.s4_e1, false, true));
					add(new Episode("2. The Lion and the Rose", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-2/home/32", R.drawable.s4_e2, false, true));
					add(new Episode("3. Breaker of Chains", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-3/home/33", R.drawable.s4_e3, false, true));
					add(new Episode("4. Oathkeeper", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-4/home/34", R.drawable.s4_e4, false, true));
					add(new Episode("5. First of His Name", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-5/home/35", R.drawable.s4_e5, false, true));
					add(new Episode("6. The Laws of Gods and Men", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-6/home/36", R.drawable.s4_e6, false, true));
					
					// check for newly aired episodes
					Calendar airTime = Calendar.getInstance(TimeZone.getTimeZone("EST"));
					Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("EST"));
					airTime.set(2014, Calendar.JUNE, 15, 21, 0, 0);
					
					if (currentTime.compareTo(airTime) == 1) {
						add(new Episode("7. Mockingbird", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-7/home/37", R.drawable.s4_e7, false, true));
						add(new Episode("8. The Mountain and the Viper", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-8/home/38", R.drawable.s4_e8, false, true));
						add(new Episode("9. The Watchers on the Wall", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-9/home/39", R.drawable.s4_e9, false, true));
						airTime.set(2014, Calendar.JUNE, 30);
						if (currentTime.compareTo(airTime) == 1)
							add(new Episode("10. The Children", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-10/home/40", R.drawable.s4_e10, false, true));
						else
							add(new Episode("10. The Children", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-10/home/40", R.drawable.s4_e10, true, true));
					} else {
						airTime.set(2014, Calendar.JUNE, 8);
						if (currentTime.compareTo(airTime) == 1) {
							add(new Episode("7. Mockingbird", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-7/home/37", R.drawable.s4_e7, false, true));
							add(new Episode("8. The Mountain and the Viper", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-8/home/38", R.drawable.s4_e8, false, true));
							add(new Episode("9. The Watchers on the Wall", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-9/home/39", R.drawable.s4_e9, true, true));
						} else {
							airTime.set(2014, Calendar.JUNE, 1);
							if (currentTime.compareTo(airTime) == 1) {
								add(new Episode("7. Mockingbird", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-7/home/37", R.drawable.s4_e7, false, true));
								add(new Episode("8. The Mountain and the Viper", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-8/home/38", R.drawable.s4_e8, true, true));
							} else {
								airTime.set(2014, Calendar.MAY, 18);
								if (currentTime.compareTo(airTime) == 1)
									add(new Episode("7. Mockingbird", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-7/home/37", R.drawable.s4_e7, true, true));
							}
						}
					}
				}},	R.drawable.bg_season_4);
	
		// New season list
		ArrayList<Season> seasonsList = new ArrayList<>();
		seasonsList.addAll(Arrays.asList(seasons));
		return seasonsList;
	}
	
	private void initControlViews() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mSplashImage = (ImageView) findViewById(R.id.main_splash);
		mProgressView = (View) findViewById(R.id.progress_bar);
		mProgressBarLayoutParams = mProgressView.getLayoutParams();
		mErrorWebview = (WebView) findViewById(R.id.error_webview);
		
		mWebView = (WebView) findViewById(R.id.main_webview);
		
		// nonsense flags for a better performance I hope
		//mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		//mWebView.getSettings().setPluginState(android.webkit.WebSettings.PluginState.ON_DEMAND);
		
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				
				Log.d("webview started url", url);
				
				setProgressBarPercent(0);
				mProgressView.setVisibility(View.VISIBLE);
				if (mErrorWebview.getVisibility() == View.VISIBLE) {
					mErrorWebview.setVisibility(View.INVISIBLE);
					mErrorWebview.loadUrl("about:blank");
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mProgressView.setVisibility(View.INVISIBLE);
				
				Log.d("webview finished url", url);
				
				if (url.startsWith(URL)) {
					Log.d("webview", "trying to remove nav bar");
					//view.loadUrl(JS_REMOVE_NAV_BAR);  // enable this for a little faster trigger, but we choose
														// to disable it for slightly better performance
					view.loadUrl(JS_ADD_URL_CHANGE_LISTENER); // the real deal
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
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				Log.d("webview title", title);
				if (title.contains("error") || 
						title.contains("404") || 
						title.contains("not found") || 
						title.contains("not available")) {
					mProgressView.setVisibility(View.INVISIBLE);
					view.stopLoading();
					if (mErrorWebview.getVisibility() == View.INVISIBLE) {
						mErrorWebview.setVisibility(View.VISIBLE);
						mErrorWebview.loadUrl(FAILED_URL);
					}
				}
			}
			
			public void onProgressChanged(WebView view, int progress) {
				setProgressBarPercent(progress);
			}
		});
		
		mSeasons = initSeasonData();
		mExpandableListView = (ExpandableListView)findViewById(R.id.right_drawer);
		mNavigationAdapter = new NavigationAdapter(this, mSeasons);		
		mExpandableListView.setAdapter(mNavigationAdapter);
		mExpandableListView.setOnChildClickListener(this);
		mExpandableListView.setOnGroupClickListener(this);
		mExpandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {		
				if (groupPosition < mNavigationAdapter.getGroupCount() - 2) {
					mSeasons.get(groupPosition).setExpanded(true);
					mNavigationAdapter.notifyDataSetChanged();
				}
			}
		});
		mExpandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				if (groupPosition < mNavigationAdapter.getGroupCount() - 2) {
					mSeasons.get(groupPosition).setExpanded(false);
					mNavigationAdapter.notifyDataSetChanged();
				}
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
	        	toggleRightDrawer();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	private void toggleRightDrawer() {
		if (mDrawerLayout != null) {
			if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			} else {
				mDrawerLayout.closeDrawers();
			}
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if (mNavigationAdapter != null) {
			mNavigationAdapter.setCurrentSelected(groupPosition, childPosition);
			mNavigationAdapter.notifyDataSetChanged();			
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawers();
		}
		mWebView.loadUrl(mSeasons.get(groupPosition).getEpisodes().get(childPosition).getUrl());
		return true;
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		if (groupPosition == mNavigationAdapter.getGroupCount() - 1)	{
			Log.d("drawer", "group item clicked");
			return true;
		} else if (groupPosition == mNavigationAdapter.getGroupCount() - 2) {
			purchaseAnProduct(BaseIabActivity.SKU_ONE_DOLLAR);
			return true;
		}
		return false;
	}
}
