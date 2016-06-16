package com.markerwapk.wikiaapi.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.markerwapk.wikiaapi.R;
import com.markerwapk.wikiaapi.models.Wiki;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Markerwapk on 15.06.16.
 */
public class WikisListAdapter extends RecyclerView.Adapter<WikisListAdapter.ViewHolder> {
    private ArrayList<Wiki> mWikis;

    private OnWikiClickListener mOnWikiClickListener;

    public interface OnWikiClickListener {
        void onClick(Wiki wiki);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.wiki_title)
        public TextView mTextView;

        @BindView(R.id.wiki_desc)
        public TextView mDesc;

        @BindView(R.id.wiki_icon)
        public ImageView mImage;

        public ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }
    }

    public WikisListAdapter(ArrayList<Wiki> wikis, OnWikiClickListener onWikiClickListener) {
        mWikis = wikis;
        mOnWikiClickListener = onWikiClickListener;
    }

    @Override
    public WikisListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wiki_card_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Wiki currentWiki = mWikis.get(position);

        holder.mTextView.setText(currentWiki.getName());
        holder.mDesc.setText(currentWiki.getDesc());

        String imagePath = currentWiki.getImage();

        if (imagePath != null && !imagePath.isEmpty())
            Picasso.with(holder.mImage.getContext()).load(currentWiki.getImage()).into(holder.mImage);
        else
            holder.mImage.setImageBitmap(null);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnWikiClickListener.onClick(currentWiki);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWikis.size();
    }
}
