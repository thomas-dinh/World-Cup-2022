package com.worldcuptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.worldcuptracking.R;
import com.worldcuptracking.adapter.AdapterListNews;
import com.worldcuptracking.model.Feeds;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewsFeedActivity extends AppCompatActivity {

    private List<Feeds> mFeedDB = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdapterListNews mAdapter;
    String newsfeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        initToolbar();

        AdView mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        //load ads request
        mAdView.loadAd(adRequest);

        HtmlParser htmlThread = new HtmlParser();
        htmlThread.execute();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Latest News");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            mFeedDB.clear();
            HtmlParser htmlThread = new HtmlParser();
            htmlThread.execute();
        }
        return super.onOptionsItemSelected(item);
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

    public class HtmlParser extends AsyncTask<Void, Integer, List<Feeds>> {

        private static final int NETWORK_NO_ERROR = -1;
        private static final int NETWORK_HOST_UNREACHABLE = 1;
        private static final int NETWORK_NO_ACCESS_TO_INTERNET = 2;
        private static final int NETWORK_TIME_OUT = 3;

        Integer serverError = NETWORK_NO_ERROR;

        protected void onPreExecute() {
            if (dialog == null) {
                dialog = new ProgressDialog(NewsFeedActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Loading...");
            }
            dialog.show();
        }

        @Override
        protected List<Feeds> doInBackground(Void... params) {
            try {

                if (Locale.getDefault().getLanguage().equals("ar")) {
                    newsfeed = "https://www.winwin.com/rss";
                    Document doc = Jsoup.connect(newsfeed).get();
                    Elements pElements = doc.select("item");

                    for (Element e : pElements) {
                        String title = e.select("title").html();
                        title = title.replace("<![CDATA[", "");
                        title = title.replace("]]>", "");

                        String desc = e.select("description").html();
                        // desc = desc.split("&quot;")[0];
                        desc = desc.replace("<![CDATA[", "");
                        desc = desc.replace("]]>", "");

                        String link = e.select("link").text();
                        String pubDate = e.select("pubDate").text();

                        String url = e.getElementsByTag("media:content").attr("url");
                        mFeedDB.add(new Feeds(title,desc, link, url, pubDate));
                    }
                    ///https://feeds.alwatanvoice.com/ar/sport.xml
                    // https://www.winwin.com/rss
                    // https://www.masrawy.com/rss/feed/579/
                    //https://www.alarabiya.net/feed/rss2/ar/sport.xml
                } else{
                    newsfeed = "https://e00-marca.uecdn.es/rss/en/world-cup.xml";

                    Document doc = Jsoup.connect(newsfeed).get();
                    for (Element e : doc.select("item")) {
                        String title = e.select("title").html();
                        title = title.replace("<![CDATA[", "");
                        title = title.replace("]]>", "");

                        String desc = e.select("description").text();
                        desc = desc.split("&nbsp;")[0];
                        desc = desc.replace("<p>", "");
                        desc = desc.replace("</p>", "");

                        String link = e.select("link").text();
                        String url = e.getElementsByTag("media:content").attr("url");
                        mFeedDB.add(new Feeds(title, desc, link, url, e.select("pubDate").text()));
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
                return null;
            } catch (Error e) {
                e.printStackTrace();
                return null;
            } // end try catch

            return mFeedDB;

        } // end doInBackground

        protected void onProgressUpdate(Integer... progress) {

        } // end onProgressUpdate

        protected void onPostExecute(List<Feeds> result) {
            if (mIsDestroyed)// Activity not there anymore
                return;
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            } // end if
            if (result != null && !result.isEmpty()) {

                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(NewsFeedActivity.this));
                recyclerView.setHasFixedSize(true);

                //set data and list adapter
                mAdapter = new AdapterListNews(NewsFeedActivity.this, mFeedDB);
                recyclerView.setAdapter(mAdapter);

                // on item list clicked
                mAdapter.setOnItemClickListener((view, obj, position) -> {
                    Intent intent = new Intent(NewsFeedActivity.this, NewsActivity.class);
                    intent.putExtra("feed", obj);// if its int type
                    startActivity(intent);
                });


            } else {
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setVisibility(View.GONE);
                switch (serverError) {
                    case NETWORK_NO_ERROR:
                        Toast.makeText(NewsFeedActivity.this, "Probably, invalid response from server", Toast.LENGTH_LONG).show();
                        break;
                    case NETWORK_NO_ACCESS_TO_INTERNET:
                        // You can customize error message (or behavior) for
                        // different type of error
                    case NETWORK_TIME_OUT:
                    case NETWORK_HOST_UNREACHABLE:
                        Toast.makeText(NewsFeedActivity.this, "Error in Connection", Toast.LENGTH_LONG).show();
                        break;
                }
            } // end if else
        } // end onPostExecute
    }
}