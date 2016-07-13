package com.fxtv.framework.model;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by var on 16/6/30.
 * Note:
 */
public class RequestData {
    public static final int REQUEST_TYPE_GET = 0;
    public static final int REQUEST_TYPE_POST = 1;

    private transient String mUrl;
    private transient Map<String, String> mRequestParams;
    private transient int mRequestType = REQUEST_TYPE_POST;

    private transient boolean mUseCache = false;
    private transient boolean mCacheEnable = false;

    private transient String mLogFlag;

    //应用接口需要包装处理response
    private transient boolean mWrapperResponse = true;

    public RequestData() {
        mRequestParams = new HashMap<String, String>();
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public Map<String, String> getRequestParams() {
        return mRequestParams;
    }

    public RequestData setRequestParams(Map<String, String> requestParams) {
        mRequestParams.putAll(requestParams);
        return this;
    }

    public int getRequestType() {
        return mRequestType;
    }

    public RequestData setRequestType(int requestType) {
        mRequestType = requestType;
        return this;
    }

    public boolean isUseCache() {
        return mUseCache;
    }

    public RequestData setUseCache(boolean useCache) {
        mUseCache = useCache;
        return this;
    }

    public boolean isCacheEnable() {
        return mCacheEnable;
    }

    public RequestData setCacheEnable(boolean cacheEnable) {
        mCacheEnable = cacheEnable;
        return this;
    }

    public String getLogFlag() {
        if (TextUtils.isEmpty(mLogFlag))
            return "";
        return mLogFlag;
    }

    public RequestData setLogFlag(String logFlag) {
        mLogFlag = logFlag;
        return this;
    }

    public boolean isWrapperResponse() {
        return mWrapperResponse;
    }

    public void setWrapperResponse(boolean wrapperResponse) {
        mWrapperResponse = wrapperResponse;
    }
}
