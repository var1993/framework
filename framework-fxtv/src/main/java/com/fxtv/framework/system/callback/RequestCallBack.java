package com.fxtv.framework.system.callback;


import com.fxtv.framework.utils.FrameworkUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 必须重写
 * void onSuccess(T data,Response resp);
 * void onFailure(Response resp);
 * void onComplete();
 * 的RequestCallBack
 * 要使用只重写onSuccess的callBack，请使用NewRequestSimpleCallBack
 *
 * @param <T> 想要接收的类型 List<Game> 、TopicMessage……
 *            Created by wzh on 2015/1/4.
 */
public abstract class RequestCallBack<T> implements CallBack<T> {
    public Type respType;

    public RequestCallBack() {
        //获取接口泛型T的class，Type，必须要在子类才能获取Interface的T
        ParameterizedType genType = FrameworkUtils.getParameterizedType(getClass());
        if (genType!=null) {
            Type[] params = genType.getActualTypeArguments();
            if (params != null && params.length > 0)
                this.respType = params[0];
        }
        //Class<T> entityClass = (Class) params[0]; //获取 class可以用此代码
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onProgress(long progress) {

    }

    @Override
    public void onComplete() {

    }
}