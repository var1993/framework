package com.fxtv.framework.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.utils.FrameworkUtils;
import com.fxtv.framework.utils.Logger;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.net.MalformedURLException;

/**
 * Created by Administrator on 2015/12/14.
 */
public class ShareWeChatComponent extends BaseShareComponent {
    public static final String SHARE_OK = "com.fxtv.framework.system.components.ok";
    public static final String SHARE_CANCLE = "com.fxtv.framework.system.components.cancle";
    public static final String SHARE_FARIL = "com.fxtv.framework.system.components.failer";
    private static final int THUMB_SIZE = 150;
    private String TAG = "ShareWeChatComponent";
    private IWXAPI mApi;
    android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            shareWeChat(mShareModel, bitmap);
        }
    };
    private BroadcastReceiver mWeChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case SHARE_OK:
                    if (mCallBack != null) {
                        Logger.d(TAG, "Wechat share onSuccess");
                        mCallBack.onSuccess(mShareModel.type);
                    }
                    break;
                case SHARE_CANCLE:
                    if (mCallBack != null) {
                        Logger.d(TAG, "Wechat share onCancel");
                        mCallBack.onCancel(mShareModel.type);
                    }
                    break;
                case SHARE_FARIL:
                    if (mCallBack != null) {
                        Logger.d(TAG, "Wechat share onFailure");
                        mCallBack.onFailure(mShareModel.type, "分享失败！");
                    }
                    break;
            }


        }
    };

    public ShareWeChatComponent(Context context, ShareModel model, SystemShare.ICallBackSystemShare callBack) {
        this.mContext = context;
        this.mShareModel = model;
        this.mCallBack = callBack;

        mApi = WXAPIFactory.createWXAPI(context, model.key);
        mApi.registerApp(model.key);

        initBroadcast();
    }

    public void initBroadcast() {
        IntentFilter intenFilter = new IntentFilter();
        intenFilter.addAction(SHARE_OK);
        intenFilter.addAction(SHARE_CANCLE);
        intenFilter.addAction(SHARE_FARIL);
        mContext.registerReceiver(mWeChatReceiver, intenFilter);
    }

    public void shareWeChat(final ShareModel model, final Bitmap bitmap) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = model.shareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = model.shareTitle;
        msg.description = model.shareSummary;
        if (bitmap != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            bitmap.recycle();
            msg.setThumbImage(thumbBmp);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        mApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    @Override
    public void share() {
        if (!mApi.isWXAppInstalled()) {
            FrameworkUtils.showToast("您还未安装微信客户端");
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(FrameworkUtils.getImageByte(mShareModel.fileImageUrl));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    msg.obj = bitmap;
                }
                mHandler.handleMessage(msg);
            }
        }.start();
    }

    public void unRegegister() {
        if (mWeChatReceiver != null && mContext != null) {
            mContext.unregisterReceiver(mWeChatReceiver);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        unRegegister();
        if (mShareModel != null) {
            mShareModel = null;
        }
        if (mCallBack != null) {
            mCallBack = null;
        }
        if (mApi != null) {
            mApi = null;
        }

    }

    @Override
    public String getKey() {
        return mShareModel != null ? mShareModel.key : "";
    }
}
