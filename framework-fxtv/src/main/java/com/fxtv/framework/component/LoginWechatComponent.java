package com.fxtv.framework.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.fxtv.framework.model.RequestData;
import com.fxtv.framework.model.Response;
import com.fxtv.framework.model.TLoginModel;
import com.fxtv.framework.system.SystemHttp;
import com.fxtv.framework.system.SystemManager;
import com.fxtv.framework.system.SystemThirdPartyLogin;
import com.fxtv.framework.system.callback.RequestCallBack;
import com.fxtv.framework.utils.FrameworkUtils;
import com.fxtv.framework.utils.Logger;
import com.google.gson.JsonObject;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2015/12/18.
 * 微信登录组件
 */
public class LoginWechatComponent extends BaseLoginComponent {
    public static final String SHARE_OK = "com.fxtv.framework.system.components.ok";
    public static final String SHARE_CANCLE = "com.fxtv.framework.system.components.cancle";
    public static final String SHARE_FARIL = "com.fxtv.framework.system.components.failer";
    private static final String TAG = "LoginWechatComponent";
    private IWXAPI mApi;
    /**
     * 接收广播
     */
    private BroadcastReceiver mWeChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case SHARE_OK:
                    String code = intent.getExtras().getString("code");
                    if (mCallBack != null) {
                        Logger.d(TAG, "Wechat login onSuccess");
                        if (code != null && !code.equals("")) {
                            Logger.i(TAG, "Code:" + code);
                            getOpenid(code);
                        } else {
                            Logger.i(TAG, "Code is null");
                        }
                    }
                    break;
                case SHARE_CANCLE:
                    if (mCallBack != null) {
                        Logger.d(TAG, "Wechat login onCancel");
                        mCallBack.onCancel();
                    }
                    break;
                case SHARE_FARIL:
                    if (mCallBack != null) {
                        Logger.d(TAG, "Wechat login onFailure");
                        mCallBack.onFailure("登录失败！");
                    }
                    break;
            }
        }
    };

    public LoginWechatComponent(Context context, TLoginModel model, SystemThirdPartyLogin.ICallBackSystemLogin callBack) {
        this.mContext = context;
        this.mTLoginModel = model;
        this.mCallBack = callBack;
        mApi = WXAPIFactory.createWXAPI(context, model.mKey);
        mApi.registerApp(model.mKey);
        initBroadcast();
    }

    /**
     * 注册接收广播
     */
    public void initBroadcast() {
        IntentFilter intenFilter = new IntentFilter();
        intenFilter.addAction(SHARE_OK);
        intenFilter.addAction(SHARE_CANCLE);
        intenFilter.addAction(SHARE_FARIL);
        mContext.registerReceiver(mWeChatReceiver, intenFilter);
    }

    /**
     * 获取微信的openid
     *
     * @param code
     */
    public void getOpenid(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        String httpurl = url.replace("APPID", mTLoginModel.mKey).replace("SECRET", mTLoginModel.mSecret).replace("CODE", code);

        RequestData requestData = new RequestData();
        requestData.setUrl(httpurl);
        requestData.setRequestType(RequestData.REQUEST_TYPE_GET);
        requestData.setWrapperResponse(false);
        requestData.setUseCache(false);
        requestData.setCacheEnable(false);

        SystemManager.getInstance().getSystem(SystemHttp.class).net(mContext, requestData, new RequestCallBack<JsonObject>() {
            @Override
            public void onSuccess(JsonObject data, Response resp) {
                String openid = data.get("openid").getAsString();
                String unionid = data.get("unionid").getAsString();
                if (!TextUtils.isEmpty(openid) && !TextUtils.isEmpty(unionid)) {
                    if (mCallBack != null) {
                        mCallBack.onSuccess(openid, unionid);
                    }
                } else {
                    if (mCallBack != null) {
                        mCallBack.onFailure("unionid or openid is null");
                    }
                }
            }

            @Override
            public void onFailure(Response resp) {
                if (mCallBack != null) {
                    mCallBack.onFailure(resp.mMessage);
                }
            }
        });
    }

    /**
     * 注销接收广播
     */
    public void unRegegister() {
        if (mWeChatReceiver != null && mContext != null) {
            mContext.unregisterReceiver(mWeChatReceiver);
        }
    }

    /**
     * 登录
     */
    @Override
    public void Login() {
        if (!mApi.isWXAppInstalled()) {
            FrameworkUtils.showToast("您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        mApi.sendReq(req);
    }

    @Override
    public void destroy() {
        super.destroy();
        unRegegister();
        if (mContext != null) {
            mContext = null;
        }
        if (mApi != null) {
            mApi = null;
        }
        if (mCallBack != null) {
            mCallBack = null;
        }
    }
}
