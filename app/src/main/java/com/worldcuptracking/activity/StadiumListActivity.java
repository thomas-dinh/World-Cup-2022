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
import com.worldcuptracking.adapter.AdapterListStadiums;
import com.worldcuptracking.model.StadiumInfo;

import java.util.ArrayList;
import java.util.List;

public class StadiumListActivity extends AppCompatActivity {

    private List<StadiumInfo> mFeedDB = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdapterListStadiums mAdapter;
    private AdView AdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        initToolbar();

        AdView mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        //load ads request
        mAdView.loadAd(adRequest);


        mFeedDB.add(new StadiumInfo(R.string.lusail_stadium, R.drawable.lusail, "file:///android_asset/1.html", R.string.lusail_location, R.string.lusail_capacity,R.string.lusail_opening));
        mFeedDB.add(new StadiumInfo(R.string.al_bayt_stadium, R.drawable.albayt, "file:///android_asset/2.html", R.string.al_bayt_location,R.string.al_bayt_capacity,R.string.al_bayt_opening));
        mFeedDB.add(new StadiumInfo(R.string.aljanoub_stadium, R.drawable.alwakrah, "file:///android_asset/3.html", R.string.aljanoub_location,R.string.aljanoub_capacity,R.string.aljanoub_opening));
        mFeedDB.add(new StadiumInfo(R.string.ahmadbinali_stadium, R.drawable.ahmadbinali, "file:///android_asset/4.html", R.string.ahmadbinali_location, R.string.ahmadbinali_capacity,R.string.ahmadbinali_opening));
        mFeedDB.add(new StadiumInfo(R.string.khalifa_stadium, R.drawable.khalifa_international, "file:///android_asset/5.html", R.string.khalifa_location,R.string.khalifa_capacity,R.string.khalifa_opening));
        mFeedDB.add(new StadiumInfo(R.string.education_stadium, R.drawable.education_city, "file:///android_asset/6.html", R.string.education_location,R.string.education_capacity,R.string.education_opening));
        mFeedDB.add(new StadiumInfo(R.string.stadium974_stadium, R.drawable.stadium_974, "file:///android_asset/7.html", R.string.stadium974_location,R.string.stadium974_capacity,R.string.stadium974_opening));
        mFeedDB.add(new StadiumInfo(R.string.al_thumama_stadium, R.drawable.al_thumama_stadium, "file:///android_asset/8.html", R.string.al_thumama_location,R.string.al_thumama_capacity,R.string.al_thumama_opening));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(StadiumListActivity.this));
        recyclerView.setHasFixedSize(true);


        //set data and list adapter
        mAdapter = new AdapterListStadiums(StadiumListActivity.this, mFeedDB);
        recyclerView.setAdapter(mAdapter);


        // on item list clicked
        mAdapter.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(StadiumListActivity.this, StadiumDetailsActivity.class);
            intent.putExtra("stadium", obj);// if its string type
            startActivity(intent);
        });

        //HtmlParser htmlThread = new HtmlParser();
        //htmlThread.execute();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Stadiums");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    ProgressDialog dialog;

    public class HtmlParser extends AsyncTask<Void, Integer, List<StadiumInfo>> {

        private static final int NETWORK_NO_ERROR = -1;
        private static final int NETWORK_HOST_UNREACHABLE = 1;
        private static final int NETWORK_NO_ACCESS_TO_INTERNET = 2;
        private static final int NETWORK_TIME_OUT = 3;

        Integer serverError = NETWORK_NO_ERROR;


        protected void onPreExecute() {
            // example of setting up something
            dialog = new ProgressDialog(StadiumListActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading...");
            dialog.show();
        } // end onPreExecute

        @Override
        protected List<StadiumInfo> doInBackground(Void... params) {
            return mFeedDB;

        } // end doInBackground

        protected void onProgressUpdate(Integer... progress) {

        } // end onProgressUpdate

        protected void onPostExecute(List<StadiumInfo> result) {
            if (result != null) {

                //ListView listview = (ListView) findViewById(R.id.list_view_news_feeds);
                //listview.setAdapter(new ArrayAdapter<Feeds>(MainActivity.this, android.R.layout.simple_list_item_1, mFeedDB));

                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(StadiumListActivity.this));
                recyclerView.setHasFixedSize(true);


                //set data and list adapter
                mAdapter = new AdapterListStadiums(StadiumListActivity.this, mFeedDB);
                recyclerView.setAdapter(mAdapter);

                // on item list clicked
                mAdapter.setOnItemClickListener((view, obj, position) -> {
                    Intent intent = new Intent(StadiumListActivity.this, StadiumDetailsActivity.class);
                    intent.putExtra("stadium", obj);// if its string type
                    startActivity(intent);
                });


            } else {
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setVisibility(View.GONE);
                switch (serverError) {
                    case NETWORK_NO_ERROR:
                        Toast.makeText(StadiumListActivity.this, "Probably, invalid response from server", Toast.LENGTH_LONG).show();
                        break;
                    case NETWORK_NO_ACCESS_TO_INTERNET:
                        // You can customize error message (or behavior) for
                        // different type of error
                    case NETWORK_TIME_OUT:
                    case NETWORK_HOST_UNREACHABLE:
                        Toast.makeText(StadiumListActivity.this, "Error in Connection", Toast.LENGTH_LONG).show();
                        break;
                }

            } // end if else
            if (dialog.isShowing()) {
                dialog.dismiss();
            } // end if
        } // end onPostExecute
    }
}
