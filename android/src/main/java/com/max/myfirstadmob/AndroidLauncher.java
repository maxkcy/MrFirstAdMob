package com.max.myfirstadmob;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import static android.content.ContentValues.TAG;


/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
	public AdView adView;
	View gameView;
	public InterstitialAd mInterstitialAd;
	LinearLayout layout;

	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MobileAds.initialize(this, new OnInitializationCompleteListener() {
			@Override
			public void onInitializationComplete(InitializationStatus initializationStatus) {
			}
		});
			loadAd();
			AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
			//initialize(new MyFirstAdMobMain(), configuration);





		//RelativeLayout layout = new RelativeLayout(this);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		gameView = initializeForView(new MyFirstAdMobMain(this), configuration);

		adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
		//adView.setAdUnitId(""); // Put in your secret key here

		AdRequest adRequest4banner = new AdRequest.Builder().build();
		adView.loadAd(adRequest4banner);

			DisplayMetrics displaymetrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = displaymetrics.heightPixels;  int width = displaymetrics.widthPixels;

			ViewGroup.LayoutParams gameParams =
					new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			gameParams.height = height - (adView.getAdSize().getHeight() + 40);
			gameParams.width = width;

			gameView.setLayoutParams(gameParams);
			layout.addView(gameView);

		/*RelativeLayout.LayoutParams gameParams =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
		gameParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		gameParams.addRule(RelativeLayout.CENTER_HORIZONTAL);*/

		ViewGroup.LayoutParams adParams =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			adParams.height = adView.getAdSize().getHeight() + 40;
			adParams.width = width;
			adView.setLayoutParams(adParams);
			layout.addView(adView);


			setContentView(layout);

		}

		public void loadAd(){
		AdRequest adRequest = new AdRequest.Builder().build();
		InterstitialAd.load(AndroidLauncher.this,"ca-app-pub-3940256099942544/1033173712",
				adRequest, new InterstitialAdLoadCallback() {

			@Override
			public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
				// The mInterstitialAd reference will be null until
				// an ad is loaded.
				mInterstitialAd = interstitialAd;
				Log.i(TAG, "onAdLoaded");

				mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
					@Override
					public void onAdDismissedFullScreenContent() {
						// Called when fullscreen content is dismissed.
						Log.d("TAG", "The ad was dismissed.");
						loadAd();
					}

					@Override
					public void onAdFailedToShowFullScreenContent(AdError adError) {
						// Called when fullscreen content failed to show.
						Log.d("TAG", "The ad failed to show.");
					}

					@Override
					public void onAdShowedFullScreenContent() {
						// Called when fullscreen content is shown.
						// Make sure to set your reference to null so you don't
						// show it a second time.
						mInterstitialAd = null;
						Log.d("TAG", "The ad was shown.");
						//Gdx.app.postRunnable(()->loadAd());
					}
				});
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				// Handle the error
				Log.i(TAG, loadAdError.getMessage());
				mInterstitialAd = null;
			}
		});


	}

	protected Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case SHOW_ADS:
				{
					adView.setVisibility(View.VISIBLE);
					break;
				}
				case HIDE_ADS:
				{
					adView.setVisibility(View.GONE);
					break;
				}
			}
		}
	};


	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}

	@Override
	public void showInterstitial() {
		if (mInterstitialAd != null){
			Gdx.app.postRunnable(() -> mInterstitialAd.show(this));
		} else {

			Log.d("TAG", "The interstitial ad wasn't ready yet.");

		}
	}

	ViewGroup.LayoutParams adParams =
			new LinearLayout.LayoutParams(0,0);
	ViewGroup.LayoutParams gameParams =
			new LinearLayout.LayoutParams(0,0);
	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		DisplayMetrics displaymetrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;  int width = displaymetrics.widthPixels;

		adParams.height = adView.getAdSize().getHeight() + 40;
		adParams.width = width;
		adView.setLayoutParams(adParams);

		gameParams.height = height - (adView.getAdSize().getHeight() + 40);
		gameParams.width = width;
		gameView.setLayoutParams(gameParams);
		//setContentView(layout);
	}
}