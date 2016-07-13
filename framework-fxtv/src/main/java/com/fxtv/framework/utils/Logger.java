package com.fxtv.framework.utils;

import android.util.Log;

import com.fxtv.framework.Profile;

public class Logger {
    private static String TAG_START_WITH;

    static {
        TAG_START_WITH = "fxtv_";
    }

    public static void d(String msg) {
        d("", msg);
    }

    public static void d(String tag, String msg) {
        if (Profile.mConfiguration.isEnableLog()) {
            Log.d(TAG_START_WITH + tag, msg + "\n" + getAutoJumpLogInfos());
        }
    }

    public static void i(String tag, String msg) {
        if (Profile.mConfiguration.isEnableLog()) {
            Log.i(TAG_START_WITH + tag, msg + "\n" + getAutoJumpLogInfos());
        }
    }

    public static void e(String tag, String msg) {
        if (Profile.mConfiguration.isEnableLog()) {
            Log.e(TAG_START_WITH + tag, msg + "\n" + getAutoJumpLogInfos());
        }
    }

    private static String getAutoJumpLogInfos() {
        StackTraceElement[] caller = Thread.currentThread().getStackTrace();
        String info = "";
        /*if(caller!=null){
			for(StackTraceElement element:caller){
				info+=generateTag(element)+"\n";
			}
		}*/
        return info;
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber()); // 替换
        return tag;
    }
}
