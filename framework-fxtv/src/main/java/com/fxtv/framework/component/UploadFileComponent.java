package com.fxtv.framework.component;

import android.graphics.Bitmap;

import com.fxtv.framework.frame.BaseComponent;
import com.fxtv.framework.other.OkHttpUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 上传组件
 *
 * @author FXTV-Android
 */
public class UploadFileComponent extends BaseComponent {
    private static final String TAG = "UploadFileComponent";
    private android.os.Handler mHandler;

    public UploadFileComponent() {
        mHandler = new android.os.Handler();
    }

    public void uploadFile(final String url, final String filePath, final IUploadCallBack callBack) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            if (callBack != null) {
                callBack.onFailure("文件地址错误", "");
            }
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("file", file);

        uploadFile(url, params, callBack);
    }

    public void uploadFile(final String url, final File file, final IUploadCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("file", file);

        uploadFile(url, params, callBack);
    }

    public void uploadFile(final String url, final Bitmap bitmap, final IUploadCallBack callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("file", bitmap);

        uploadFile(url, params, callBack);
    }

    public void uploadFile(final String url, final Map<String, Object> params, final IUploadCallBack callBack) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (params != null) {
            int filePos = 0;
            for (String key : params.keySet()) {
                Object o = params.get(key);
                if (o instanceof File) {
                    File file = (File) o;
                    builder.addFormDataPart("file" + filePos, file.getName(), RequestBody.create(null, file));
                    filePos++;
                } else if (o instanceof Bitmap) {
                    byte[] temp = bitmapToByteArray((Bitmap) o);
                    String fileName = String.valueOf(System.currentTimeMillis()) + ".png";
                    builder.addFormDataPart("file" + filePos, fileName, RequestBody.create(null, temp));
                    filePos++;
                } else {
                    builder.addFormDataPart(key, o.toString());
                }
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        Call call = OkHttpUtils.getInstance().mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callBack != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure("网络错误", e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                if (response.isSuccessful()) {
                    if (callBack != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(string);
                            }
                        });
                    }
                } else {
                    if (callBack != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure("未知错误", string);
                            }
                        });
                    }
                }
            }
        });
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public interface IUploadCallBack {
        void onSuccess(String response);

        void onFailure(String msg, String response);
    }
}
