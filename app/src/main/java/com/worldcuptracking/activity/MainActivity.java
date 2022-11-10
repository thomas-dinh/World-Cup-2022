package com.worldcuptracking.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.worldcuptracking.R;
import com.worldcuptracking.WorldCupApp;
import com.worldcuptracking.listener.DatabaseListener;
import com.worldcuptracking.model.Feeds;
import com.worldcuptracking.model.KOMatch;
import com.worldcuptracking.model.KnockOut;
import com.worldcuptracking.model.Match;
import com.worldcuptracking.model.StadiumInfo;
import com.worldcuptracking.utils.Tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatabaseListener {

    private LinearLayout parent;
    private LinearLayout lyt_progress;
    Fragment fragment;
    String newsfeed;
    private InterstitialAd mInterstitialAd;
    private int mItemID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        //load ads request
        mAdView.loadAd(adRequest);

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
                       // NewsActivity.this.finish();
                        mInterstitialAd = null;
                        openScreen(mItemID);
                    }
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        mInterstitialAd = null;
                        openScreen(mItemID);
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
                Log.e("TAG", "Ad load failed");
            }

        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //display counter
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = formatter.parse(getString(R.string.start_date_local));
            countDownDisplay(Calendar.getInstance().getTime(), date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        lyt_progress = findViewById(R.id.lyt_progress);
        parent = findViewById(R.id.lyt_parent);

        if (WorldCupApp.databaseListener == null)
            WorldCupApp.databaseListener = this;
        else {
            onDataLoaded();
        }

        //load random stadium
        getStadiumData();

        //check if news feed is not empty, cache the data
        if (!WorldCupApp.feeds.isEmpty()) {
            final Feeds news1 = WorldCupApp.feeds.get(0);
            final Feeds news2 = WorldCupApp.feeds.get(1);

            //news
            ImageView imgNews1 = findViewById(R.id.image3);
            Tools.displayImageOriginal(MainActivity.this, imgNews1, news1.img);
            TextView txtNews1 = findViewById(R.id.headline1);
            txtNews1.setText(news1.title);
            View v1 = findViewById(R.id.lyt_news1);
            v1.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("feed", news1);
                startActivity(intent);
            });


            ImageView imgNews2 = findViewById(R.id.image4);
            Tools.displayImageOriginal(MainActivity.this, imgNews2, news2.img);
            TextView txtNews2 = findViewById(R.id.headline2);
            txtNews2.setText(news2.title);
            View v2 = findViewById(R.id.lyt_news2);
            v2.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("feed", news2);
                startActivity(intent);
            });
        }


        //"MORE" button initialize
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(view -> {
            mItemID = 100;
            showAdsInter(mItemID);
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewsFeedActivity.class);
            startActivity(intent);
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(view -> {
            mItemID = 200;
            showAdsInter(mItemID);

        });

        //fetch news
        HtmlParser htmlThread = new HtmlParser();
        htmlThread.execute();
    }

    private void countDownDisplay(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        TextView channel = findViewById(R.id.counter);
        channel.setText(String.format("%d days %d hours %d minutes", elapsedDays,
                elapsedHours, elapsedMinutes));

        if (elapsedDays < 0 || elapsedDays < 0 || elapsedMinutes < 0 || elapsedSeconds < 0) {
            LinearLayout parent = findViewById(R.id.countdown);
            parent.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        //update countdown after resume
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = formatter.parse(getString(R.string.start_date_local));
            countDownDisplay(Calendar.getInstance().getTime(), date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!WorldCupApp.matches.isEmpty()) {
            for (Match m : WorldCupApp.matches) {
                if (m.header)
                    continue;
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = formatter.parse(m.date);
                    m.timespan = Tools.getTimeSpan(Calendar.getInstance().getTime(), date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            onDataLoaded();
        }
    }


    private void crossfade() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        parent.setAlpha(0f);
        parent.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        parent.animate().alpha(1f).setDuration(500).setListener(null);

//        // Animate the loading view to 0% opacity. After the animation ends,
//        // set its visibility to GONE as an optimization step (it won't
//        // participate in layout passes, etc.)
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
        lyt_progress.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {lyt_progress.setVisibility(View.GONE);}
                });
    }

    private void getStadiumData() {
        List<StadiumInfo> stadiums = new ArrayList<>();

        stadiums.add(new StadiumInfo(R.string.lusail_stadium, R.drawable.lusail, "file:///android_asset/1.html", R.string.lusail_location, R.string.lusail_capacity,R.string.lusail_opening));
        stadiums.add(new StadiumInfo(R.string.al_bayt_stadium, R.drawable.albayt, "file:///android_asset/2.html", R.string.al_bayt_location,R.string.al_bayt_capacity,R.string.al_bayt_opening));
        stadiums.add(new StadiumInfo(R.string.aljanoub_stadium, R.drawable.alwakrah, "file:///android_asset/3.html", R.string.aljanoub_location,R.string.aljanoub_capacity,R.string.aljanoub_opening));
        stadiums.add(new StadiumInfo(R.string.ahmadbinali_stadium, R.drawable.ahmadbinali, "file:///android_asset/4.html", R.string.ahmadbinali_location, R.string.ahmadbinali_capacity,R.string.ahmadbinali_opening));
        stadiums.add(new StadiumInfo(R.string.khalifa_stadium, R.drawable.khalifa_international, "file:///android_asset/5.html", R.string.khalifa_location,R.string.khalifa_capacity,R.string.khalifa_opening));
        stadiums.add(new StadiumInfo(R.string.education_stadium, R.drawable.education_city, "file:///android_asset/6.html", R.string.education_location,R.string.education_capacity,R.string.education_opening));
        stadiums.add(new StadiumInfo(R.string.stadium974_stadium, R.drawable.stadium_974, "file:///android_asset/7.html", R.string.stadium974_location,R.string.stadium974_capacity,R.string.stadium974_opening));
        stadiums.add(new StadiumInfo(R.string.al_thumama_stadium, R.drawable.al_thumama_stadium, "file:///android_asset/8.html", R.string.al_thumama_location,R.string.al_thumama_capacity,R.string.al_thumama_opening));

        final StadiumInfo stadium = stadiums.get(new Random().nextInt(stadiums.size()));

        //stadium
        ImageView imgStd = findViewById(R.id.image);
        Tools.displayImageOriginal(MainActivity.this, imgStd, stadium.imgUrl);
        TextView txtStd = findViewById(R.id.stadium_name);
        txtStd.setText(stadium.namee);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, StadiumDetailsActivity.class);
            intent.putExtra("stadium", stadium);// if its string type
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            doExitApp();
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            exitTime = System.currentTimeMillis();
            Toast.makeText(this, getResources().getString(R.string.exit_toast), Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Toast.makeText(getApplicationContext(), item.getTitle() + " Selected", Toast.LENGTH_SHORT).show();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_refresh) {

            HtmlParser htmlThread = new HtmlParser();
            htmlThread.execute();

            try {
                String inputDateString = "14/10/2022 15:00:00";
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = formatter.parse(inputDateString);
                countDownDisplay(Calendar.getInstance().getTime(), date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ((WorldCupApp) getApplication()).reloadDatabase();

            if (!WorldCupApp.matches.isEmpty()) {
                for (Match m : WorldCupApp.matches) {
                    if (m.header)
                        continue;
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = formatter.parse(m.date);
                        m.timespan = Tools.getTimeSpan(Calendar.getInstance().getTime(), date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                onDataLoaded();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAdsInter(int id){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }else {
            openScreen(id);
        }
    }

    private void openScreen(int id){
        if (id == R.id.nav_match) {
            Intent intent = new Intent(MainActivity.this, MatchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_group) {
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            Intent intent = new Intent(MainActivity.this, NewsFeedActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_stadium) {
            Intent intent = new Intent(MainActivity.this, StadiumListActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_team) {
            Intent intent = new Intent(MainActivity.this, TeamActivity.class);
            startActivity(intent);
        }else if (id == 100){
            Intent intent = new Intent(MainActivity.this, MatchActivity.class);
            startActivity(intent);
        } else if (id == 200){
            Intent intent = new Intent(MainActivity.this, StadiumListActivity.class);
            startActivity(intent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        mItemID = id;

        if(id == R.id.nav_match || id == R.id.nav_group || id == R.id.nav_news
                || id == R.id.nav_stadium || id == R.id.nav_team){

            showAdsInter(id);
        }
        else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_exit) {
            this.finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (id == R.id.share_app) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String body = getResources().getString(R.string.share_text) + " " + getResources().getString(R.string.app_name) + " : " + "http://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
            String sub = getResources().getString(R.string.share_text);
            myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
            myIntent.putExtra(Intent.EXTRA_TEXT,body);
            startActivity(Intent.createChooser(myIntent, "Share: " + getResources().getString(R.string.app_name)));
        }
        else if (id == R.id.rate_us) {
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }
        else if (id == R.id.privacy_policy) {
            String str2 = getResources().getString(R.string.PrivacyLink);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str2)));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onDataLoaded() {
        if (WorldCupApp.matches.isEmpty() || WorldCupApp.teams.isEmpty())
            return;
        long min = Long.MAX_VALUE;
        int id = 0;
        for (int i = 0; i < WorldCupApp.matches.size(); i++) {
            Match m = WorldCupApp.matches.get(i);
            if (m.header)
                continue;
            if (m.timespan > 0 && m.timespan < min) {
                min = m.timespan;
                id = i;
            }
        }
        final Match match = WorldCupApp.matches.get(id);
        if (match != null && !match.header) {
            findViewById(R.id.no_match).setVisibility(View.GONE);
            findViewById(R.id.lyt_parent).setVisibility(View.VISIBLE);
            ImageView image1 = findViewById(R.id.image1);
            Tools.displayImageRound(MainActivity.this, image1, Tools.getDrawable(MainActivity.this, Tools.getTeam(match.home_team - 1).iso2));
            TextView name1 = findViewById(R.id.name1);
            name1.setText(WorldCupApp.teams.get(match.home_team - 1).name);
            ImageView image2 = findViewById(R.id.image2);
            Tools.displayImageRound(MainActivity.this, image2, Tools.getDrawable(MainActivity.this, Tools.getTeam(match.away_team - 1).iso2));
            TextView name2 = findViewById(R.id.name2);
            name2.setText(WorldCupApp.teams.get(match.away_team - 1).name);

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = formatter.parse(match.date);
                TextView std = findViewById(R.id.stadium);
                std.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(date));

                TextView time = findViewById(R.id.time);
                time.setText(new SimpleDateFormat("HH:mm").format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            View v3 = findViewById(R.id.lyt_parent);
            v3.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, MatchDetailActivity.class);
                intent.putExtra("match", match);// if its string type
                startActivity(intent);
            });
            crossfade();
        } else {
            if (!WorldCupApp.knockouts.isEmpty()) {
                min = Long.MAX_VALUE;
                id = 0;
                KOMatch latestKOMatch = null;
                for (int i = 0; i < WorldCupApp.knockouts.size(); i++) {
                    KnockOut ko = WorldCupApp.knockouts.get(i);
                    for (int j = 0; j < ko.matches.size(); j++) {
                        KOMatch m = ko.matches.get(j);
                        if (m.header)
                            continue;
                        if (m.timespan > 0 && m.timespan < min) {
                            min = m.timespan;
                            latestKOMatch = m;
                        }
                    }
                }
                if (latestKOMatch != null && !latestKOMatch.header) {
                    findViewById(R.id.no_match).setVisibility(View.GONE);
                    findViewById(R.id.lyt_parent).setVisibility(View.VISIBLE);
                    final KOMatch komatch = latestKOMatch;
                    ImageView image1 = findViewById(R.id.image1);
                    image1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    image1.setImageResource(R.drawable.soccer);
                    //Tools.displayImageRound(MatchDetailActivity.this, image1, R.drawable.soccer);
                    TextView team1 = findViewById(R.id.name1);
                    team1.setText("" + latestKOMatch.home_team);

                    ImageView image2 = findViewById(R.id.image2);
                    image2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    image2.setImageResource(R.drawable.soccer);
                    //Tools.displayImageRound(MatchDetailActivity.this, image2, R.drawable.soccer);
                    TextView team2 = findViewById(R.id.name2);
                    team2.setText("" + latestKOMatch.away_team);

                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = formatter.parse(latestKOMatch.date);
                        TextView std = findViewById(R.id.stadium);
                        std.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(date));

                        TextView time = findViewById(R.id.time);
                        time.setText(new SimpleDateFormat("HH:mm").format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    View v3 = findViewById(R.id.lyt_parent);
                    v3.setOnClickListener(view -> {
                        Intent intent = new Intent(MainActivity.this, MatchDetailActivity.class);
                        intent.putExtra("KOmatch", komatch);// if its string type
                        startActivity(intent);
                    });
                    crossfade();
                } else {
                    findViewById(R.id.lyt_parent).setVisibility(View.GONE);
                    TextView std = findViewById(R.id.no_match);
                    std.setVisibility(View.VISIBLE);
                    std.setText("Sorry, no upcoming match.");
                }
            }
        }
    }

    public class HtmlParser extends AsyncTask<Void, Integer, Boolean> {

        private static final int NETWORK_NO_ERROR = -1;
        private static final int NETWORK_HOST_UNREACHABLE = 1;
        private static final int NETWORK_NO_ACCESS_TO_INTERNET = 2;
        private static final int NETWORK_TIME_OUT = 3;

        Integer serverError = NETWORK_NO_ERROR;
        Feeds news1;
        Feeds news2,news3,news4;

        protected void onPreExecute() {
            if (dialog == null) {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMessage("Loading...");
            }
            //dialog.show();
        } // end onPreExecute

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                if (Locale.getDefault().getLanguage().equals("ar")) {
                    newsfeed = "https://www.winwin.com/rss";
                } else{
                    newsfeed = "https://e00-marca.uecdn.es/rss/en/world-cup.xml";
                }

                Document doc = Jsoup.connect(newsfeed).get();
                int i = 0;
                for (Element e : doc.select("item")) {
                    if (i >= 4)
                        break;
                    String title = e.select("title").html();
                    title = title.replace("<![CDATA[", "");
                    title = title.replace("]]>", "");

                    String desc = e.select("description").text();
                    desc = desc.split("&nbsp;")[0];
                    desc = desc.replace("<p>", "");
                    desc = desc.replace("</p>", "");

                    String link = e.select("link").text();
                    String url = e.getElementsByTag("media:content").attr("url");

                    if (i == 0) {
                        news1 = new Feeds(title, desc, link, url, e.select("pubDate").text());
                    } else if (i == 1) {
                        news2 = new Feeds(title, desc, link, url, e.select("pubDate").text());
                    }else if (i == 2) {
                        news3 = new Feeds(title, desc, link, url, e.select("pubDate").text());
                    }else if (i == 3) {
                        news4 = new Feeds(title, desc, link, url, e.select("pubDate").text());
                    }


                    i++;
                }

                ((WorldCupApp) getApplication()).writeNewData(news1, news2,news3,news4);
                return true;
            } catch (ConnectException e) {
                serverError = NETWORK_NO_ACCESS_TO_INTERNET;
                return false;
            } catch (UnknownHostException e) {
                serverError = NETWORK_HOST_UNREACHABLE;
                return false;
            } catch (SocketTimeoutException e) {
                serverError = NETWORK_TIME_OUT;
                return false;
            } catch (SocketException e) {
                serverError = NETWORK_TIME_OUT;
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (Error e) {
                serverError = NETWORK_TIME_OUT;
                return false;
            } // end try catch
        } // end doInBackground

        protected void onProgressUpdate(Integer... progress) {

        } // end onProgressUpdate

        protected void onPostExecute(Boolean result) {
            if (mIsDestroyed)// Activity not there anymore
                return;
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            } // end if
            if (result) {
                //news
                ImageView imgNews1 = findViewById(R.id.image3);
                Tools.displayImageOriginal(MainActivity.this, imgNews1, news1.img);
                TextView txtNews1 = findViewById(R.id.headline1);
                txtNews1.setText(news1.title);
                View v1 = findViewById(R.id.lyt_news1);
                v1.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putExtra("feed", news1);
                    startActivity(intent);
                });


                ImageView imgNews2 = findViewById(R.id.image4);
                Tools.displayImageOriginal(MainActivity.this, imgNews2, news2.img);
                TextView txtNews2 = findViewById(R.id.headline2);
                txtNews2.setText(news2.title);
                View v2 = findViewById(R.id.lyt_news2);
                v2.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putExtra("feed", news2);
                    startActivity(intent);
                });

                ImageView imgNews3 = findViewById(R.id.image5);
                Tools.displayImageOriginal(MainActivity.this, imgNews3, news3.img);
                TextView txtNews3 = findViewById(R.id.headline3);
                txtNews3.setText(news3.title);
                View v3 = findViewById(R.id.lyt_news3);
                v3.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putExtra("feed", news3);
                    startActivity(intent);
                });

                ImageView imgNews4 = findViewById(R.id.image6);
                Tools.displayImageOriginal(MainActivity.this, imgNews4, news4.img);
                TextView txtNews4 = findViewById(R.id.headline4);
                txtNews4.setText(news4.title);
                View v4 = findViewById(R.id.lyt_news4);
                v4.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putExtra("feed", news4);
                    startActivity(intent);
                });


            } else {
                if (!WorldCupApp.feeds.isEmpty()) {
                    final Feeds news1 = WorldCupApp.feeds.get(0);
                    final Feeds news2 = WorldCupApp.feeds.get(1);
                    final Feeds news3 = WorldCupApp.feeds.get(2);
                    final Feeds news4 = WorldCupApp.feeds.get(3);

                    //news
                    ImageView imgNews1 = findViewById(R.id.image3);
                    Tools.displayImageOriginal(MainActivity.this, imgNews1, news1.img);
                    TextView txtNews1 = findViewById(R.id.headline1);
                    txtNews1.setText(news1.title);
                    View v1 = findViewById(R.id.lyt_news1);
                    v1.setOnClickListener(view -> {
                        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                        intent.putExtra("feed", news1);
                        startActivity(intent);
                    });


                    ImageView imgNews2 = findViewById(R.id.image4);
                    Tools.displayImageOriginal(MainActivity.this, imgNews2, news2.img);
                    TextView txtNews2 = findViewById(R.id.headline2);
                    txtNews2.setText(news2.title);
                    View v2 = findViewById(R.id.lyt_news2);
                    v2.setOnClickListener(view -> {
                        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                        intent.putExtra("feed", news2);
                        startActivity(intent);
                    });



                    ImageView imgNews3 = findViewById(R.id.image5);
                    Tools.displayImageOriginal(MainActivity.this, imgNews3, news3.img);
                    TextView txtNews3 = findViewById(R.id.headline3);
                    txtNews3.setText(news3.title);
                    View v3 = findViewById(R.id.lyt_news3);
                    v3.setOnClickListener(view -> {
                        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                        intent.putExtra("feed", news3);
                        startActivity(intent);
                    });


                    ImageView imgNews4 = findViewById(R.id.image6);
                    Tools.displayImageOriginal(MainActivity.this, imgNews4, news4.img);
                    TextView txtNews4 = findViewById(R.id.headline4);
                    txtNews4.setText(news4.title);
                    View v4 = findViewById(R.id.lyt_news4);
                    v4.setOnClickListener(view -> {
                        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                        intent.putExtra("feed", news4);
                        startActivity(intent);
                    });



                }
                switch (serverError) {
                    case NETWORK_NO_ERROR:
                        Toast.makeText(MainActivity.this, "Probably, invalid response from server", Toast.LENGTH_LONG).show();
                        break;
                    case NETWORK_NO_ACCESS_TO_INTERNET:
                        Toast.makeText(MainActivity.this, "You are offline", Toast.LENGTH_LONG).show();
                        break;
                    case NETWORK_TIME_OUT:
                    case NETWORK_HOST_UNREACHABLE:
                        Toast.makeText(MainActivity.this, "Connection time out", Toast.LENGTH_LONG).show();
                        break;
                }
            } // end if else

        } // end onPostExecute
    } // end HtmlParser class
} // end NewsFeeds
