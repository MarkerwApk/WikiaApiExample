package com.markerwapk.wikiaapi;

import android.app.Application;

import com.markerwapk.wikiaapi.components.DaggerNetComponent;
import com.markerwapk.wikiaapi.components.NetComponent;
import com.markerwapk.wikiaapi.modules.NetModule;

/**
 * Created by Markerwapk on 15.06.16.
 */
public class BaseApplication extends Application {

    private static final String BASE_URL="http://www.wikia.com/api/v1/";


    private static NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mNetComponent = DaggerNetComponent.builder().netModule(new NetModule(this, BASE_URL)).build();
    }

    public static synchronized NetComponent getNetComponent() {
        return mNetComponent;
    }
}
