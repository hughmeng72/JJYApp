package com.pekingopera.oa.common;

import android.net.Uri;

import com.pekingopera.oa.model.FlowDoc;

import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */
public interface IPager {
    Uri getUri();
    int getId();
    String getTitle();

    List<FlowDoc> getAttachments();
}
