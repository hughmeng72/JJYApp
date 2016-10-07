package com.pekingopera.oa.model;

import android.net.Uri;
import android.widget.Switch;

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
    private int CreatorId;
    private String Creator;
    private String CreateTime;

    private boolean BudgetInvolved;
    private String ProjectName;
    private String ItemName;
    private double TotalAmount;
    private double AmountPaidProcurement;
    private double AmountToBePaidProcurement;
    private double AmountPaidReimbursement;
    private double AmountToBePaidReimbursement;
    private double AmountLeft;

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

    public String getStatusDesc() {
        switch (Status) {
            case 0:
                return "审批中";
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

    public String getFlowNo() {
        return FlowNo;
    }

    public boolean isBudgetInvolved() {
        return BudgetInvolved;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public String getItemName() {
        return ItemName;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public double getAmountPaidProcurement() {
        return AmountPaidProcurement;
    }

    public double getAmountToBePaidProcurement() {
        return AmountToBePaidProcurement;
    }

    public double getAmountPaidReimbursement() {
        return AmountPaidReimbursement;
    }

    public double getAmountToBePaidReimbursement() {
        return AmountToBePaidReimbursement;
    }

    public double getAmountLeft() {
        return AmountLeft;
    }

    public int getCreatorId() {
        return CreatorId;
    }
}