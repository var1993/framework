package com.fxtv.framework.other;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Var on 16/3/8.
 */
public class OkHttpUtils {

    private static OkHttpUtils mInstance;

    public OkHttpClient mOkHttpClient;

    private OkHttpUtils() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 懒汉式[线程不安全]
     *
     * @return
     */
    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpUtils();
        }
        return mInstance;
    }


}
