package com.pekingopera.oa.model;

import android.net.Uri;

import com.pekingopera.oa.common.IPager;

import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */

public class Flow {
    private int Id;
    private String FlowName;
    private String FlowNo;
    private String ModelName;
    private double Amount;
    private String CurrentStepName;
    private int Status;
    private String DepName;
    private String Remark;
    private String Creator;
    private String CreateTime;

    private List<FlowStep> Steps;
    private List<FlowDoc> Attachments;
    private int mFlowNo;

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

    public String getDepName() {
        return DepName;
    }

    public String getRemark() {
        return Remark;
    }

    public List<FlowStep> getSteps() {
        return Steps;
    }

    public List<FlowDoc> getAttachments() {
        return Attachments;
    }

    public String getFlowNo() {
        return FlowNo;
    }
}