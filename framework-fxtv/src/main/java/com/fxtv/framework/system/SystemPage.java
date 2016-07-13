package com.fxtv.framework.system;

import android.app.Activity;

import com.fxtv.framework.utils.Logger;
import com.fxtv.framework.frame.BaseSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * 界面管理系统
 *
 * @author FXTV-Android
 */
public class SystemPage extends BaseSystem {
    private static final String TAG = "SystemPage";

    private List<Activity> mList = new ArrayList<Activity>();
    private Activity mCurrActivity;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void destroy() {
        super.destroy();
        finishAllActivity();
        mCurrActivity = null;
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
            Logger.d(TAG, "addActivity name=" + activity.getClass().getSimpleName());
            mList.add(activity);
            mCurrActivity = activity;
        }
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            Logger.d(TAG, "finishActivity name=" + activity.getClass().getSimpleName());
            mList.remove(activity);
            if(mList!=null && mList.size()>0){
                mCurrActivity=mList.get(mList.size()-1);//定位到最后一个存活的act
            }else{
                mCurrActivity=null;
            }
            activity.finish();
        }
    }
    public void finishActivityNotRemove(Activity activity) {
        if (activity != null) {
            Logger.d(TAG, "finishActivity name=" + activity.getClass().getSimpleName());
            activity.finish();
        }
    }

    public void finishAllActivity() {
        if(mList==null || mList.size()<1) return;
        for (Activity activity : mList) {
            if(activity!=null && !activity.isFinishing()){
                activity.finish();
            }
        }
        mList.clear();
        mCurrActivity=null;
    }

    public Activity getCurrActivity() {
        return mCurrActivity;
    }

    public List<Activity> getListActivity(){
        return mList;
    }


}
