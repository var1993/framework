package com.fxtv.framework.system;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.fxtv.framework.Profile;
import com.fxtv.framework.frame.BaseSystem;
import com.fxtv.framework.utils.FrameworkUtils;
import com.fxtv.framework.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 程序错误处理系统
 *
 * @author FXTV-Android
 */
public class SystemCrash extends BaseSystem {
    private static final String TAG = "SystemCrash";
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private Map<String, String> mBaseInfo;

    @Override
    protected void init() {
        super.init();
        mBaseInfo = getBaseInfo(mContext);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                if (Profile.mConfiguration.isEnableCrashLogFile()) {
                    Logger.d("zsh","run here");
                    saveInfoToSD(ex);
                }

                if (Profile.mConfiguration.isEnableCrashReset()) {
                    resetApp(500);
                } else {
                    mDefaultHandler.uncaughtException(thread, ex);
                    Logger.d("zsh","run here3");
                }
            }
        });
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (mDefaultHandler != null) {
            mDefaultHandler = null;
        }
    }

    private void resetApp(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
            FrameworkUtils.restartApplication(mContext, mContext.getPackageName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取系统未捕捉的错误信息
     *
     * @param throwable
     * @return
     */
    private String getExceptionInfo(Throwable throwable) {
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        throwable.printStackTrace(mPrintWriter);
        mPrintWriter.close();

        return mStringWriter.toString();
    }

    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     *
     * @param context
     * @return
     */
    private HashMap<String, String> getBaseInfo(Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            map.put("versionName", mPackageInfo.versionName);
            map.put("versionCode", "" + mPackageInfo.versionCode);

            map.put("MODEL", "" + Build.MODEL);
            map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
            map.put("PRODUCT", "" + Build.PRODUCT);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
     *
     * @param ex
     * @return
     */
    private String saveInfoToSD(Throwable ex) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : mBaseInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }
        sb.append(getExceptionInfo(ex));

        File dir = new File(Profile.mConfiguration.getLogFilePath() + "/crash");
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            fileName = dir.toString() + File.separator + paserTime(System.currentTimeMillis())
                    + ".log";
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("zsh","run hrer2");
        }

        return sb.toString();
    }

    /**
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
     *
     * @param milliseconds
     * @return
     */
    private String paserTime(long milliseconds) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String times = format.format(new Date(milliseconds));

        return times;
    }
}
