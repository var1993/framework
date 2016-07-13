package com.fxtv.framework.component.downloadfile;

import java.io.File;

/**
 * Created by Var on 16/4/18.
 */
public interface IDownloadFileCallback {
    void onError(int error_type);

    void onProgress(float progress, long downloadPos, long total);

    void onSpeed(int speed);

    void onSuccess(File file);

    void onComplete();
}
