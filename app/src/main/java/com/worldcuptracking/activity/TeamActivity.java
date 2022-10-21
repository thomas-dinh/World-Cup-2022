package com.worldcuptracking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.worldcuptracking.R;
import com.worldcuptracking.WorldCupApp;
import com.worldcuptracking.adapter.AdapterListTeams;
import com.worldcuptracking.model.Team;

public class TeamActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterListTeams mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        initToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeamActivity.this));
        recyclerView.setHasFixedSize(true);


        //set data and list adapter
        mAdapter = new AdapterListTeams(TeamActivity.this, WorldCupApp.teams);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListTeams.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Team obj, int position) {
                Intent intent = new Intent(TeamActivity.this, TeamProfileActivity.class);
                intent.putExtra("team", obj);
                startActivity(intent);
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Qualified Teams");
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
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
