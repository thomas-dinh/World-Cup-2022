package com.worldcuptracking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.worldcuptracking.utils.ItemAnimation;
import com.worldcuptracking.model.Team;
import com.worldcuptracking.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import com.worldcuptracking.R;

public class AdapterListGroups extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    private List<Team> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Team obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListGroups(Context context, List<Team> items) {
        this.items = items;
        ctx = context;
    }

    public void setList(List<Team> items) {
        this.items = items;
    }

    public List<Team> getItems() {
        return items;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView ranking;
        public TextView name;
        public ImageView image;
        public TextView played;
        public TextView goals;
        public TextView points;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            ranking = (TextView) v.findViewById(R.id.ranking);
            name = (TextView) v.findViewById(R.id.name);
            image = (ImageView) v.findViewById(R.id.image);
            played = (TextView) v.findViewById(R.id.played);
            goals = (TextView) v.findViewById(R.id.goal_diff);
            points = (TextView) v.findViewById(R.id.point);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public SectionViewHolder(View v) {
            super(v);
            v.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_standing, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Team p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.ranking.setText("" + (position + 1));
            view.name.setText(p.name);
            Tools.displayImageRound(ctx, view.image, Tools.getDrawable(ctx, p.iso2));
            view.played.setText("" + p.getMatchPlayed());
            view.goals.setText("" + (p.getGoalScored() - p.getGoalAgainst()));
            view.points.setText("" + p.getPoints());
            setAnimation(view.itemView, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
    }

    public void insertItem(int index, Team team) {
        items.add(index, team);
        notifyItemInserted(index);
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, ItemAnimation.FADE_IN);
            lastPosition = position;
        }
    }

    public void move(int current, int newp) {
        notifyItemMoved(current, newp);
    }
}
