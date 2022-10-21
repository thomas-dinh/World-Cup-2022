package com.worldcuptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.worldcuptracking.R;
import com.worldcuptracking.model.StadiumInfo;
import com.worldcuptracking.utils.Tools;

import java.util.Locale;

/**
 * Created by hafiz on 4/16/2018.
 */
public class StadiumDetailsActivity extends AppCompatActivity {

    StadiumInfo stadium;
    String content;
    Menu menu;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        TextView channel = findViewById(R.id.channel);


        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.admob_interstitial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        StadiumDetailsActivity.this.finish();

                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                    }
                });

            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }

        });


        mAdView = findViewById(R.id.adView3);
        mAdView.loadAd(adRequest);


        if (Locale.getDefault().getLanguage().equals("ar")) {
             channel.setText("stadiumguide.com");
        }else {

        }
        initToolbar();

        Intent intent = getIntent();
        stadium = (StadiumInfo) intent.getSerializableExtra("stadium");



        ImageView image = findViewById(R.id.image);
        Tools.displayImageOriginal(StadiumDetailsActivity.this, image, stadium.imgUrl);

        TextView titleTxt = findViewById(R.id.title);
        titleTxt.setText(stadium.namee);
        TextView copyTxt = findViewById(R.id.date);
        copyTxt.setText(stadium.cityy + ", Qatar");

        WebView contentTxt = findViewById(R.id.content);
        contentTxt.loadUrl(stadium.link);

        contentTxt.getSettings().setJavaScriptEnabled(true);

        //contentTxt.setWebViewClient(new MyWebViewClient());
        //menu.findItem(R.id.action_refresh).setVisible(false);
       // HtmlParser htmlThread = new HtmlParser();
      //  htmlThread.execute();
    }
    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(StadiumDetailsActivity.this);
        }

    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Tools.setSystemBarColor(this, R.color.grey_900);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            //HtmlParser htmlThread = new HtmlParser();
          //  htmlThread.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    // load links in WebView instead of default browser
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

    ProgressDialog dialog;
    boolean mIsDestroyed;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroyed = true;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }




} // end NewsFeeds

