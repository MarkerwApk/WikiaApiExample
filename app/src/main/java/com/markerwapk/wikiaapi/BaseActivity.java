package com.markerwapk.wikiaapi;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.markerwapk.wikiaapi.api.WikiaApi;

import javax.inject.Inject;

/**
 * Created by Markerwapk on 15.06.16.
 */
public abstract class BaseActivity extends AppCompatActivity {


    private static final String TAG = "BaseActivity";

    protected static final String HUBS_LIST = "HUBS_LIST";
    protected static final String WIKIS_LIST = "WIKIS_LIST";
    protected static final String NEXT = "NEXT";
    protected static final String BATCH = "BATCH";
    protected static final String LAST_POSITION = "LAST_POSITION";

    protected static final String IMAGE_TRANSITION = "IMAGE_TRANSITION";
    protected static final String TITLE_TRANSITION = "TITLE_TRANSITION";

    protected static final String HUB_EXTRA = "HUB_EXTRA";


    @Inject
    WikiaApi mWikiaApi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseApplication.getNetComponent().inject(this);

    }

    @NonNull
    protected Snackbar getFailedSnackbar(CoordinatorLayout coordinatorLayout) {
        return Snackbar.make(coordinatorLayout, getString(R.string.download_problem), Snackbar.LENGTH_INDEFINITE);
    }

}
