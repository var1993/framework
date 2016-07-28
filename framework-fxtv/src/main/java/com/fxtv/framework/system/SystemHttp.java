package com.fxtv.framework.system;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.fxtv.framework.Profile;
import com.fxtv.framework.frame.BaseSystem;
import com.fxtv.framework.model.Cache;
import com.fxtv.framework.model.RequestData;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.other.OkHttpUtils;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.utils.FrameworkUtils;
import com.fxtv.framework.utils.Logger;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * http接口系统
 *
 * @author FXTV-Android
 */
public class SystemHttp extends BaseSystem {
    public static final int CODE_SUCCESS = 2000;
    public static final int CODE_ERROR_UNKNOWN = 1000;
    public static final int CODE_ERROR_DATA_FORMAT = 1001;
    public static final int CODE_ERROR_TIMEOUT = 1002;
    public static final int CODE_ERROR_URL_CONSTRUCT = 1003;
    public static final int CODE_ERROR_NOT_NET = 1004;
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static final String TAG_RESPONSE = "http_response";
    private static final String TAG_REQUEST = "http_request";
    private static final int MODEL_NORMAL = 0;
    private static final int MODEL_CACHE_UPDATE = 1;

    private Gson mGson;
    private OkHttpClient mOkHttpClient;
    private android.os.Handler mHandler;

    private List<Call> mPoolCalls;

    @Override
    protected void init() {
        super.init();
        mOkHttpClient = OkHttpUtils.getInstance().mOkHttpClient;
        mPoolCalls = new ArrayList<>();
        mHandler = new Handler();
        mGson = new Gson();
    }

    @Override
    protected void destroy() {
        super.destroy();
        mOkHttpClient = null;
        mHandler = null;
        mGson = null;
        mPoolCalls.clear();
        mPoolCalls = null;
    }

    public void cancelRequest(Context context) {
        for (Call call : mPoolCalls) {
            if (call != null
                    && call.request() != null
                    && call.request().tag() == context) {
                try {
                    call.cancel();
                } catch (Exception e) {
                    //
                }
                mPoolCalls.remove(call);
                break;
            }
        }
    }

    public <T> void net(final Context context, final RequestData requestData, final RequestCallBack<T> callBack) {
        Logger.d(TAG_REQUEST, String.format("net,flag=%s,request_type=%s,useCache=%b",
                requestData.getLogFlag(),
                requestData.getRequestType(),
                requestData.isUseCache()));

        //特殊处理
        requestData.getUrl();

        if (callBack != null) {
            callBack.onStart();
        }

        if (requestData.isUseCache()) {
            String key = String.format("%s?%s", requestData.getUrl(), constructorParamsForGet(requestData.getRequestParams()));
            Logger.d(TAG_REQUEST, String.format("net,flag=%s,cache key=%s", requestData.getLogFlag(), key));
            Cache cache = SystemManager.getInstance().getSystem(SystemHttpCache.class).getCache(key);
            if (cache != null) {
                if (callBack != null) {
                    Logger.d(TAG_RESPONSE, "net,flag=" + requestData.getLogFlag() + ",cache," + "response=" + cache.value);
                    final Response<T> resp = wrapperResponse(cache.value, requestData, callBack);
                    resp.mFromCache = true;
                    resp.mUrl = requestData.getUrl();
                    callBack.onSuccess(resp.mWrapperData, resp);
                    callBack.onComplete();
                }

                if (FrameworkUtils.isNetworkConnected(mContext)) {
                    long tmp = System.currentTimeMillis() - cache.time;
                    if (FrameworkUtils.isMobileConnected(mContext)) {
                        // 数据流量
                        if (tmp > Profile.mConfiguration.getCacheValidityDataFlawRate()) {
                            Logger.d(TAG_REQUEST, String.format("net,flag=%s,gprs start update cache", requestData.getLogFlag()));
                            netDispatcher(context, requestData, MODEL_CACHE_UPDATE, null);
                        }
                    } else if (FrameworkUtils.isWifiConnected(mContext)) {
                        // wifi
                        if (tmp > Profile.mConfiguration.getCacheValidityDataWifiRate()) {
                            Logger.d(TAG_REQUEST, String.format("net,flag=%s,wifi start update cache", requestData.getLogFlag()));
                            netDispatcher(context, requestData, MODEL_CACHE_UPDATE, null);
                        }
                    }
                }
            } else {
                netDispatcher(context, requestData, MODEL_NORMAL, callBack);
            }
        } else {
            netDispatcher(context, requestData, MODEL_NORMAL, callBack);
        }
    }

    private <T> void netDispatcher(final Context context, final RequestData requestData, final int model, final RequestCallBack<T> callBack) {
        if (requestData.getRequestType() == RequestData.REQUEST_TYPE_GET) {
            netGet(context, requestData, model, callBack);
        } else {
            netPost(context, requestData, model, callBack);
        }
    }

    private <T> void netGet(final Context context, final RequestData requestData, final int model, final RequestCallBack<T> callBack) {
        String params = constructorParamsForGet(requestData.getRequestParams());
        String url;
        if (!TextUtils.isEmpty(params)) {
            url = String.format("%s?%s", requestData.getUrl(), params);
        } else {
            url = requestData.getUrl();
        }

        final Request request = new Request.Builder()
                .get()
                .url(url)
                .tag(context)
                .build();
        Logger.d(TAG_REQUEST, String.format("netGet,flag=%s,model=%s,url=%s", requestData.getLogFlag(), model, request.url().toString()));

        Call call = mOkHttpClient.newCall(request);
        mPoolCalls.add(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d(TAG_RESPONSE, String.format("netGet,flag=%s,model=%s,onFailure", requestData.getLogFlag(), model));
                int code;
                if (FrameworkUtils.isNetworkConnected(Profile.mContext)) {
                    code = CODE_ERROR_TIMEOUT;
                } else {
                    code = CODE_ERROR_NOT_NET;
                }
                handelError(call.request().url().toString(), code, callBack);
                if (mPoolCalls != null) {
                    mPoolCalls.remove(call);
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {
                try {
                    String responseStr = response.body().string();
                    Logger.d(TAG_RESPONSE, String.format("netGet,flag=%s,model=%s,onResponse,responseStr=%s", requestData.getLogFlag(), model, responseStr));

                    if (response.isSuccessful()) {
                        Logger.d(TAG_RESPONSE, String.format("netGet,flag=%s,model=%s,onResponse,isSuccessful", requestData.getLogFlag(), model));

                        final Response<T> resp = wrapperResponse(responseStr, requestData, callBack);
                        resp.mUrl = call.request().url().toString();
                        if (resp.mCode == CODE_SUCCESS) {
                            Logger.d(TAG_RESPONSE, String.format("netGet,flag=%s,model=%s,onResponse,isSuccessful,code is success", requestData.getLogFlag(), model));
                            if (callBack != null) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callBack.onSuccess(resp.mWrapperData, resp);
                                        callBack.onComplete();
                                    }
                                });
                            }
                            if (requestData.isCacheEnable()) { // 缓存数据
                                String key = String.format("%s?%s", requestData.getUrl(), constructorParamsForGet(requestData.getRequestParams()));
                                SystemManager.getInstance().getSystem(SystemHttpCache.class).updateCache(key, responseStr);
                            }
                        } else {
                            Logger.d(TAG_RESPONSE, String.format("netGet,flag=%s,model=%s,onResponse,isSuccessful,code is failure,msg=%s", requestData.getLogFlag(), model, resp.mMessage));
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (callBack != null) {
                                        callBack.onFailure(resp);
                                        callBack.onComplete();
                                    }
                                }
                            });
                        }
                    } else {
                        Logger.d(TAG_RESPONSE, String.format("netGet,flag=%s,model=%s,onResponse,is not Successful", requestData.getLogFlag(), model));
                        handelError(call.request().url().toString(), CODE_ERROR_UNKNOWN, callBack);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.d(TAG_RESPONSE, String.format("netGet,flag=%s,model=%s,onResponse,ioException=%s", requestData.getLogFlag(), model, e.getMessage()));
                    handelError(call.request().url().toString(), CODE_ERROR_UNKNOWN, callBack);
                }

                if (mPoolCalls != null) {
                    mPoolCalls.remove(call);
                }
            }
        });
    }

    private <T> void netPost(final Context context, final RequestData requestData, final int model, final RequestCallBack<T> callBack) {
        String params = constructorParams(requestData.getRequestParams());
        Logger.d(TAG_REQUEST, String.format("netPost,flag=%s,model=%s,url=%s?%s", requestData.getLogFlag(), model, requestData.getUrl(), params));

        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
        final Request request = new Request.Builder()
                .post(body)
                .url(requestData.getUrl())
                .tag(context)
                .build();
        Call call = mOkHttpClient.newCall(request);
        mPoolCalls.add(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d(TAG_RESPONSE, String.format("netPost,flag=%s,model=%s,onFailure", requestData.getLogFlag(), model));
                int code;
                if (FrameworkUtils.isNetworkConnected(Profile.mContext)) {
                    code = CODE_ERROR_TIMEOUT;
                } else {
                    code = CODE_ERROR_NOT_NET;
                }
                handelError(call.request().url().toString(), code, callBack);

                if (mPoolCalls != null) {
                    mPoolCalls.remove(call);
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {
                try {
                    String responseStr = response.body().string();
                    Logger.d(TAG_RESPONSE, String.format("netPost,flag=%s,model=%s,onResponse,responseStr=%s", requestData.getLogFlag(), model, responseStr));
                    if (response.isSuccessful()) {
                        Logger.d(TAG_RESPONSE, String.format("netPost,flag=%s,model=%s,onResponse,isSuccessful", requestData.getLogFlag(), model));
                        final Response<T> resp = wrapperResponse(responseStr, requestData, callBack);
                        resp.mUrl = call.request().url().toString();

                        if (resp.mCode == CODE_SUCCESS) {
                            Logger.d(TAG_RESPONSE, String.format("netPost,flag=%s,model=%s,onResponse,code is success", requestData.getLogFlag(), model));
                            if (callBack != null) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callBack.onSuccess(resp.mWrapperData, resp);
                                        callBack.onComplete();
                                    }
                                });
                            }
                            if (requestData.isCacheEnable()) { // 缓存数据
                                String key = String.format("%s?%s", requestData.getUrl(), constructorParamsForGet(requestData.getRequestParams()));
                                SystemManager.getInstance().getSystem(SystemHttpCache.class).updateCache(key, responseStr);
                            }
                        } else {
                            Logger.d(TAG_RESPONSE, String.format("netPost,flag=%s,model=%s,onResponse,code is failure,msg=%s", requestData.getLogFlag(), model, resp.mMessage));
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (callBack != null) {
                                        callBack.onFailure(resp);
                                        callBack.onComplete();
                                    }
                                }
                            });
                        }
                    } else {
                        Logger.d(TAG_RESPONSE, String.format("netPost,flag=%s,model=%s,onResponse,is not Successful", requestData.getLogFlag(), model));
                        handelError(call.request().url().toString(), CODE_ERROR_UNKNOWN, callBack);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.d(TAG_RESPONSE, String.format("netPost,flag=%s,model=%s,onResponse,IOException=%s", requestData.getLogFlag(), model, e.getMessage()));
                    handelError(call.request().url().toString(), CODE_ERROR_UNKNOWN, callBack);
                }

                if (mPoolCalls != null) {
                    mPoolCalls.remove(call);
                }
            }
        });
    }

    private String getErrorMessage(int error_code) {
        String message;
        switch (error_code) {
            case CODE_ERROR_NOT_NET:
                message = "网络未连接";
                break;
            case CODE_ERROR_TIMEOUT:
                message = "网络连接超时";
                break;
            case CODE_ERROR_URL_CONSTRUCT:
                message = "URL构造错误";
                break;
            case CODE_ERROR_DATA_FORMAT:
                message = "返回数据序列化错误";
                break;

            case CODE_ERROR_UNKNOWN:
            default:
                message = "未知错误";
                break;

        }
        return message;
    }

    /**
     * 解析 Respjson 转为Response<T>
     *
     * @param respJson
     * @param callBack
     * @param <T>
     * @return
     */
    private <T> Response<T> wrapperResponse(String respJson, RequestData requestData, RequestCallBack<T> callBack) {
        Response<T> response = new Response<>();

        if (requestData.isWrapperResponse()) {
            try {
                JSONObject jsonObject = new JSONObject(respJson);
                response.mMetadata = respJson;
                response.mCode = jsonObject.getInt("code");
                response.mMessage = jsonObject.getString("message");
                response.mTime = jsonObject.getLong("time");
                response.mFromCache = false;
                String data = jsonObject.getString("data");
                if (callBack != null && callBack.respType != null && callBack.respType != String.class) {
                    response.mWrapperData = mGson.fromJson(data, callBack.respType);
                } else {
                    response.mWrapperData = (T) data;
                }
            } catch (Exception e) {
                Logger.e(TAG_RESPONSE, String.format("wrapperResponse,flag=%s,data format error=%s", requestData.getLogFlag(), e.getMessage()));
                e.printStackTrace();
                response.mCode = CODE_ERROR_DATA_FORMAT;
                response.mMessage = getErrorMessage(CODE_ERROR_DATA_FORMAT);
            }
        } else {
            response.mMetadata = respJson;
            response.mCode = CODE_SUCCESS;
            response.mMessage = "数据请求成功";
            response.mFromCache = false;
            if (callBack != null && callBack.respType != null && callBack.respType != String.class) {
                response.mWrapperData = mGson.fromJson(respJson, callBack.respType);
            } else {
                response.mWrapperData = (T) respJson;
            }
        }
        return response;
    }

    private <T> void handelError(final String url, final int errorCode, final RequestCallBack<T> callBack) {
        if (callBack != null) {
            final Response resp = new Response<>();
            resp.mUrl = url;
            resp.mCode = errorCode;
            resp.mMessage = getErrorMessage(errorCode);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onFailure(resp);
                    callBack.onComplete();
                }
            });
        }
    }

    private String constructorParamsForGet(Map<String, String> requestParamsMap) {
        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        for (String key : requestParamsMap.keySet()) {
            if (pos > 0) {
                tempParams.append("&");
            }
            tempParams.append(String.format("%s=%s", key, requestParamsMap.get(key)));
            pos++;
        }

        return tempParams.toString();
    }

    private String constructorParams(Map<String, String> requestParamsMap) {
        StringBuilder tempParams = new StringBuilder();
        for (String key : requestParamsMap.keySet()) {
            tempParams.append(String.format("%s=%s", key, requestParamsMap.get(key)));
        }

        return tempParams.toString();
    }
}
