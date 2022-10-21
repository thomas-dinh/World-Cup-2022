package com.worldcuptracking.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.worldcuptracking.utils.ItemAnimation;
import com.worldcuptracking.model.Match;
import com.worldcuptracking.utils.Tools;
import com.worldcuptracking.WorldCupApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import com.worldcuptracking.R;

public class AdapterListMatches extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    private List<Match> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private boolean header;

    public interface OnItemClickListener {
        void onItemClick(View view, Match obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListMatches(Context context, List<Match> items, boolean header) {
        this.items = items;
        ctx = context;
        this.header = header;
    }

    public void setList(List<Match> items) {
        this.items = items;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image1;
        public TextView name1;
        public ImageView image2;
        public TextView name2;
        public TextView time;
        public TextView date;
        public View lyt_parent;
        public TextView status;

        public OriginalViewHolder(View v) {
            super(v);
            image1 = (ImageView) v.findViewById(R.id.image1);
            name1 = (TextView) v.findViewById(R.id.name1);
            image2 = (ImageView) v.findViewById(R.id.image2);
            name2 = (TextView) v.findViewById(R.id.name2);
            time = (TextView) v.findViewById(R.id.time);
            date = (TextView) v.findViewById(R.id.date);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            status = v.findViewById(R.id.status);

        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title_section;

        public SectionViewHolder(View v) {
            super(v);
            title_section = (TextView) v.findViewById(R.id.title_section);
            v.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_single, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Match p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            if (header) {
                view.date.setVisibility(View.VISIBLE);
                view.date.setText(p.date_local);
            }
            view.name1.setText(WorldCupApp.teams.get(p.home_team - 1).name);
            view.name2.setText(WorldCupApp.teams.get(p.away_team - 1).name);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = formatter.parse(p.date);
                view.time.setText(new SimpleDateFormat("HH:mm").format(date));
                if (p.home_result >= 0 && p.away_result >= 0) {
                    view.time.setText(p.home_result + " : " + p.away_result);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Tools.displayImageRound(ctx, view.image1, Tools.getDrawable(ctx, Tools.getTeam(p.home_team - 1).iso2));
            Tools.displayImageRound(ctx, view.image2, Tools.getDrawable(ctx, Tools.getTeam(p.away_team - 1).iso2));
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
            if (p.finished) {
                view.status.setVisibility(View.VISIBLE);
            } else {
                view.status.setVisibility(View.GONE);
            }

            setAnimation(view.itemView, position);
        } else {
            SectionViewHolder view = (SectionViewHolder) holder;
            view.title_section.setText(p.date_local);
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
        return items.get(position).header ? VIEW_SECTION : VIEW_ITEM;
    }

    public void insertItem(int index, Match match) {
        items.add(index, match);
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

}