package com.worldcuptracking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.worldcuptracking.utils.ItemAnimation;
import com.worldcuptracking.model.StadiumInfo;
import com.worldcuptracking.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import com.worldcuptracking.R;

public class AdapterListStadiums extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<StadiumInfo> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, StadiumInfo obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListStadiums(Context context, List<StadiumInfo> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title,st_opening,st_city,st_capacity;
        public Button button;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            title = v.findViewById(R.id.stadium_name_text);
            st_capacity = v.findViewById(R.id.stadium_capacity_text);
            st_city = v.findViewById(R.id.stadium_location_text);
            st_opening = v.findViewById(R.id.stadium_opening_text);
            button = v.findViewById(R.id.button);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stadium_card, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            StadiumInfo p = items.get(position);
            Tools.displayImageOriginal(ctx, view.image, p.imgUrl);
            view.title.setText(p.namee);

            view.st_capacity.setText(p.capacityy);
            view.st_city.setText(p.cityy);
            view.st_opening.setText(p.opening);

            view.button.setOnClickListener(view1 -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view1, items.get(position), position);
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
            ItemAnimation.animate(view, on_attach ? position : -1, ItemAnimation.BOTTOM_UP);
            lastPosition = position;
        }
    }

}
