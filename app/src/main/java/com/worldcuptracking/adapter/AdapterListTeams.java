package com.worldcuptracking.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.worldcuptracking.utils.ItemAnimation;
import com.worldcuptracking.model.Team;
import com.worldcuptracking.utils.Tools;

import java.util.List;
import com.worldcuptracking.R;

public class AdapterListTeams extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Team> items;
    private Context ctx;
    private AdapterListTeams.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Team obj, int position);
    }

    public void setOnItemClickListener(final AdapterListTeams.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListTeams(Context context, List<Team> items) {
        this.items = items;
        ctx = context;
    }

    public void setList(List<Team> items) {
        this.items = items;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;

        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.img);
            name = v.findViewById(R.id.team);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        vh = new AdapterListTeams.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Team p = items.get(position);
        if (holder instanceof AdapterListTeams.OriginalViewHolder) {
            AdapterListTeams.OriginalViewHolder view = (AdapterListTeams.OriginalViewHolder) holder;
            view.name.setText(p.name);
            Tools.displayImageRound(ctx, view.image, Tools.getDrawable(ctx, p.iso2));
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
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

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, ItemAnimation.FADE_IN);
            lastPosition = position;
        }
    }
}
