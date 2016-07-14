package com.fxtv.framework;

import android.os.Environment;

/**
 * Created by var on 16/7/13.
 * Note:
 */
public class Configuration {
    /**
     * Log 开关
     */
    private boolean mEnableLog;

    /**
     * Log 本地文件
     */
    private boolean mEnableLogFile;

    /**
     * log 文件 路径
     */
    private String mLogFilePath;
    /**
     * 崩溃 重启
     */
    private boolean mEnableCrashReset;

    /**
     * 崩溃 log 本地文件
     */
    private boolean mEnableCrashLogFile;

    /**
     * wifi 缓存有效期 单位 ms
     */
    private long mCacheValidityDataWifiRate;

    /**
     * grps 缓存有效期 单位 ms
     */
    private long mCacheValidityDataFlawRate;

    private boolean mEnableDebugMode;

    public Configuration(Builder builder) {
        mEnableLog = builder.mEnableLog;
        mEnableLogFile = builder.mEnableLogFile;
        mLogFilePath = builder.mLogFilePath;
        mEnableCrashReset = builder.mEnableCrashReset;
        mEnableCrashLogFile = builder.mEnableCrashLogFile;
        mCacheValidityDataWifiRate = builder.mCacheValidityDataWifiRate;
        mCacheValidityDataFlawRate = builder.mCacheValidityDataFlawRate;
        mEnableDebugMode = builder.mEnableDebugMode;
    }

    public boolean isEnableLog() {
        return mEnableLog;
    }

    public void setEnableLog(boolean enableLog) {
        mEnableLog = enableLog;
    }

    public boolean isEnableLogFile() {
        return mEnableLogFile;
    }

    public void setEnableLogFile(boolean enableLogFile) {
        mEnableLogFile = enableLogFile;
    }

    public String getLogFilePath() {
        return mLogFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        mLogFilePath = logFilePath;
    }

    public boolean isEnableCrashReset() {
        return mEnableCrashReset;
    }

    public void setEnableCrashReset(boolean enableCrashReset) {
        mEnableCrashReset = enableCrashReset;
    }

    public boolean isEnableCrashLogFile() {
        return mEnableCrashLogFile;
    }

    public void setEnableCrashLogFile(boolean enableCrashLogFile) {
        mEnableCrashLogFile = enableCrashLogFile;
    }

    public long getCacheValidityDataWifiRate() {
        return mCacheValidityDataWifiRate;
    }

    public void setCacheValidityDataWifiRate(long cacheValidityDataWifiRate) {
        mCacheValidityDataWifiRate = cacheValidityDataWifiRate;
    }

    public long getCacheValidityDataFlawRate() {
        return mCacheValidityDataFlawRate;
    }

    public void setCacheValidityDataFlawRate(long cacheValidityDataFlawRate) {
        mCacheValidityDataFlawRate = cacheValidityDataFlawRate;
    }

    public boolean isEnableDebugMode() {
        return mEnableDebugMode;
    }

    public static class Builder {
        /**
         * Log 开关
         */
        private boolean mEnableLog;

        /**
         * Log 本地文件
         */
        private boolean mEnableLogFile;

        /**
         * log 文件 路径
         */
        private String mLogFilePath = Environment.getExternalStorageDirectory() + "/fxtv";

        /**
         * 崩溃 重启
         */
        private boolean mEnableCrashReset;

        /**
         * 崩溃 log 本地文件
         */
        private boolean mEnableCrashLogFile;

        /**
         * wifi 缓存有效期 单位 ms
         */
        private long mCacheValidityDataWifiRate = 1 * 60 * 1000;

        /**
         * grps 缓存有效期 单位 ms
         */
        private long mCacheValidityDataFlawRate = 10 * 60 * 1000;

        private boolean mEnableDebugMode;

        public Builder setEnableLog(boolean enableLog) {
            mEnableLog = enableLog;
            return this;
        }

        public Builder setEnableLogFile(boolean enableLogFile) {
            mEnableLogFile = enableLogFile;
            return this;
        }

        public Builder setLogFilePath(String logFilePath) {
            mLogFilePath = logFilePath;
            return this;
        }

        public Builder setEnableCrashReset(boolean enableCrashReset) {
            mEnableCrashReset = enableCrashReset;
            return this;
        }

        public Builder setEnableCrashLogFile(boolean enableCrashLogFile) {
            mEnableCrashLogFile = enableCrashLogFile;
            return this;
        }

        public Builder setCacheValidityDataWifiRate(long cacheValidityDataWifiRate) {
            mCacheValidityDataWifiRate = cacheValidityDataWifiRate;
            return this;
        }

        public Builder setCacheValidityDataFlawRate(long cacheValidityDataFlawRate) {
            mCacheValidityDataFlawRate = cacheValidityDataFlawRate;
            return this;
        }

        public Builder setEnableDebugModel(boolean debugModel) {
            mEnableDebugMode = debugModel;
            if (debugModel) {
                mEnableLog = true;
                mEnableLogFile = true;
                mEnableCrashReset = false;
                mEnableCrashLogFile = true;
            } else {
                mEnableLog = false;
                mEnableLogFile = false;
                mEnableCrashReset = true;
                mEnableCrashLogFile = false;
            }
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}
