package com.worldcuptracking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.worldcuptracking.R;
import com.worldcuptracking.WorldCupApp;
import com.worldcuptracking.activity.MatchDetailActivity;
import com.worldcuptracking.adapter.AdapterListKOMatches;
import com.worldcuptracking.adapter.AdapterListMatches;
import com.worldcuptracking.model.KOMatch;
import com.worldcuptracking.model.Match;

public class MatchFragment extends Fragment {

    public MatchFragment() {
    }

    public static MatchFragment newInstance(String id) {
        MatchFragment fragment = new MatchFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("phaseId", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_match, container, false);

        Bundle args = getArguments();
        String phaseId = args.getString("phaseId", "Group");

        //set data and list adapter
        switch (phaseId) {
            case "Group":
                listMatch(root, container);
                break;
            case "Round of 16":
                listKOMatch(root, 0);
                break;
            case "Final":
                listKOMatch(root, 1);
                break;
            case "Third place play-off":
                listKOMatch(root, 2);
                break;
            case "Semi-finals":
                listKOMatch(root, 3);
                break;
            case "Quarter-finals":
                listKOMatch(root, 4);
                break;
        }

        return root;
    }

    private void listMatch(View root, final View parent) {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        AdapterListMatches mAdapter = new AdapterListMatches(parent.getContext(), WorldCupApp.matches, false);
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListMatches.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Match obj, int position) {
                Intent intent = new Intent(parent.getContext(), MatchDetailActivity.class);
                intent.putExtra("match", obj);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void listKOMatch(View root, int id) {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        AdapterListKOMatches mAdapter = new AdapterListKOMatches(this.getContext(), WorldCupApp.knockouts.get(id).matches, false);
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListKOMatches.OnItemClickListener() {
            @Override
            public void onItemClick(View view, KOMatch obj, int position) {
                Intent intent = new Intent(getContext(), MatchDetailActivity.class);
                intent.putExtra("KOmatch", obj);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);

    }
}
