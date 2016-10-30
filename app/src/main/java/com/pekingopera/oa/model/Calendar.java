package com.pekingopera.oa.model;

import android.net.Uri;

import com.pekingopera.oa.common.IPager;

import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */

public class Calendar implements IPager {
    private int Id;
    private String Title;
    private String DepName;
    private String CreateTime;
    private String Url;
    private List<FlowDoc> Attachments;

    @Override
    public int getId() {
        return Id;
    }

    @Override
    public Uri getUri() {
        return Uri.parse(Url);
    }

    public String getTitle() {
        return Title;
    }

    public String getDepName() {
        return DepName;
    }

    public List<FlowDoc> getAttachments() {
        return Attachments;
    }

    public String getCreateTime() {
        return CreateTime;
    }
}
