package com.pekingopera.oa.model;

import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */

public class Gov {
    private int Id;
    private String FlowName;
    private String ModelName;
    private String CurrentStepName;
    private int Status;
    private String DepName;
    private String Remark;
    private int CreatorId;
    private String Creator;
    private String CreateTime;

    private boolean ApprovalAuthorized;

    private List<FlowStep> Steps;
    private List<FlowDoc> Attachments;

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
                return "已完成";
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
}