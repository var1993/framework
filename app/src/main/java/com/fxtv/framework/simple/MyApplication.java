package com.fxtv.framework.simple;

import android.app.Application;

import com.fxtv.framework.Configuration;
import com.fxtv.framework.Profile;

/**
 * Created by var on 16/7/14.
 * Note:
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Configuration.Builder builder = new Configuration.Builder().setDebugModel(true).setEnableCrashReset(true);
        Profile.initProfile(this, builder.build());
    }
}
