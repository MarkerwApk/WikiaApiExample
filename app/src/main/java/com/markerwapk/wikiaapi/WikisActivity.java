package com.markerwapk.wikiaapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.markerwapk.wikiaapi.adapters.WikisListAdapter;
import com.markerwapk.wikiaapi.models.Wiki;
import com.markerwapk.wikiaapi.models.WikiaHub;
import com.markerwapk.wikiaapi.models.WikisList;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Markerwapk on 15.06.16.
 */
public class WikisActivity extends BaseActivity {

    private static final int LIMIT = 25;

    @BindView(R.id.image)
    ImageView mImage;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.wikis_list)
    RecyclerView mWikisList;

    @BindView(R.id.coordinate_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Subscription mSubscription;

    private WikiaHub mHub;

    private int mLastBatch = 1;

    private Snackbar mProgressSnackbar;

    private int mNext = -1;

    private ArrayList<Wiki> mWikis;

    private int mLastPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wikis_activity_layout);

        ButterKnife.bind(this);

        mHub = (WikiaHub) getIntent().getSerializableExtra(HUB_EXTRA);


        prepareToolbar();

        if (savedInstanceState != null) {
            mWikis = (ArrayList<Wiki>) savedInstanceState.getSerializable(WIKIS_LIST);
            mLastBatch = savedInstanceState.getInt(BATCH);
            mNext = savedInstanceState.getInt(NEXT);

            mWikisList.scrollToPosition(savedInstanceState.getInt(LAST_POSITION));
        } else {
            mWikis = new ArrayList<>();
            downloadWikis();
        }


        prepareRecyclerView();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(WIKIS_LIST, mWikis);
        outState.putSerializable(BATCH, mLastBatch);
        outState.putSerializable(NEXT, mNext);
        outState.putSerializable(LAST_POSITION, mLastPosition);
        super.onSaveInstanceState(outState);

    }

    private void prepareRecyclerView() {
        mWikisList.setLayoutManager(new LinearLayoutManager(this));
        mWikisList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                           @Override
                                           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                               super.onScrolled(recyclerView, dx, dy);

                                               LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mWikisList.getLayoutManager();

                                               int mScrollPosition = linearLayoutManager.findLastVisibleItemPosition();
                                               mLastPosition = linearLayoutManager.findFirstVisibleItemPosition();

                                               if (mWikis.size() - mScrollPosition < 10 && mSubscription == null) {

                                                   Log.i("Marek", "onScrolled: " + mScrollPosition + " " + mWikis.size());
                                                   downloadWikis(++mLastBatch);
                                               }

                                           }

                                           @Override
                                           public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                               super.onScrollStateChanged(recyclerView, newState);
                                           }
                                       }

        );

        mWikisList.setAdapter(new WikisListAdapter(mWikis, new WikisListAdapter.OnWikiClickListener() {
            @Override
            public void onClick(Wiki wiki) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(wiki.getUrl()));
                startActivity(i);
            }
        }));

    }

    private void prepareToolbar() {
        mCollapsingToolbarLayout.setTitle(mHub.getName());

        ViewCompat.setTransitionName(mImage, IMAGE_TRANSITION);

        Picasso.with(this).load(mHub.getImage()).into(mImage, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
                mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

                new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            mCollapsingToolbarLayout.setStatusBarScrimColor(vibrant.getRgb());
                            mCollapsingToolbarLayout.setContentScrimColor(vibrant.getRgb());
                            mCollapsingToolbarLayout.setBackgroundColor(vibrant.getRgb());
                            mCollapsingToolbarLayout.setCollapsedTitleTextColor(vibrant.getTitleTextColor());

                        }


                    }
                });
            }

            @Override
            public void onError() {

            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void downloadWikis() {
        downloadWikis(1);
    }

    private void downloadWikis(int batch) {

        if (mNext == 0)
            return;

        showProgressSnackbar();
        mLastBatch = batch;

        mSubscription = mWikiaApi.getWikis(mHub.getQueryName(), (mNext == -1) ? LIMIT : mNext, batch).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<WikisList>() {
            @Override
            public void onCompleted() {
                mSubscription = null;
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                onFailed();
            }

            @Override
            public void onNext(WikisList wikisList) {
                onSuccess();
                mNext = wikisList.getNext();
                mWikis.addAll(wikisList.getItems());

                mWikisList.getAdapter().notifyDataSetChanged();
            }
        });

    }

    private void showProgressSnackbar() {
        if (mProgressSnackbar == null)
            mProgressSnackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.download_progress), Snackbar.LENGTH_INDEFINITE);

        if (!mProgressSnackbar.isShown())
            mProgressSnackbar.show();


    }

    private void hideProgressSnackbar() {
        if (mProgressSnackbar != null && mProgressSnackbar.isShown())
            mProgressSnackbar.dismiss();
    }

    private void onSuccess() {
        hideProgressSnackbar();

        Snackbar.make(mCoordinatorLayout, getString(R.string.download_wikis_success), Snackbar.LENGTH_LONG).show();
    }


    private void onFailed() {
        hideProgressSnackbar();

        final Snackbar failedSnackbar = getFailedSnackbar(mCoordinatorLayout);
        failedSnackbar.setAction(R.string.try_again, new OnClickListener() {
            @Override
            public void onClick(View v) {
                failedSnackbar.dismiss();
                downloadWikis(mLastBatch);
            }
        });
        failedSnackbar.show();


    }

    @Override
    protected void onDestroy() {

        if (mSubscription != null)
            mSubscription.unsubscribe();

        super.onDestroy();
    }


}
