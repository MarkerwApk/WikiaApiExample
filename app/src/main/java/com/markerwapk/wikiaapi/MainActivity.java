package com.markerwapk.wikiaapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.markerwapk.wikiaapi.adapters.CategoryListAdapter;
import com.markerwapk.wikiaapi.models.WikiaHub;
import com.markerwapk.wikiaapi.models.WikiaHubs;
import com.markerwapk.wikiaapi.models.WikisList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.coordinate_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.category_list)
    RecyclerView mCategoryList;

    private CategoryListAdapter mCategoryListAdapter;

    protected ArrayList<WikiaHub> mWikiaHubs;

    private Subscription mSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initView();


        if (savedInstanceState != null) {
            mWikiaHubs = (ArrayList<WikiaHub>) savedInstanceState.getSerializable(HUBS_LIST);
            notifyHubsList();
        } else {
            mWikiaHubs = new ArrayList<>();
            downloadHubs();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(HUBS_LIST, mWikiaHubs);
        super.onSaveInstanceState(outState);

    }

    private void initView() {
        mCategoryList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


    }

    protected void downloadHubs() {
        mSubscription = mWikiaApi.getWikiaHubs().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).flatMapIterable(new Func1<WikiaHubs, ArrayList<WikiaHub>>() {
            @Override
            public ArrayList<WikiaHub> call(WikiaHubs wikiaHubs) {
                mWikiaHubs.clear();

                mWikiaHubs.addAll(wikiaHubs.getList().values());
                return mWikiaHubs;
            }
        }).doOnNext(new Action1<WikiaHub>() {
            @Override
            public void call(final WikiaHub wikiaHub) {

                mWikiaApi.getWikis(wikiaHub.getQueryName(), 1, 1).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<WikisList>() {
                    @Override
                    public void call(WikisList wikisList) {
                        WikiaHub currentHub = findHub(wikiaHub.getName());

                        if (currentHub == null)
                            return;

                        if (wikisList.getItems().size() == 0) {
                            mWikiaHubs.remove(currentHub);
                            return;
                        }
                        currentHub.setImage(wikisList.getItems().get(0).getImage());

                        onSuccess();
                    }

                    private WikiaHub findHub(String name) {
                        for (WikiaHub hub : mWikiaHubs)
                            if (hub.getName().equalsIgnoreCase(name))
                                return hub;

                        return null;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        onFailed();
                    }
                });
            }
        }).subscribe();

    }

    @Override
    protected void onDestroy() {

        if (mSubscription != null)
            mSubscription.unsubscribe();
        super.onDestroy();
    }


    protected void onFailed() {
        final Snackbar failedSnackbar = getFailedSnackbar(mCoordinatorLayout);
        failedSnackbar.setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedSnackbar.dismiss();
                downloadHubs();
            }
        });
        failedSnackbar.show();
    }


    protected void onSuccess() {
        final Snackbar successSnackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.download_success), Snackbar.LENGTH_LONG);
        successSnackbar.show();

        notifyHubsList();

    }


    private void notifyHubsList() {
        if (mCategoryListAdapter == null) {
            mCategoryListAdapter = new CategoryListAdapter(mWikiaHubs, mOnHubClickListener);
            mCategoryList.setAdapter(mCategoryListAdapter);
        } else {
            mCategoryListAdapter.notifyDataSetChanged();
        }
    }

    private CategoryListAdapter.OnHubClickListener mOnHubClickListener = new CategoryListAdapter.OnHubClickListener() {
        @Override
        public void onClick(WikiaHub hub, ImageView image, TextView title) {
            Intent intentPerms = new Intent(MainActivity.this, WikisActivity.class);
            intentPerms.putExtra(HUB_EXTRA, hub);


            Pair<View, String> imagePair = Pair.create((View) image, IMAGE_TRANSITION);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imagePair);
            ActivityCompat.startActivity(MainActivity.this, intentPerms, options.toBundle());

        }
    };
}
