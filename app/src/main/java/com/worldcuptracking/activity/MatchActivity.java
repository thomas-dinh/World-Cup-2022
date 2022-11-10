package com.worldcuptracking.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.worldcuptracking.R;
import com.worldcuptracking.WorldCupApp;
import com.worldcuptracking.fragment.MatchFragment;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    private ViewPager view_pager;
    private TabLayout tab_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_scroll);
        initToolbar();
        initComponent();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Matches");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);
    }

    private void setupViewPager(ViewPager viewPager) {
        MatchActivity.SectionsPagerAdapter adapter = new MatchActivity.SectionsPagerAdapter(getSupportFragmentManager());

        if (!WorldCupApp.matches.isEmpty()) {
            adapter.addFragment(MatchFragment.newInstance("Group"), getResources().getString(R.string.group_stage));
        }
        if (!WorldCupApp.knockouts.isEmpty()) {
            adapter.addFragment(MatchFragment.newInstance("Round of 16"), getResources().getString(R.string.round16));
            adapter.addFragment(MatchFragment.newInstance("Quarter-finals"), getResources().getString(R.string.quarter_final));
            adapter.addFragment(MatchFragment.newInstance("Semi-finals"), getResources().getString(R.string.semi_final));
            adapter.addFragment(MatchFragment.newInstance("Third place play-off"), getResources().getString(R.string.thirdplayoff));
            adapter.addFragment(MatchFragment.newInstance("Final"), getResources().getString(R.string.grandfinal));
        }
        viewPager.setAdapter(adapter);
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

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}