package com.pekingopera.oa.model;

import android.net.Uri;

import com.pekingopera.oa.common.IPager;

import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */

public class Mail implements IPager {
    private int Id;
    private String Subject;
    private String Creator;
    private String SendTime;
    private String Url;
    private List<FlowDoc> Attachments = null;

    @Override
    public Uri getUri() {
        return Uri.parse(Url);
    }

    @Override
    public int getId() {
        return Id;
    }

    @Override
    public String getTitle() {
        return Subject;
    }

    @Override
    public List<FlowDoc> getAttachments() {
        return Attachments;
    }

    public String getSubject() {
        return Subject;
    }

    public String getCreator() {
        return Creator;
    }

    public String getSendTime() {
        return SendTime;
    }
}
