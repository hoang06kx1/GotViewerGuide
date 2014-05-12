package co.k2lab.gotguide;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import co.k2lab.gotguide.model.Episode;
import co.k2lab.gotguide.model.Season;
import co.k2lab.gotguide.utils.Callback;
import co.k2lab.gotguide.utils.CustomDialog;
import co.k2lab.gotguide.utils.Utils;

public class MainActivity extends Activity {
	private WebView mWebView;
	private ImageView mSplashImage;
	private ArrayList<Season> mSeasons;
	private static final String URL = "http://viewers-guide.hbo.com/";
	// private static final String URL = "http://beta.html5test.com/";
	// private static final String URL = "http://google.com";
	private static final String FAILED_URL = "file:///android_asset/error/error-screen.html";
	private static final String FIRST_TIME_KEY = "first_time";
	private static final int _firstTimeCount = 3;
	private static final long SPLASH_TIME = 7000;
	private View mProgressView;
	private LayoutParams mProgressBarLayoutParams;
	private boolean _triggerHint = false;
	private boolean _urlLoaded = false;
	private boolean _pageNotFound = false;
	private long _startTime = 0;
	private ExpandableListView mExpandableListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isNetWorkAvailable()) {
			setContentView(R.layout.activity_main);		
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
					}
				}
			}, SPLASH_TIME);
		}
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
		// season1
		Episode episode1_1 = new Episode("1. Winter Is Coming", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-1/home/21", R.drawable.s1_e1, false, true);
		Episode episode1_2 = new Episode("2. The Kingsroad", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-2/home/22", R.drawable.s1_e2, false, true);
		Episode episode1_3 = new Episode("3. Lord Snow", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-3/home/23", R.drawable.s1_e3, false, true);
		Episode episode1_4 = new Episode("4. Cripples, Bastards, and Broken Things", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-4/home/24", R.drawable.s1_e4, false, true);
		Episode episode1_5 = new Episode("5. The Wolf and the Lion", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-5/home/25", R.drawable.s1_e5, false, true);
		Episode episode1_6 = new Episode("6. A Golden Crown", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-6/home/26", R.drawable.s1_e6, false, true);
		Episode episode1_7 = new Episode("7. You Win or You Die", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-7/home/27", R.drawable.s1_e7, false, true);
		Episode episode1_8 = new Episode("8. The Pointy End", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-8/home/28", R.drawable.s1_e8, false, true);
		Episode episode1_9 = new Episode("9. Baelor", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-9/home/29", R.drawable.s1_e9, false, true);
		Episode episode1_10 = new Episode("10. Fire and Blood", "http://viewers-guide.hbo.com/game-of-thrones/season-1/episode-10/home/30", R.drawable.s1_e10, false, true);
		
		Episode[] episodes1 = new Episode[] {episode1_1,episode1_2,episode1_3, episode1_4, episode1_5, episode1_6, episode1_7, episode1_8, episode1_9, episode1_10};		
		ArrayList<Episode> episodes1List = new ArrayList<Episode>();
		episodes1List.addAll(Arrays.asList(episodes1));
		seasons[0] = new Season("Season 1", episodes1List, R.drawable.bg_season_1);
		
		// season 2
		Episode[] episodes2 = new Episode[10];		
		episodes2[0] = new Episode("1. The North Remembers", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-1/home/11", R.drawable.s2_e1, false, true);
		episodes2[1] = new Episode("2. The Night Lands", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-2/home/12", R.drawable.s2_e2, false, true);
		episodes2[2] = new Episode("3. What Is Dead May Never Die", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-3/home/13", R.drawable.s2_e3, false, true);
		episodes2[3] = new Episode("4. Garden of Bones", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-4/home/14", R.drawable.s2_e4, false, true);
		episodes2[4] = new Episode("5. The Ghost of Harrenhal", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-5/home/15", R.drawable.s2_e5, false, true);
		episodes2[5] = new Episode("6. The Old Gods and the New", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-6/home/16", R.drawable.s2_e6, false, true);
		episodes2[6] = new Episode("7. A Man Without Honor", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-7/home/17", R.drawable.s2_e7, false, true);
		episodes2[7] = new Episode("8. The Prince of Winterfell", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-8/home/18", R.drawable.s2_e8, false, true);
		episodes2[8] = new Episode("9. Blackwater", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-9/home/19", R.drawable.s2_e9, false, true);
		episodes2[9] = new Episode("10. Valar Morghulis", "http://viewers-guide.hbo.com/game-of-thrones/season-2/episode-10/home/20", R.drawable.s2_e10, false, true);
		ArrayList<Episode> episodes2List = new ArrayList<>();
		episodes2List.addAll(Arrays.asList(episodes2));
		seasons[1] = new Season("Season 2", episodes2List, R.drawable.bg_season_2);
		
		// season 3
		Episode[] episodes3 = new Episode[10];
		episodes3[0] = new Episode("1. Valar Dohaeris", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-1/home/1", R.drawable.s3_e1, false, true);
		episodes3[1] = new Episode("2. Dark Wings, Dark Words", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-2/home/2", R.drawable.s3_e2, false, true);
		episodes3[2] = new Episode("3. Walk of Punishment", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-3/home/3", R.drawable.s3_e3, false, true);
		episodes3[3] = new Episode("4. And Now His Watch Is Ended", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-4/home/4", R.drawable.s3_e4, false, true);
		episodes3[4] = new Episode("5. Kissed by Fire", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-5/home/5", R.drawable.s3_e5, false, true);
		episodes3[5] = new Episode("6. The Climb", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-6/home/6", R.drawable.s3_e6, false, true);
		episodes3[6] = new Episode("7. The Bear and the Maiden Fair", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-7/home/7", R.drawable.s3_e7, false, true);
		episodes3[7] = new Episode("8. Second Sons", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-8/home/8", R.drawable.s3_e8, false, true);
		episodes3[8] = new Episode("9. The Rains of Castamere", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-9/home/9", R.drawable.s3_e9, false, true);
		episodes3[9] = new Episode("10. Mhysa", "http://viewers-guide.hbo.com/game-of-thrones/season-3/episode-10/home/10", R.drawable.s3_e10, false, true);
		ArrayList<Episode> episodes3List = new ArrayList<>();
		episodes3List.addAll(Arrays.asList(episodes3));
		seasons[2] = new Season("Season 3", episodes3List, R.drawable.bg_season_3);				
		
		Episode[] episodes4 = new Episode[10];
		episodes4[0] = new Episode("1. Two Swords", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-1/home/31", R.drawable.s4_e1, false, true);
		episodes4[1] = new Episode("2. The Lion and the Rose", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-2/home/32", R.drawable.s4_e2, false, true);
		episodes4[2] = new Episode("3. Breaker of Chains", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-3/home/33", R.drawable.s4_e3, false, true);
		episodes4[3] = new Episode("4. Oathkeeper", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-4/home/34", R.drawable.s4_e4, false, true);
		episodes4[4] = new Episode("5. First of His Name", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-5/home/35", R.drawable.s4_e5, false, true);
		episodes4[5] = new Episode("6. The Laws of Gods and Men", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-6/home/36", R.drawable.s4_e6, false, true);
		episodes4[6] = new Episode("7. Mockingbird", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-7/home/37", R.drawable.s4_e7, false, true);
		episodes4[7] = new Episode("8. The Mountain and the Viper", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-8/home/38", R.drawable.s4_e8, false, true);
		episodes4[8] = new Episode("9. The Watchers on the Wall", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-9/home/39", R.drawable.s4_e9, false, true);
		episodes4[9] = new Episode("10. The Children", "http://viewers-guide.hbo.com/game-of-thrones/season-4/episode-10/home/40", R.drawable.s4_e10, false, true);
		ArrayList<Episode> episodes4List = new ArrayList<>();
		episodes4List.addAll(Arrays.asList(episodes4));
		seasons[3] = new Season("Season 4", episodes4List, R.drawable.bg_season_4);
		
		// New seasons list
		ArrayList<Season> seasonsList = new ArrayList<>();
		seasonsList.addAll(Arrays.asList(seasons));
		return seasonsList;
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
				 * _urlLoaded = true; long currentTime =
				 * System.currentTimeMillis(); if (mSplashImage != null &&
				 * (currentTime - _startTime > SPLASH_TIME)) { hideSplash(); }
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
		mSeasons = initSeasonData();
		mExpandableListView = (ExpandableListView)findViewById(R.id.right_drawer);
		final NavigationAdapter navigationAdapter = new NavigationAdapter(this, mSeasons);
		mExpandableListView.setAdapter(navigationAdapter);
		mExpandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {				
				mSeasons.get(groupPosition).setExpanded(true);
				navigationAdapter.notifyDataSetChanged();
			}
		});
		mExpandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				mSeasons.get(groupPosition).setExpanded(false);
				navigationAdapter.notifyDataSetChanged();
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
}
