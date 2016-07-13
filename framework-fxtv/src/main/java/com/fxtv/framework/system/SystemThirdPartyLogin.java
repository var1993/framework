package com.fxtv.framework.system;

import android.content.Context;
import android.content.Intent;

import com.fxtv.framework.component.BaseLoginComponent;
import com.fxtv.framework.component.LoginQQComponent;
import com.fxtv.framework.component.LoginSinaComponent;
import com.fxtv.framework.component.LoginWechatComponent;
import com.fxtv.framework.frame.BaseSystem;
import com.fxtv.framework.model.TLoginModel;
import com.fxtv.framework.utils.Logger;

/**
 * 第三方登录系统
 *
 * @author FXTV-Android
 */
public class SystemThirdPartyLogin extends BaseSystem {
    private static final String TAG = "SystemThirdPartyLogin";
    private BaseLoginComponent mLoginComponent;

    private ICallBackSystemLogin mLoginCallBack;
    private ICallBackSystemLogin mCurrentCallBack;

    @Override
    protected void init() {
        super.init();
        mLoginCallBack = new ICallBackSystemLogin() {

            @Override
            public void onSuccess(String id, String id2) {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onSuccess(id, id2);
                    onDestroyComponent();
                }

            }

            @Override
            public void onFailure(String msg) {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onFailure(msg);
                    onDestroyComponent();
                }
            }

            @Override
            public void onCancel() {
                if (mCurrentCallBack != null) {
                    mCurrentCallBack.onCancel();
                    onDestroyComponent();
                }
            }
        };
    }

    @Override
    public void destroySystem() {
        super.destroySystem();
        if (mLoginComponent != null) {
            mLoginComponent.destroy();
        }
        if (mLoginCallBack != null) {
            mLoginCallBack = null;
        }
        if (mCurrentCallBack != null) {
            mCurrentCallBack = null;
        }
    }

    /**
     * 使用登录系统的在所在的Activity的onActivityResult调用此方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mLoginComponent != null) {
            mLoginComponent.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 销毁登录系统
     */
    public void onDestroyComponent() {
        if (mLoginComponent != null) {
            mLoginComponent.destroy();
            mLoginComponent = null;
        }
        if (mCurrentCallBack != null) {
            mCurrentCallBack = null;
        }
    }

    /**
     * 调用第三方用户登录的接口
     *
     * @param context
     * @param callBack
     */
    public void thirdLogin(final Context context, final TLoginModel model, final ICallBackSystemLogin callBack) {
        mCurrentCallBack = callBack;
        switch (model.mType) {
            case TLoginModel.TYPE_LOGIN_QQ:
                mLoginComponent = new LoginQQComponent(context, model, mLoginCallBack);
                break;
            case TLoginModel.TYPE_LOGIN_SINA:
                mLoginComponent = new LoginSinaComponent(context, model, mLoginCallBack);
                break;
            case TLoginModel.TYPE_LOGIN_WECHAT:
                mLoginComponent = new LoginWechatComponent(context, model, mLoginCallBack);
                break;
            default:
                Logger.e(TAG, "Error,not find the type=" + model.mType);
                break;
        }
        if (mLoginComponent != null) {
            mLoginComponent.Login();
        }
    }

    public interface ICallBackSystemLogin {
        void onSuccess(String id, String id2);

        void onFailure(String msg);

        void onCancel();
    }
}
