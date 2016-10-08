package com.pekingopera.oa.model;

import android.net.Uri;

import com.pekingopera.oa.common.IPager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */

public class Gov implements IPager {
    private int Id;
    private String FlowName;
    private String ModelName;
    private String CurrentStepName;
    private int Status;
    private String DepName;
    private String Remark;
    private String CurrentDocPath;
    private String FlowFiles;
    private int CreatorId;
    private String Creator;
    private String CreateTime;
    private String ReviewWords;

    private boolean ApprovalAuthorized;

    private List<FlowStep> Steps;
    private List<FlowDoc> Attachments;

    @Override
    public Uri getUri() {
        return null;
    }

    public int getId() {
        return Id;
    }

    public String getFlowName() {
        return FlowName;
    }

    public String getModelName() {
        return ModelName;
    }

    public String getCurrentStepName() {
        return CurrentStepName;
    }

    public int getStatus() {
        return Status;
    }

    public String getStatusDesc() {
        switch (Status) {
            case 0:
                return "进行中";
            case 1:
                return "已签发";
            case 5:
                return "已归档";
            case -1:
                return "已锁定";
            case -2:
                return "已退回";
            default:
                return "其它";
        }
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

    public int getCreatorId() {
        return CreatorId;
    }

    public boolean isApprovalAuthorized() {
        return ApprovalAuthorized;
    }

    public String getCurrentDocPath() {
        return CurrentDocPath;
    }

    public String getFlowFiles() {
        return FlowFiles;
    }

    public String getReviewWords() {
        return ReviewWords;
    }

    public void setReviewWords(String reviewWords) {
        ReviewWords = reviewWords;
    }
}