package com.fxtv.framework.model;

/**
 * Created by wzh on 2015/12/30.
 * 返回结果  T 为想要的泛型类型，支持对象和List<T>
 */
public class Response<T> {
    public String mUrl;
    public String mMetadata;
    public T mWrapperData;
    public int mCode;
    public String mMessage;
    public long mTime;

    public boolean mFromCache;

    @Override
    public String toString() {
        return "Response{" +
                "code=" + mCode +
                ", msg='" + mMessage + '\'' +
                ", data=" + mWrapperData +
                ", time=" + mTime +
                ", fromCache=" + mFromCache +
                ", reqUrl='" + mUrl + '\'' +
                '}';
    }
}
