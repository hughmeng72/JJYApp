package com.pekingopera.oa.model;

import android.net.Uri;

import com.pekingopera.oa.common.IPager;

/**
 * Created by wayne on 10/3/2016.
 */

public class Calendar implements IPager {
    private int Id;
    private String Title;
    private String DepName;
    private String CreateTime;
    private String Url;

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

    public String getCreateTime() {
        return CreateTime;
    }
}
