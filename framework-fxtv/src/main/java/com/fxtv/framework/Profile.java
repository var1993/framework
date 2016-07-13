package com.fxtv.framework;

import android.content.Context;

import com.fxtv.framework.system.SystemCrash;
import com.fxtv.framework.system.SystemFrameworkConfig;
import com.fxtv.framework.system.SystemManager;

/**
 * Created by Var on 16/3/29.
 */
public class Profile {
    public static Context mContext;

    public static void initProfile(Context context) {
        mContext = context;
        SystemManager.getInstance().getSystem(SystemCrash.class);
        SystemManager.getInstance().getSystem(SystemFrameworkConfig.class);
    }
}
