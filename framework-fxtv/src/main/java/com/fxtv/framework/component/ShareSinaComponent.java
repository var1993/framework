package com.fxtv.framework.component;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.fxtv.framework.R;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.utils.FrameworkUtils;
import com.fxtv.framework.utils.Logger;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;

import java.net.MalformedURLException;

/**
 * Created by Administrator on 2015/12/14.
 */
public class ShareSinaComponent extends BaseShareComponent {
    public static final String ACTION_OK = "com.fxtv.framework.component.ShareSinaComponent.ok";
    public static final String ACTION_CANCLE = "com.fxtv.framework.component.ShareSinaComponent.cancle";
    public static final String ACTION_FAILE = "com.fxtv.framework.component.ShareSinaComponent.faile";
    private static final int THUMB_SIZE = 150;
    private String TAG = "ShareSinaComponent";
    private IWeiboShareAPI mWeiboShareAPI;
    android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            Logger.d(TAG, "bitmap:" + bitmap);
            shareSinaWeBo(mShareModel, bitmap);
        }
    };
    private BroadcastReceiver mSinaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_OK:
                    if (mCallBack != null) {
                        mCallBack.onSuccess(mShareModel.type);
                    }
                    break;
                case ACTION_CANCLE:
                    if (mCallBack != null) {
                        mCallBack.onCancel(mShareModel.type);
                    }
                    break;
                case ACTION_FAILE:
                    if (mCallBack != null) {
                        mCallBack.onFailure(mShareModel.type, "分享失败！");
                    }
                    break;
            }
        }
    };

    public ShareSinaComponent(Context context, ShareModel model, SystemShare.ICallBackSystemShare callBack) {
        this.mContext = context;
        this.mShareModel = model;
        this.mCallBack = callBack;
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, model.key);
        mWeiboShareAPI.registerApp();
        initBroadCast();
    }

    private void initBroadCast() {
        IntentFilter intenFilter = new IntentFilter();
        intenFilter.addAction(ACTION_OK);
        intenFilter.addAction(ACTION_CANCLE);
        intenFilter.addAction(ACTION_FAILE);
        mContext.registerReceiver(mSinaReceiver, intenFilter);
    }

    public void shareSinaWeBo(ShareModel model, Bitmap bitmap) {
        if (!mWeiboShareAPI.isWeiboAppSupportAPI()) {
            FrameworkUtils.showToast("请先安装新浪客户端");
            return;
        }
        Logger.d(TAG, "开始分享新浪");
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        // weiboMessage.textObject=getTextObj(model.shareTitle, model.shareUrl);

        if (bitmap != null) {
            weiboMessage.imageObject = getImageObj(bitmap);
        }
        weiboMessage.mediaObject = getWebpageObj(model.shareTitle, model.shareSummary, model.shareUrl, bitmap);
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest((Activity) mContext, request);
    }

    /**
     * 分享网页内容
     *
     * @param title
     * @param summary
     * @param targUrl
     * @param bitmap
     * @return
     */
    private WebpageObject getWebpageObj(String title, String summary, String targUrl, Bitmap bitmap) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = summary;
        if (bitmap != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            bitmap.recycle();
            mediaObject.setThumbImage(thumbBmp);
        } else {
            Bitmap thumbBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
            mediaObject.setThumbImage(thumbBmp);
        }
        mediaObject.actionUrl = targUrl;
        mediaObject.defaultText = "飞熊视频";
        return mediaObject;
    }

    /**
     * 分享文本内容
     *
     * @param summary
     * @param targUrl
     * @return
     */
    private TextObject getTextObj(final String summary, final String targUrl) {
        TextObject textObject = new TextObject();
        textObject.text = summary + targUrl;
        return textObject;
    }

    /**
     * 分享图片内容
     *
     * @param bitmap
     * @return
     */
    private ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    @Override
    public void share() {
        if (!mWeiboShareAPI.isWeiboAppSupportAPI()) {
            FrameworkUtils.showToast("请先安装新浪客户端");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                Bitmap bitmap = null;
                if (mShareModel.fileImageUrl != null && !mShareModel.fileImageUrl.equals("")) {
                    try {
                        bitmap = BitmapFactory.decodeStream(FrameworkUtils.getImageByte(mShareModel.fileImageUrl));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        msg.obj = bitmap;
                    }
                }

                mHandler.handleMessage(msg);
            }
        }.start();
    }

    @Override
    public String getKey() {
        return mShareModel != null ? mShareModel.key : "";
    }

    private void unRegisterReceiVer() {
        if (mContext != null) {
            mContext.unregisterReceiver(mSinaReceiver);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mContext = null;
        if (mWeiboShareAPI != null) {
            mWeiboShareAPI = null;
        }
        if (mShareModel != null) {
            mShareModel = null;
        }
        if (mCallBack != null) {
            mCallBack = null;
        }
        if (mSinaReceiver != null) {
            mSinaReceiver = null;
        }

        unRegisterReceiVer();
    }
}
