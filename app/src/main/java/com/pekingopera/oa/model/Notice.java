package com.pekingopera.oa.model;

import android.net.Uri;

import com.pekingopera.oa.common.IPager;

import java.util.List;

/**
 * Created by wayne on 10/2/2016.
 */

public class Notice implements IPager {
    private int id;
    private String TypeName;
    private String Title;
    private String FilePath;
    private String Notes;
    private String Creator;
    private String AddTime;
    private String url;
    private List<FlowDoc> Attachments;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public List<FlowDoc> getAttachments() {
        return Attachments;
    }

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

    @Override
    public Uri getUri() {
        return Uri.parse(url);
    }

}