package com.worldcuptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.worldcuptracking.R;
import com.worldcuptracking.model.Feeds;
import com.worldcuptracking.utils.Tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class NewsActivity extends AppCompatActivity {

    Feeds news;
    String content;
    Menu menu;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initToolbar();
        Intent intent = getIntent();

        MobileAds.initialize(this, initializationStatus -> {});

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
                                NewsActivity.this.finish();

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

        TextView channel = findViewById(R.id.channel);
        news = (Feeds) intent.getSerializableExtra("feed");
        if (Locale.getDefault().getLanguage().equals("ar")) {
            channel.setText("المصدر: winwin.com (c)");
        }else {
            channel.setText("Source: marca.com (c)");
        }
        HtmlParser htmlThread = new HtmlParser();
        htmlThread.execute();
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(NewsActivity.this);
        }

    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            HtmlParser htmlThread = new HtmlParser();
            htmlThread.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    // load links in WebView instead of default browser
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //view.loadUrl(url);
            return true;
        }

    }

    ProgressDialog dialog;


    public class HtmlParser extends AsyncTask<Void, Integer, String> {

        private static final int NETWORK_NO_ERROR = -1;
        private static final int NETWORK_HOST_UNREACHABLE = 1;
        private static final int NETWORK_NO_ACCESS_TO_INTERNET = 2;
        private static final int NETWORK_TIME_OUT = 3;

        Integer serverError = NETWORK_NO_ERROR;
        private String datetime;

        protected void onPreExecute() {
            dialog = new ProgressDialog(NewsActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading...");
            dialog.show();
        } // end onPreExecute

        @Override
        protected String doInBackground(Void... params) {
            try {
                // need http protocol
                Document doc = Jsoup.connect(news.link).get();
                if (Locale.getDefault().getLanguage().equals("ar")) {
                    if (doc != null) {
                    Element element = doc.getElementsByClass("clearfix text-formatted field field--name-body field--type-text-with-summary field--label-hidden field__item").first();
                    if (element != null) {
                        element.getElementsByClass("align-center.embedded-entity").remove();
                        element.getElementsByClass("a2a_kit a2a_kit_size_32 addtoany_list").remove();
                        element.getElementsByClass("embedded-entity").remove();

                        // replace body with selected element
                        doc.body().empty().append(element.toString());
                        final String html = doc.toString();

                        return element.html();
                    }
                }
                }else{
                    if (doc != null) {
                        Element element = doc.getElementsByClass("ue-c-article__body").first();
                        if (element != null) {
                            element.getElementsByClass("fi-video-container").remove();
                            // replace body with selected element
                            doc.body().empty().append(element.toString());
                            final String html = doc.toString();

                            return element.html();
                        }
                    }
            }

            } catch (ConnectException e) {
                serverError = NETWORK_NO_ACCESS_TO_INTERNET;
                return null;
            } catch (UnknownHostException e) {
                serverError = NETWORK_HOST_UNREACHABLE;
                return null;
            } catch (SocketTimeoutException e) {
                serverError = NETWORK_TIME_OUT;
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            } // end try catch
            return null;

        } // end doInBackground

        protected void onProgressUpdate(Integer... progress) {

        } // end onProgressUpdate

        protected void onPostExecute(String result) {
            if (result != null) {
                ImageView image = findViewById(R.id.image);
                Tools.displayImageOriginal(NewsActivity.this, image, news.img);
                TextView titleTxt =  findViewById(R.id.title);
                titleTxt.setText(news.title);
                TextView dateTxt = findViewById(R.id.date);
                dateTxt.setText(news.date);

                WebView contentTxt = findViewById(R.id.content);
                contentTxt.getSettings().setJavaScriptEnabled(true);
                contentTxt.setWebViewClient(new MyWebViewClient());


                if (Locale.getDefault().getLanguage().equals("ar")) {
                content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<html><head>" +
                  "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
                  "</head><body style='text-align: justify;direction: rtl;'>";
                }else {
                 content =
                 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<html><head>" +
                  "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
                   "</head><body style='text-align: justify;'>";
                }

                content += result + "</body></html>";
                contentTxt.loadData(content, "text/html; charset=utf-8", "UTF-8");
                menu.findItem(R.id.action_refresh).setVisible(false);
            } else {
                switch (serverError) {
                    case NETWORK_NO_ERROR:
                        Toast.makeText(NewsActivity.this, "Probably, invalid response from server", Toast.LENGTH_LONG).show();
                        break;
                    case NETWORK_NO_ACCESS_TO_INTERNET:
                        // You can customize error message (or behavior) for
                        // different type of error
                    case NETWORK_TIME_OUT:
                    case NETWORK_HOST_UNREACHABLE:
                        Toast.makeText(NewsActivity.this, "Error in Connection", Toast.LENGTH_LONG).show();
                        break;
                }
            } // end if else

            if (dialog.isShowing()) {
                dialog.dismiss();
            } // end if
        } // end onPostExecute
    } // end HtmlParser class
} // end NewsFeeds

