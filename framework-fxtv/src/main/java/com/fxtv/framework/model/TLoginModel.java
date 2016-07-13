package com.fxtv.framework.model;

/**
 * Created by var on 16/7/12.
 * Note:
 */
public class TLoginModel {

    public static final int TYPE_LOGIN_QQ = 0;
    public static final int TYPE_LOGIN_WECHAT = 1;
    public static final int TYPE_LOGIN_SINA = 2;

    public int mType;

    public String mKey;
    public String mSecret;
}
