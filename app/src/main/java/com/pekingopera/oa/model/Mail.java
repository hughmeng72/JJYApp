package com.pekingopera.oa.model;

import android.net.Uri;

import com.pekingopera.oa.common.IPager;

/**
 * Created by wayne on 10/3/2016.
 */

public class Mail implements IPager {
    private int Id;
    private String Subject;
    private String Creator;
    private String SendTime;
    private String Url;

    @Override
    public Uri getUri() {
        return Uri.parse(Url);
    }

    @Override
    public int getId() {
        return Id;
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
