package com.fxtv.framework;

import android.content.Context;

import com.fxtv.framework.system.SystemCrash;
import com.fxtv.framework.system.SystemManager;

/**
 * Created by Var on 16/3/29.
 */
public class Profile {
    public static Context mContext;
    public static Configuration mConfiguration;

    public static void initProfile(Context context, Configuration configuration) {
        mContext = context;
        if (configuration == null) {
            configuration = new Configuration.Builder().setEnableDebugModel(true).build();
        }
        mConfiguration = configuration;
        SystemManager.getInstance().getSystem(SystemCrash.class);
    }
}
