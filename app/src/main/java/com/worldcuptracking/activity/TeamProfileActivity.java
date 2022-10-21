package com.worldcuptracking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.worldcuptracking.R;
import com.worldcuptracking.WorldCupApp;
import com.worldcuptracking.adapter.AdapterListMatches;
import com.worldcuptracking.model.Match;
import com.worldcuptracking.model.Team;
import com.worldcuptracking.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class TeamProfileActivity extends AppCompatActivity {


    Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_team);

        Intent intent = getIntent();
        team = (Team) intent.getSerializableExtra("team");

        ImageView image = (ImageView) findViewById(R.id.image);
        Tools.displayImageOriginal(TeamProfileActivity.this, image, Tools.getDrawable(TeamProfileActivity.this, team.iso2));
        TextView t = (TextView) findViewById(R.id.name);
        t.setText(team.name);

        TextView nickname;
        TextView rank;
        TextView app;
        TextView title;
        nickname = findViewById(R.id.nickname);
        rank = findViewById(R.id.ranking);
        app = findViewById(R.id.apps);
        title = findViewById(R.id.titles);
        nickname.setText(team.nickname);
        rank.setText("" + team.ranking);
        app.setText("" + team.apps);
        title.setText("" + team.titles);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        List<Match> matches = new ArrayList<>();
        for (Match match : WorldCupApp.matches) {
            if (match.header)
                continue;
            if (match.home_team - team.id == 0 || match.away_team - team.id == 0) {
                matches.add(match);
            }
        }

        //set data and list adapter
        AdapterListMatches mMatchAdapter = new AdapterListMatches(this, matches, true);
        recyclerView.setAdapter(mMatchAdapter);

        // on item list clicked
        mMatchAdapter.setOnItemClickListener(new AdapterListMatches.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Match obj, int position) {
                Intent intent = new Intent(TeamProfileActivity.this, MatchDetailActivity.class);
                intent.putExtra("match", obj);
                startActivity(intent);
            }

        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(team.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
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
}
