package com.fxtv.framework.system;

import com.fxtv.framework.utils.Logger;
import com.fxtv.framework.frame.BaseSystem;
import com.umeng.message.PushAgent;

/**
 * 推送系统
 *
 * @author FXTV-Android
 */
public class SystemPush extends BaseSystem {
    private static final String TAG = "SystemPush";

    @Override
    protected void init() {
        super.init();
        PushAgent.getInstance(mContext).enable();
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    public void activityOnCreate() {
        PushAgent.getInstance(mContext).onAppStart();
    }

    public void openPush() {
        PushAgent.getInstance(mContext).enable();
    }

    public void closePush() {
        PushAgent.getInstance(mContext).disable();
    }

    public void addTag(final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PushAgent.getInstance(mContext).getTagManager().add(tag);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e(TAG, "Error,Add tag,tag=" + tag);
                }
            }
        }).start();
    }

    public void deleteTag(final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PushAgent.getInstance(mContext).getTagManager().delete(tag);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e(TAG, "Error,Delete tag,tag=" + tag);
                }
            }
        }).start();
    }
}
