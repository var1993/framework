package com.fxtv.framework.system;


import android.content.Context;
import android.content.Intent;

import com.fxtv.framework.component.BaseShareComponent;
import com.fxtv.framework.component.ShareQQComponent;
import com.fxtv.framework.component.ShareQzonComponent;
import com.fxtv.framework.component.ShareSinaComponent;
import com.fxtv.framework.component.ShareWeChatCircleComponent;
import com.fxtv.framework.component.ShareWeChatComponent;
import com.fxtv.framework.frame.BaseSystem;
import com.fxtv.framework.model.ShareModel;


/**
 * 分享系统
 *
 * @author FXTV-Android
 */
public class SystemShare extends BaseSystem {
    private final static String TAG = "SystemShare";
    private BaseShareComponent mCurrentComponent;
    private ICallBackSystemShare mCurrentCallBack;
    private ICallBackSystemShare mShareCallBack;

    @Override
    protected void init() {
        super.init();
        mShareCallBack = new ICallBackSystemShare() {
            @Override
            public void onSuccess(int shareType) {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onSuccess(shareType);
                    destroyComponent();
                }
            }

            @Override
            public void onFailure(int shareType, String msg) {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onFailure(shareType, msg);
                    destroyComponent();
                }
            }

            @Override
            public void onCancel(int shareType) {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onCancel(shareType);
                    destroyComponent();
                }
            }
        };
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mShareCallBack != null) {
            mShareCallBack = null;
        }

        if (mCurrentComponent != null) {
            mCurrentComponent.destroy();
        }

        if (mShareCallBack != null) {
            mShareCallBack = null;
        }
    }

    public BaseShareComponent getCurrentComponent() {
        return mCurrentComponent;
    }

    /**
     * 分享
     *
     * @param context  上下文
     * @param model    分享实体
     * @param callBack 分享回调
     */
    public void share(final Context context, final ShareModel model, final ICallBackSystemShare callBack) {
        mCurrentCallBack = callBack;
        switch (model.type) {
            case ShareModel.SHARE_TYPE_QQ:
                shareQQ(context, model);
                break;
            case ShareModel.SHARE_TYPE_QQ_QZONE:
                shareQZone(context, model);
                break;
            case ShareModel.SHARE_TYPE_SINA:
                shareSina(context, model);
                break;
            case ShareModel.SHARE_TYPE_WECHAT:
                shareWeChat(mContext, model);
                break;
            case ShareModel.SHARE_TYPE_WECHAT_CIRCLE:
                shareWeChatCircle(mContext, model);
                break;
            default:
                if (callBack != null) {
                    callBack.onFailure(model.type, "不支持此类型分享");
                }
                break;
        }
    }

    /**
     * 调用分享的activity中 必须调用此函数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCurrentComponent != null) {
            mCurrentComponent.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * QQ分享
     *
     * @param context
     * @param model
     */
    private void shareQQ(Context context, final ShareModel model) {
        mCurrentComponent = new ShareQQComponent(context, model, mShareCallBack);
        mCurrentComponent.share();
    }

    /**
     * QQ空间分享
     *
     * @param context
     * @param model
     */
    private void shareQZone(Context context, final ShareModel model) {
        mCurrentComponent = new ShareQzonComponent(context, model, mShareCallBack);
        mCurrentComponent.share();
    }

    /**
     * 新浪分享
     *
     * @param context
     * @param model
     */
    private void shareSina(Context context, final ShareModel model) {
        mCurrentComponent = new ShareSinaComponent(context, model, mShareCallBack);
        mCurrentComponent.share();
    }

    /**
     * 微信分享
     *
     * @param context
     * @param model
     */
    private void shareWeChat(Context context, final ShareModel model) {
        mCurrentComponent = new ShareWeChatComponent(context, model, mShareCallBack);
        mCurrentComponent.share();
    }

    /**
     * 微信朋友圈分享
     *
     * @param context
     * @param model
     */
    private void shareWeChatCircle(Context context, final ShareModel model) {
        mCurrentComponent = new ShareWeChatCircleComponent(context, model, mShareCallBack);
        mCurrentComponent.share();
    }

    /**
     * 分享控件销毁
     */
    private void destroyComponent() {
        if (mCurrentComponent != null) {
            mCurrentComponent.destroy();
            mCurrentComponent = null;
        }
        mCurrentCallBack = null;
    }


    public interface ICallBackSystemShare {
        /**
         * 分享类型,qq,wechat。。。
         *
         * @param shareType SystemShare.SHARE_TYPE_QQ = 0;...
         */
        void onSuccess(int shareType);

        void onFailure(int shareType, String msg);

        void onCancel(int shareType);
    }

}
