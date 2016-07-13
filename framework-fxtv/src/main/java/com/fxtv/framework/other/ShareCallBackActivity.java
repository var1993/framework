package com.fxtv.framework.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fxtv.framework.R;
import com.fxtv.framework.component.ShareSinaComponent;
import com.fxtv.framework.system.SystemManager;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.utils.Logger;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * The following unrelated icon files have identical contents: banner_logo.png, logo.png
 * 统一处理的新浪回调页
 * Created by Administrator on 2015/12/22.
 */
public class ShareCallBackActivity extends Activity implements IWeiboHandler.Response {
    private final static String TAG = "ShareCallBackActivity";
    private IWeiboShareAPI mWeiboShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_layout);
        String key = SystemManager.getInstance().getSystem(SystemShare.class).getCurrentComponent().getKey();
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, key);
        mWeiboShareAPI.registerApp();
        mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        Intent intent = new Intent();
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Logger.d(TAG, "SINA SHARE OK");
                intent.setAction(ShareSinaComponent.ACTION_OK);
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Logger.d(TAG, "SINA SHARE ERROR：" + baseResponse.errMsg);
                intent.setAction(ShareSinaComponent.ACTION_FAILE);
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Logger.d(TAG, "SINA SHARE CANCEL：");
                intent.setAction(ShareSinaComponent.ACTION_CANCLE);
                break;
            default:
                intent.setAction(ShareSinaComponent.ACTION_FAILE);
                break;
        }
        sendBroadcast(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWeiboShareAPI != null) {
            mWeiboShareAPI = null;
        }
    }
}
