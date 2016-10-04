package com.pekingopera.oa.model;

import android.net.Uri;

import com.pekingopera.oa.common.IPager;

/**
 * Created by wayne on 10/3/2016.
 */

public class Flow {
    private int Id;
    private String FlowName;
    private String ModelName;
    private double Amount;
    private String CurrentStepName;
    private int Status;
    private String Creator;
    private String CreateTime;

    public int getId() {
        return Id;
    }

    public String getFlowName() {
        return FlowName;
    }

    public String getModelName() {
        return ModelName;
    }

    public double getAmount() {
        return Amount;
    }

    public String getCurrentStepName() {
        return CurrentStepName;
    }

    public int getStatus() {
        return Status;
    }

    public String getCreator() {
        return Creator;
    }

    public String getCreateTime() {
        return CreateTime;
    }
}