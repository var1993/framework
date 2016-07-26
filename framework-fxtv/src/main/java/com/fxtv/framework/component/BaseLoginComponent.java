package com.fxtv.framework.component;

import android.content.Context;
import android.content.Intent;

import com.fxtv.framework.frame.BaseComponent;
import com.fxtv.framework.model.TLoginModel;
import com.fxtv.framework.system.SystemThirdPartyLogin;

/**
 * Created by Administrator on 2015/12/18.
 */
public abstract class BaseLoginComponent extends BaseComponent {
    protected Context mContext;
    protected TLoginModel mTLoginModel;
    protected SystemThirdPartyLogin.ICallBackSystemLogin mCallBack;

    public abstract void Login();

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    public String getKey() {
        return mTLoginModel.mKey;
    }
}
