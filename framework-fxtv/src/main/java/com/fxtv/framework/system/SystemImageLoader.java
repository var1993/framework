package com.fxtv.framework.system;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fxtv.framework.frame.BaseSystem;

/**
 * 第三方登录系统
 *
 * @author FXTV-Android
 */
public class SystemImageLoader extends BaseSystem {
    private static final String TAG = "SystemImageLoader";

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    public void displayImage(final Context context, final ImageView imageView, final String url, final int holderImage) {
        Glide.with(context).load(url).placeholder(holderImage).error(holderImage).fallback(holderImage).into(imageView);
    }
    public void displayImage(final Activity activity, final ImageView imageView, final String url, final int holderImage) {
        Glide.with(activity).load(url).placeholder(holderImage).error(holderImage).fallback(holderImage).into(imageView);
    }
    public void displayImage(final FragmentActivity fragmentActivity, final ImageView imageView, final String url, final int holderImage) {
        Glide.with(fragmentActivity).load(url).placeholder(holderImage).error(holderImage).fallback(holderImage).into(imageView);
    }

    public void displayImage(final android.app.Fragment fragmentV4, final ImageView imageView, final String url, final int holderImage) {
        Glide.with(fragmentV4).load(url).placeholder(holderImage).error(holderImage).fallback(holderImage).into(imageView);
    }

    public void displayImage(final Fragment fragment, final ImageView imageView, final String url, final int holderImage) {
        Glide.with(fragment).load(url).placeholder(holderImage).error(holderImage).fallback(holderImage).into(imageView);
    }


    public void displayImage(final Context context, final ImageView imageView, final String url, final int holderImage, final int errorImage, final int emptyImage) {
        Glide.with(context).load(url).placeholder(holderImage).error(errorImage).fallback(emptyImage).into(imageView);
    }

    public void displayImage(final Activity activity, final ImageView imageView, final String url, final int holderImage, final int errorImage, final int emptyImage) {
        Glide.with(activity).load(url).placeholder(holderImage).error(errorImage).fallback(emptyImage).into(imageView);
    }

    public void displayImage(final FragmentActivity fragmentActivity, final ImageView imageView, final String url, final int holderImage, final int errorImage, final int emptyImage) {
        Glide.with(fragmentActivity).load(url).placeholder(holderImage).error(errorImage).fallback(emptyImage).into(imageView);
    }

    public void displayImage(final android.app.Fragment fragmentV4, final ImageView imageView, final String url, final int holderImage, final int errorImage, final int emptyImage) {
        Glide.with(fragmentV4).load(url).placeholder(holderImage).error(errorImage).fallback(emptyImage).into(imageView);
    }

    public void displayImage(final Fragment fragment, final ImageView imageView, final String url, final int holderImage, final int errorImage, final int emptyImage) {
        Glide.with(fragment).load(url).placeholder(holderImage).error(errorImage).fallback(emptyImage).into(imageView);
    }

    public String getCachePath() {
        return Glide.getPhotoCacheDir(mContext).getPath();
    }
}
