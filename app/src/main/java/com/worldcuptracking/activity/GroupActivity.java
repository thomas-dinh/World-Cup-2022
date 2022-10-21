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
import com.worldcuptracking.fragment.GroupFragment;
import com.worldcuptracking.model.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private ViewPager view_pager;
    private TabLayout tab_layout;
    private SectionsPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_scroll);

        initToolbar();
        initComponent();

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Groups");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        view_pager = findViewById(R.id.view_pager);
        setupViewPager(view_pager);

        tab_layout = findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);
    }

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        int id = 0;
        for (Group group : WorldCupApp.groups) {
            adapter.addFragment(GroupFragment.newInstance(id++), "Group " + group.name.toUpperCase());
        }
        viewPager.setAdapter(adapter);
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
        } else if (item.getItemId() == R.id.action_refresh) {
            ((GroupFragment) adapter.getItem(view_pager.getCurrentItem())).setAdapter();
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