package com.fxtv.framework.model;

/**
 * Created by wzh on 2015/12/31.
 */
public class HttpCode {
    //全局http请求code
    public static final int
            SUECCSS = 2000,//数据获取成功,
            DATA_ERROR = 5036,//数据解析错误

            NO_NETWORK = 4004,//无连接

            statusCode_0 = 0,//连接超时
            VIDEO_URI_ERROR = 707,//无法获取视频地址
            statusCode_400 = 400,
            statusCode_401 = 401,
            statusCode_403 = 403,
            statusCode_404 = 404,
            statusCode_408 = 408,
            statusCode_410 = 410,
            statusCode_413 = 413,
            statusCode_414 = 414,
            statusCode_415 = 415,

            statusCode_500 = 500,
            statusCode_501 = 501,
            statusCode_502 = 502,
            statusCode_503 = 503,
            statusCode_504 = 504,
            statusCode_505 = 505;

}
