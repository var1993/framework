package com.fxtv.framework.system;

import com.fxtv.framework.frame.BaseSystem;
import com.umeng.analytics.MobclickAgent;

/**
 * 崩溃log收集系统
 *
 * @author FXTV-Android
 */
public class SystemCrashLogCollect extends BaseSystem {

    @Override
    protected void init() {
        super.init();
        // umeng
        MobclickAgent.setCatchUncaughtExceptions(SystemManager.getInstance()
                .getSystem(SystemFrameworkConfig.class).mCrashLogNet);
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    public void activityOnCreate() {
    }
}
