package com.fxtv.framework.component.downloadfile;

import com.fxtv.framework.frame.BaseComponent;
import com.fxtv.framework.other.OkHttpUtils;
import com.fxtv.framework.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载组件
 *
 * @author FXTV-Android
 */
public class ComponentDownloadFile extends BaseComponent {
    private static final String TAG = "ComponentDownloadFile";

    public static final int DOWNLOAD_STATUS_IDLE = -1;
    public static final int DOWNLOAD_STATUS_ING = 0;
    public static final int DOWNLOAD_STATUS_PAUSE = 1;
    public static final int DOWNLOAD_STATUS_ERROR = 2;
    public static final int DOWNLOAD_STATUS_CANCELED = 3;

    public static final int ERROR_TYPE_UNKNOWN = 100;
    public static final int ERROR_TYPE_NETWORK_ERROR = 101;
    public static final int ERROR_TYPE_URL_INVALID = 103;

    private DownloadEntity downloadEntity;

    private long downloadPos;
    private long totalSize;

    private int currentStatus = DOWNLOAD_STATUS_IDLE;

    private File file;

    private DownloadFileCallback callback;

    public ComponentDownloadFile(DownloadEntity entity, DownloadFileCallback callback) {
        if (entity != null) {
            downloadEntity = entity;
            downloadPos = entity.startPos;
        }
        this.callback = callback;
    }

    @Override
    public void destroy() {
        super.destroy();
        callback = null;
    }

    public void updateDownloadEntity(DownloadEntity entity) {
        Logger.d(TAG, "setData," + entity.toString());
        if (entity != null) {
            downloadEntity = entity;
            this.downloadPos = entity.startPos;
        }
    }

    public void download() {
        Logger.d(TAG, "download");
        if (downloadEntity != null
                && currentStatus != DOWNLOAD_STATUS_ING
                && currentStatus != DOWNLOAD_STATUS_CANCELED) {
            down();
        }
    }

    public void pause() {
        Logger.d(TAG, "pause");
        if (currentStatus == DOWNLOAD_STATUS_ING) {
            currentStatus = DOWNLOAD_STATUS_PAUSE;
        }
    }

    public void cancel() {
        if (currentStatus != DOWNLOAD_STATUS_IDLE) {
            if (currentStatus == DOWNLOAD_STATUS_ING) {
                currentStatus = DOWNLOAD_STATUS_CANCELED;
            } else if (currentStatus == DOWNLOAD_STATUS_PAUSE) {
                file.delete();
                reset();
            }
        }
    }

    public void reset() {
        if (currentStatus == DOWNLOAD_STATUS_ING) {
            cancel();
        } else {
            downloadEntity = null;
            currentStatus = DOWNLOAD_STATUS_IDLE;
            downloadPos = 0;
            totalSize = 0;
            file = null;
        }
    }

    public int getStatus() {
        return currentStatus;
    }

    private void down() {
        Logger.d(TAG, "down");
        currentStatus = DOWNLOAD_STATUS_ING;

        Request request;
        if (downloadPos != 0) {
            String property = "bytes=" + downloadPos + "-";
            request = new Request.Builder().url(downloadEntity.url).addHeader("RANGE", property).build();
        } else {
            request = new Request.Builder().url(downloadEntity.url).build();
        }

        OkHttpUtils.getInstance().mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "onFailure,e=" + e.getMessage());
                e.printStackTrace();
                handleError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Logger.d(TAG, "onResponse");
                if (currentStatus != DOWNLOAD_STATUS_ING)
                    return;

                if (response.isSuccessful()) {
                    Logger.d(TAG, "onResponse,isSuccessful");

                    file = new File(downloadEntity.savePath);
                    if (file.exists() && downloadPos == 0) {
                        file.delete();
                        file = new File(downloadEntity.savePath);
                    }

                    InputStream inputStream = null;
                    RandomAccessFile randomAccessFile = null;
                    try {
                        inputStream = response.body().byteStream();
                        randomAccessFile = new RandomAccessFile(file, "rw");
                        randomAccessFile.seek(downloadPos);

                        totalSize = response.body().contentLength() + downloadPos;
                        Logger.d(TAG, "total=" + totalSize);

                        byte buffer[] = new byte[1024];
                        int len = -1;
                        long size = 0;

                        long temp = System.currentTimeMillis();
                        while ((len = inputStream.read(buffer)) != -1
                                && currentStatus == DOWNLOAD_STATUS_ING
                                && currentStatus != DOWNLOAD_STATUS_PAUSE
                                && currentStatus != DOWNLOAD_STATUS_CANCELED) {
                            randomAccessFile.write(buffer, 0, len);

                            size += len;
                            downloadPos += len;
                            if (callback != null
                                    && (System.currentTimeMillis() - temp) >= 1000
                                    && currentStatus == DOWNLOAD_STATUS_ING
                                    && currentStatus != DOWNLOAD_STATUS_PAUSE
                                    && currentStatus != DOWNLOAD_STATUS_CANCELED) {
                                callback.onProgress(((float) downloadPos / (float) totalSize) * 100f, downloadPos, totalSize);
                                callback.onSpeed((int) size / 1024);
                                size = 0;
                                temp = System.currentTimeMillis();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Logger.e(TAG, "down,error=" + e.getMessage());
                        handleError(e);
                    } finally {
                        if (randomAccessFile != null) {
                            randomAccessFile.close();
                        }

                        if (inputStream != null) {
                            inputStream.close();
                        }

                        if (currentStatus == DOWNLOAD_STATUS_ING) {
                            if (callback != null) {
                                callback.onSuccess(file);
                                callback.onComplete();
                            }

                            currentStatus = DOWNLOAD_STATUS_IDLE;
                            reset();
                        } else if (currentStatus == DOWNLOAD_STATUS_CANCELED) {
                            file.delete();
                            currentStatus = DOWNLOAD_STATUS_IDLE;
                            reset();
                        }
                    }
                } else {
                    Logger.d(TAG, "onResponse,is not Successful,code=" + response.code());
                    handleError(response.code());
                }
            }
        });
    }

    private void handleError(Exception e) {
        currentStatus = DOWNLOAD_STATUS_ERROR;

        int error_type;
        Class<? extends Exception> aClass = e.getClass();
        Logger.e(TAG, "handleError,error class =" + aClass.getSimpleName());

        if (aClass.isInstance(ConnectException.class) || aClass.isInstance(SocketTimeoutException.class)) {
            error_type = ERROR_TYPE_NETWORK_ERROR;
        } else if (aClass.isInstance(SocketException.class)) {
            error_type = ERROR_TYPE_UNKNOWN;
        } else {
            error_type = ERROR_TYPE_UNKNOWN;
        }

        if (callback != null) {
            callback.onError(error_type);
        }
    }

    private void handleError(int code) {
        currentStatus = DOWNLOAD_STATUS_ERROR;
        if (callback != null) {
            callback.onError(ERROR_TYPE_URL_INVALID);
        }
    }
}
