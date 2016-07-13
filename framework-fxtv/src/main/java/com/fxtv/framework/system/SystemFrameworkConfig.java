package com.fxtv.framework.system;

import android.os.Environment;

import com.fxtv.framework.utils.FrameworkUtils;
import com.fxtv.framework.frame.BaseSystem;

/**
 * @author FXTV-Android
 */
public class SystemFrameworkConfig extends BaseSystem {
    /**
     * log 开关
     */
    public boolean mLog = true;

    /**
     * 崩溃处理
     */
    public boolean mCrashHander = true;

    /**
     * 崩溃log缓存本地
     */
    public boolean mCrashLogNative = true;

    /**
     * 崩溃log缓存服务器(umeng)
     */
    public boolean mCrashLogNet = true;

    /**
     * 程序崩溃重启
     */
    public boolean mCrashReset = true;

    /**
     * 版本自动更新
     */
    public boolean mVersionUpgrade = true;

    /**
     * 缓存根目录
     */
    public String mCacheDir = Environment.getExternalStorageDirectory() + "/fxtv";

    /**
     * http缓存 流量环境下更新时间
     */
    public long mHttpCacheGprsPastTime = 10 * 60 * 1000;

    /**
     * http缓存 wifi环境下更新时间
     */
    public long mHttpCacheWifiPastTime = 1 * 60 * 1000;

    public String mVersion;
    public static final String platform = "android";
    public int StatusBarHeight = -1;//状态栏高度


    /**
     * ------------------短信验证--------------------------
     */

    @Override
    protected void init() {
        super.init();
        mVersion = FrameworkUtils.getVersion(mContext);
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

}
