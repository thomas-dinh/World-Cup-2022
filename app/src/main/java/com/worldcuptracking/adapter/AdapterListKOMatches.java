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

import com.worldcuptracking.model.KOMatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.worldcuptracking.R;
import com.worldcuptracking.utils.ItemAnimation;
import com.worldcuptracking.utils.Tools;

public class AdapterListKOMatches extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    private List<KOMatch> items = new ArrayList<>();
    private Context ctx;
    private AdapterListKOMatches.OnItemClickListener mOnItemClickListener;
    private boolean header;

    public interface OnItemClickListener {
        void onItemClick(View view, KOMatch obj, int position);
    }

    public void setOnItemClickListener(final AdapterListKOMatches.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListKOMatches(Context context, List<KOMatch> items, boolean header) {
        this.items = items;
        ctx = context;
        this.header = header;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image1;
        public TextView name1;
        public ImageView image2;
        public TextView name2;
        public TextView time;
        public TextView date;
        public View lyt_parent;
        public TextView penalty;
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
            penalty = v.findViewById(R.id.penalty);
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
            vh = new AdapterListKOMatches.OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new AdapterListKOMatches.SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        KOMatch p = items.get(position);
        if (holder instanceof AdapterListKOMatches.OriginalViewHolder) {
            AdapterListKOMatches.OriginalViewHolder view = (AdapterListKOMatches.OriginalViewHolder) holder;
//            if (header) {
//                view.date.setVisibility(View.VISIBLE);
//                view.date.setText(p.date_local);
//            }
            if (p.home_team.getClass().equals(String.class)) {
                view.name1.setText(p.home_team.toString());
            } else {
                if (Integer.valueOf(p.home_team.toString()) < 32) {
                    view.name1.setText(Tools.getTeam(Integer.valueOf(p.home_team.toString()) - 1).name);
                    Tools.displayImageRound(ctx, view.image1, Tools.getDrawable(ctx, Tools.getTeam(Integer.valueOf(p.home_team.toString()) - 1).iso2));
                } else
                    view.name1.setText(p.home_team.toString());
            }

            if (p.away_team.getClass().equals(String.class)) {
                view.name2.setText(p.away_team.toString());
            } else {
                if (Integer.valueOf(p.away_team.toString()) < 32) {
                    view.name2.setText(Tools.getTeam(Integer.valueOf(p.away_team.toString()) - 1).name);
                    Tools.displayImageRound(ctx, view.image2, Tools.getDrawable(ctx, Tools.getTeam(Integer.valueOf(p.away_team.toString()) - 1).iso2));

                } else
                    view.name2.setText(p.away_team.toString());
            }

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
            //Tools.displayImageRound(ctx, view.image1, Tools.getDrawable(ctx,R));
            //Tools.displayImageRound(ctx, view.image2, Tools.getDrawable(ctx, Tools.getTeam(p.away_team - 1).iso2));
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

            if (p.home_penalty >= 0 || p.away_penalty >= 0) {
                view.penalty.setVisibility(View.VISIBLE);
                view.penalty.setText("(" + p.home_penalty + " : " + p.away_penalty + ")");
            }else{
                view.penalty.setVisibility(View.GONE);
            }

            setAnimation(view.itemView, position);
        } else {
            AdapterListKOMatches.SectionViewHolder view = (AdapterListKOMatches.SectionViewHolder) holder;
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

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, ItemAnimation.FADE_IN);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).header ? VIEW_SECTION : VIEW_ITEM;
    }

    public void insertItem(int index, KOMatch match) {
        items.add(index, match);
        notifyItemInserted(index);
    }
}
