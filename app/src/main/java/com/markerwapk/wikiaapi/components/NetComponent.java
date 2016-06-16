package com.markerwapk.wikiaapi.components;

import com.markerwapk.wikiaapi.BaseActivity;
import com.markerwapk.wikiaapi.modules.NetModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Markerwapk on 15.06.16.
 */

@Singleton
@Component(modules = NetModule.class)
public interface NetComponent {
    void inject(BaseActivity baseActivity);
}
