package com.fxtv.framework.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;
import com.fxtv.framework.utils.Logger;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 关于分享功能
 *
 * @author Android2
 */
public class ShareQQComponent extends BaseShareComponent {
    private static final String TAG = "ShareQQComponent";
    private Tencent mTencent;
    private IUiListener mIqListener;

    public ShareQQComponent(Context context, ShareModel model, SystemShare.ICallBackSystemShare callBack) {
        this.mContext = context;
        this.mShareModel = model;
        this.mCallBack = callBack;
        mTencent = Tencent.createInstance(model.key, mContext);
        mIqListener = new IqListener();
    }

    @Override
    public void destroy() {
        super.destroy();
        mContext = null;
        if (mTencent != null) {
            mTencent.releaseResource();
            mTencent = null;
        }
        if (mCallBack != null) {
            mCallBack = null;
        }
        if (mIqListener != null) {
            mIqListener = null;
        }
    }

    /**
     * 实现QQ分享
     */
    @Override
    public void share() {
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareModel.shareTitle);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareModel.shareSummary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareModel.shareUrl);
        if (mShareModel.fileImageUrl != null && !mShareModel.fileImageUrl.equals("")) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareModel.fileImageUrl);
        }
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "飞熊视频");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);

        mTencent.shareToQQ((Activity) mContext, params, mIqListener);
    }

    @Override
    public String getKey() {
        return mShareModel != null ? mShareModel.key : "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, intent, mIqListener);
        }
    }

    /**
     * 分享回调接口
     */
    private class IqListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            if (mCallBack != null) {
                Logger.d(TAG, "QQ  SHARE SUCCESS");
                mCallBack.onSuccess(mShareModel.type);
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (mCallBack != null) {
                Logger.d(TAG, "QQ SHARE ERROR:" + uiError.errorMessage);
                mCallBack.onFailure(mShareModel.type, uiError.errorMessage);
            }
        }

        @Override
        public void onCancel() {
            if (mCallBack != null) {
                Logger.d(TAG, "QQ SHARE CANCLE");
                mCallBack.onCancel(mShareModel.type);
            }
        }
    }
}
