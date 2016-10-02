package com.pekingopera.oa.model;

import android.net.Uri;

/**
 * Created by wayne on 10/2/2016.
 */

public class Notice {
    private String TypeName;
    private String Title;
    private String FilePath;
    private String Notes;
    private String Creator;
    private String AddTime;
    private String url;

    public String getTypeName() {
        return TypeName;
    }

    public String getTitle() {
        return Title;
    }

    public String getFilePath() {
        return FilePath;
    }

    public String getNotes() {
        return Notes;
    }

    public String getCreator() {
        return Creator;
    }

    public String getAddTime() {
        return AddTime;
    }

    public Uri getUri() {
        return Uri.parse(url);
    }
}