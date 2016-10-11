package com.pekingopera.oa.model;

/**
 * Created by wayne on 10/11/2016.
 */

public class Version {
    private String updateUrl;
    private int versionCode;
    private long apkSize;
    private String versionName;
    private String updateContent;
    private boolean force;

    public String getUpdateUrl() {
        return updateUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public long getApkSize() {
        return apkSize;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public boolean isForce() {
        return force;
    }
}