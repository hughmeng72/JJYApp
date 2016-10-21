package com.pekingopera.oa.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wayne on 10/17/2016.
 */

public class Employee implements Serializable {
    @SerializedName("Id")
    private int mId;
    private String RealName;
    private String DepName;

    private boolean selected = false;

    public int getId() {
        return mId;
    }

    public String getRealName() {
        return RealName;
    }

    public String getDepName() {
        return DepName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}