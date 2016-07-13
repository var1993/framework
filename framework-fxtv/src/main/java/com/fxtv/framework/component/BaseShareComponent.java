package com.fxtv.framework.component;

import android.content.Context;
import android.content.Intent;

import com.fxtv.framework.frame.BaseComponent;
import com.fxtv.framework.model.ShareModel;
import com.fxtv.framework.system.SystemShare;

/**
 * Created by Administrator on 2015/12/14.
 */
public abstract class BaseShareComponent extends BaseComponent {
    protected Context mContext;
    protected ShareModel mShareModel;
    protected SystemShare.ICallBackSystemShare mCallBack;

    public abstract void share();

    public abstract String getKey();

    public void onNewIntent(Intent intent){}

    public void onActivityResult(int requestCode,int resultCode,Intent intent){

    }
}
