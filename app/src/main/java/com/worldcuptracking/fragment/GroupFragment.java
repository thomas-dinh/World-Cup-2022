package com.worldcuptracking.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.worldcuptracking.R;
import com.worldcuptracking.WorldCupApp;
import com.worldcuptracking.activity.MatchDetailActivity;
import com.worldcuptracking.adapter.AdapterListGroups;
import com.worldcuptracking.adapter.AdapterListMatches;
import com.worldcuptracking.listener.LiveDataListener;
import com.worldcuptracking.model.Match;
import com.worldcuptracking.model.Team;

public class GroupFragment extends Fragment implements LiveDataListener {

    AdapterListGroups mAdapter;
    AdapterListMatches mMatchAdapter;
    RecyclerView recyclerView;
    private LinearLayout lyt_progress;
    public int groupId;

    public GroupFragment() {
    }

    public static GroupFragment newInstance(int id) {
        GroupFragment fragment = new GroupFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("groupId", id);
        fragment.setArguments(args);
        WorldCupApp.listeners.add(fragment);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_group, container, false);

        lyt_progress = root.findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.GONE);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        Bundle args = getArguments();
        groupId = args.getInt("groupId", 0);


        //set data and list adapter
        mAdapter = new AdapterListGroups(this.getContext(), WorldCupApp.groups.get(groupId).getTeams());
        recyclerView.setAdapter(mAdapter);
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListGroups.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Team obj, int position) {

            }

        });

//        if (groupId != 0) {
//            recyclerView.setVisibility(View.INVISIBLE);
//        }

        RecyclerView recyclerView1 = (RecyclerView) root.findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView1.setHasFixedSize(true);

        //set data and list adapter
        mMatchAdapter = new AdapterListMatches(this.getContext(), WorldCupApp.groups.get(groupId).matches, true);
        recyclerView1.setAdapter(mMatchAdapter);

        // on item list clicked
        mMatchAdapter.setOnItemClickListener(new AdapterListMatches.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Match obj, int position) {
                Intent intent = new Intent(GroupFragment.this.getContext(), MatchDetailActivity.class);
                intent.putExtra("match", obj);
                startActivity(intent);
            }

        });

        return root;
    }

    public void setAdapter() {
        recyclerView.setVisibility(View.GONE);
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
        lyt_progress.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        lyt_progress.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        //set data and list adapter
                        mAdapter = new AdapterListGroups(getContext(), WorldCupApp.groups.get(groupId).getTeams());
                        recyclerView.setAdapter(mAdapter);
                        // on item list clicked
                        mAdapter.setOnItemClickListener(new AdapterListGroups.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, Team obj, int position) {

                            }

                        });
                    }
                });
    }

    public void show() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        if (recyclerView != null)
            recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void updateUI() {
        //System.out.println("notify changed");
        if (mAdapter != null) {
            setAdapter();
//            recyclerView.setVisibility(View.GONE);
//            lyt_progress.setVisibility(View.VISIBLE);
//            mAdapter.setList(WorldCupApp.groups.get(groupId).getTeams());
//            mAdapter.notifyDataSetChanged();
//            lyt_progress.setAlpha(1.0f);
//            lyt_progress.animate()
//                    .alpha(0f)
//                    .setDuration(getContext().getResources().getInteger(
//                            android.R.integer.config_longAnimTime))
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            lyt_progress.setVisibility(View.GONE);
//                            recyclerView.setVisibility(View.VISIBLE);
//                        }
//                    });

        }
        if (mMatchAdapter != null) {
            mMatchAdapter.setList(WorldCupApp.groups.get(groupId).matches);
            mMatchAdapter.notifyDataSetChanged();
        }
    }
}