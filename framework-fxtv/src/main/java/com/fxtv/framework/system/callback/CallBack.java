package com.fxtv.framework.system.callback;

import com.fxtv.framework.model.Response;

/**
 * 防止其它包New CallBack，设为默认
 * Created by wzh on 2016/1/4.
 */
interface CallBack<T> {
    void onStart();

    void onSuccess(T data,Response resp);

    void onProgress(long progress);

    void onFailure(Response resp);

    void onComplete();
}