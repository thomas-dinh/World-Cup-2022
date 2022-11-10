package com.worldcuptracking.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.worldcuptracking.R;
import com.worldcuptracking.WorldCupApp;
import com.worldcuptracking.listener.MatchResultListener;
import com.worldcuptracking.model.KOMatch;
import com.worldcuptracking.model.Match;
import com.worldcuptracking.model.Stadium;
import com.worldcuptracking.utils.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MatchDetailActivity extends AppCompatActivity implements MatchResultListener {

    Match match;
    KOMatch KOmatch;
    String name1;
    String name2;
    View parent;
    Date date;
    LinearLayout lyt_progress;

    InterstitialAd mInterstitialAd;
    boolean isActivityIsVisible = true;

    @Override
    protected void onPause() {
        super.onPause();
        isActivityIsVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityIsVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityIsVisible = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

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
                        MatchDetailActivity.this.finish();
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

        initToolbar();

        lyt_progress = findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.GONE);

        Intent intent = getIntent();
        match = (Match) intent.getSerializableExtra("match");
        KOmatch = (KOMatch) intent.getSerializableExtra("KOmatch");
        if (match != null) {
            TextView group = findViewById(R.id.group);
            group.setText(getResources().getString(R.string.group) + " " + match.group);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = formatter.parse(match.date);
                TextView std = findViewById(R.id.round);
                std.setText(getResources().getString(R.string.match) + " " + match.name + ", " + new SimpleDateFormat("EEE, dd MMM yyyy HH:mm").format(date));
                this.date = date;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ImageView image1 = findViewById(R.id.image1);
            Tools.displayImageOriginal(MatchDetailActivity.this, image1, Tools.getDrawable(MatchDetailActivity.this, Tools.getTeam(match.home_team - 1).iso2));
            TextView team1 = findViewById(R.id.name1);
            name1 = WorldCupApp.teams.get(match.home_team - 1).name;
            team1.setText(name1);

            ImageView image2 = findViewById(R.id.image2);
            Tools.displayImageOriginal(MatchDetailActivity.this, image2, Tools.getDrawable(MatchDetailActivity.this, Tools.getTeam(match.away_team - 1).iso2));
            TextView team2 = findViewById(R.id.name2);
            name2 = WorldCupApp.teams.get(match.away_team - 1).name;
            team2.setText(name2);

            if (match.home_result >= 0 && match.away_result >= 0) {
                TextView score = findViewById(R.id.score);
                score.setText(match.home_result + " : " + match.away_result);
            }

            TextView stadium = findViewById(R.id.stadium);
            Stadium std = WorldCupApp.stadiums.get(match.stadium - 1);
            stadium.setText(std.name + ", " + std.city);


            TextView status = findViewById(R.id.status);

            if (match.finished) {
                status.setText("Match Finished");
            } else {
                String timespan = Tools.getTimeSpanToString(Calendar.getInstance().getTime(), date);
                status.setText(timespan);
            }


            //firebase live score listener
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("groups").child(match.group.toLowerCase()).child("matches").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            Match m = issue.getValue(Match.class);
                            if ((m.name - match.name) == 0) {
                                match = m;
                                if (match.home_result >= 0 && match.away_result >= 0) {
                                    TextView score = (TextView) findViewById(R.id.score);
                                    score.setText(match.home_result + " : " + match.away_result);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (KOmatch != null) {
            TextView group = findViewById(R.id.group);
            group.setText("" + KOmatch.type);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = formatter.parse(KOmatch.date);
                TextView std = findViewById(R.id.round);
                std.setText("Match " + KOmatch.name + ", " + new SimpleDateFormat("EEE, dd MMM yyyy HH:mm").format(date));
                this.date = date;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (KOmatch.home_team.getClass().equals(String.class)) {
                ImageView image1 = findViewById(R.id.image1);
                image1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                image1.setImageResource(R.drawable.soccer);
                TextView team1 = findViewById(R.id.name1);
                team1.setText("" + KOmatch.home_team);
            } else {
                if (Integer.valueOf(KOmatch.home_team.toString()) < 32) {
                    ImageView image1 = findViewById(R.id.image1);
                    Tools.displayImageRound(MatchDetailActivity.this, image1, Tools.getDrawable(MatchDetailActivity.this, Tools.getTeam(Integer.valueOf(KOmatch.home_team.toString()) - 1).iso2));
                    TextView team1 = findViewById(R.id.name1);
                    name1 = WorldCupApp.teams.get(Integer.valueOf(KOmatch.home_team.toString()) - 1).name;
                    team1.setText("" + name1);
                } else {
                    ImageView image1 = findViewById(R.id.image1);
                    image1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    image1.setImageResource(R.drawable.soccer);
                    TextView team1 = findViewById(R.id.name1);
                    team1.setText("" + KOmatch.home_team);
                }
            }


            if (KOmatch.away_team.getClass().equals(String.class)) {
                ImageView image2 = findViewById(R.id.image2);
                image2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                image2.setImageResource(R.drawable.soccer);
                TextView team1 = findViewById(R.id.name2);
                team1.setText("" + KOmatch.away_team);

            } else {
                if (Integer.valueOf(KOmatch.away_team.toString()) < 32) {
                    ImageView image2 = findViewById(R.id.image2);
                    Tools.displayImageRound(MatchDetailActivity.this, image2, Tools.getDrawable(MatchDetailActivity.this, Tools.getTeam(Integer.valueOf(KOmatch.away_team.toString()) - 1).iso2));
                    TextView team1 = findViewById(R.id.name2);
                    name2 = WorldCupApp.teams.get(Integer.valueOf(KOmatch.away_team.toString()) - 1).name;
                    team1.setText("" + name2);
                } else {
                    ImageView image2 = findViewById(R.id.image2);
                    image2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    image2.setImageResource(R.drawable.soccer);
                    TextView team1 = findViewById(R.id.name2);
                    team1.setText("" + KOmatch.away_team);
                }
            }

            if (KOmatch.home_result >= 0 && KOmatch.away_result >= 0) {
                TextView score = findViewById(R.id.score);
                score.setText(KOmatch.home_result + " : " + KOmatch.away_result);
            }

            TextView score = findViewById(R.id.penalty);
            score.setVisibility(View.INVISIBLE);
            if (KOmatch.home_penalty >= 0 || KOmatch.away_penalty >= 0) {
                score.setVisibility(View.VISIBLE);
                score.setText("(" + KOmatch.home_penalty + " : " + KOmatch.away_penalty + ")");
            }

            TextView stadium = findViewById(R.id.stadium);
            Stadium std = WorldCupApp.stadiums.get(KOmatch.stadium - 1);
            stadium.setText(std.name + ", " + std.city);

            TextView status = findViewById(R.id.status);

            if (KOmatch.finished) {
                status.setText("Match Finished");
            } else {
                String timespan = Tools.getTimeSpanToString(Calendar.getInstance().getTime(), date);
                status.setText(timespan);
            }
        }

        Button button = findViewById(R.id.button);
        parent = findViewById(R.id.calender);
        button.setOnClickListener(view -> {
            int MyVersion = Build.VERSION.SDK_INT;
            if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (!checkIfAlreadyhavePermission()) {
                    requestForSpecificPermission();
                } else {
                    AddToCalender(parent);
                }
            } else {
                AddToCalender(parent);
            }
        });

        loadNativeAds();
    }

    private void loadNativeAds(){
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.admob_native_id))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // Show the ad.
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        TemplateView template = findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                        template.setVisibility(View.VISIBLE);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAds(new AdRequest.Builder().build(), 3);
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MatchDetailActivity.this);
        }

    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Match Detail");
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
            ((WorldCupApp) getApplication()).reloadDatabase();
            findViewById(R.id.score).setVisibility(View.GONE);
            findViewById(R.id.penalty).setVisibility(View.GONE);
            lyt_progress.setVisibility(View.VISIBLE);
            lyt_progress.setAlpha(1.0f);
            lyt_progress.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            lyt_progress.setVisibility(View.GONE);
                            findViewById(R.id.score).setVisibility(View.VISIBLE);
                            if (KOmatch != null && (KOmatch.home_penalty >= 0 || KOmatch.away_penalty >= 0))
                                findViewById(R.id.penalty).setVisibility(View.VISIBLE);
                        }
                    });

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResultChanged() {
    }

    private void AddToCalender(View parent) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (addReminder(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))) {
            Toast.makeText(getApplicationContext(), "Reminder added successfully!", Toast.LENGTH_SHORT).show();
            parent.setVisibility(View.GONE);
        } else {
        }

    }

    public boolean addReminder(int statrYear, int startMonth, int startDay, int startHour, int startMinut) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(statrYear, startMonth, startDay, startHour, startMinut);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(statrYear, startMonth, startDay, startHour, startMinut);
        endTime.add(Calendar.HOUR_OF_DAY, 2);
        long endMillis = endTime.getTimeInMillis();

        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();

        eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        eventValues.put(CalendarContract.Events.TITLE, name1 + " vs " + name2);
        eventValues.put(CalendarContract.Events.DESCRIPTION, "Qatar 2022 World Cup Match");
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        eventValues.put(CalendarContract.Events.DTSTART, startMillis);
        eventValues.put(CalendarContract.Events.DTEND, endMillis);

        eventValues.put(CalendarContract.Events.HAS_ALARM, 1);

        Uri eventUri = getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        /***************** Event: Reminder(with alert) Adding reminder to event *******************/

        String reminderUriString = "content://com.android.calendar/reminders";

        ContentValues reminderValues = new ContentValues();

        reminderValues.put("event_id", eventID);
        reminderValues.put("minutes", 30);
        reminderValues.put("method", 1);

        Uri reminderUri = getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);

        int added = Integer.parseInt(reminderUri.getLastPathSegment());
        return (added > 0) ? true : false;

//        //this means reminder is added
//        if (added > 0) {
//            Intent view = new Intent(Intent.ACTION_VIEW);
//            Intent data = view.setData(reminderUri);// enter the uri of the event not the reminder
//
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//                view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP
//                        | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
//            } else {
//                view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                        Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                        Intent.FLAG_ACTIVITY_NO_HISTORY |
//                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//            }
//            //view the event in calendar
//            startActivity(view);
//        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AddToCalender(parent);
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}