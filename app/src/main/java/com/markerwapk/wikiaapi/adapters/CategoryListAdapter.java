package com.markerwapk.wikiaapi.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.markerwapk.wikiaapi.R;
import com.markerwapk.wikiaapi.models.WikiaHub;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Markerwapk on 15.06.16.
 */
public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    private ArrayList<WikiaHub> mHubs;

    private OnHubClickListener mOnHubClickListener;

    public interface OnHubClickListener {
        void onClick(WikiaHub hub, ImageView image, TextView title);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.hub_title)
        public TextView mTextView;

        @BindView(R.id.hub_icon)
        public ImageView mImage;

        public ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }
    }

    public CategoryListAdapter(ArrayList<WikiaHub> hubs, OnHubClickListener onHubClickListener) {
        mHubs = hubs;
        mOnHubClickListener = onHubClickListener;
    }

    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_card_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final WikiaHub currentHub = mHubs.get(position);

        holder.mTextView.setText(currentHub.getName());
        Picasso.with(holder.mImage.getContext()).load(currentHub.getImage()).into(holder.mImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnHubClickListener.onClick(currentHub, holder.mImage, holder.mTextView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHubs.size();
    }
}
