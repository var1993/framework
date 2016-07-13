package com.fxtv.framework.component.downloadfile;

/**
 * Created by Var on 16/4/18.
 */
public class DownloadEntity {
    public String url;
    public String savePath;
    public long startPos;

    public DownloadEntity(String url, String savePath, long startPos) {
        this.url = url;
        this.savePath = savePath;
        this.startPos = startPos;
    }

    @Override
    public String toString() {
        return "DownloadEntity:url=" + url + ",savePath=" + savePath + ",startPos=" + startPos;
    }
}
