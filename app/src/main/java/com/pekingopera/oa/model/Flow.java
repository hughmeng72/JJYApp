package com.pekingopera.oa.model;

import android.net.Uri;
import android.widget.Switch;

import com.google.gson.annotations.SerializedName;
import com.pekingopera.oa.common.IPager;

import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */

public class Flow implements IPager {
    private int Id;
    private String FlowName;
    private String FlowNo;
    private String ModelName = "报销申请";
    private double Amount;
    private String PaymentTerm;
    private String CurrentStepName;
    private int Status;
    private String DepName;
    private String Remark;
    private String CurrentDocPath;
    private String FlowFiles;
    private String DocBody;
    private int CreatorId;
    private String Creator;
    private String CreateTime;
    private String ReviewWords;

    private boolean BudgetInvolved;
    private String ProjectName;
    private String ItemName;
    private double TotalAmount;
    private double AmountPaidProcurement;
    private double AmountToBePaidProcurement;
    private double AmountPaidReimbursement;
    private double AmountToBePaidReimbursement;
    private double AmountLeft;

    private String mPhotoName1;
    private String mPhotoName2;
    private String mPhotoName3;

    private String mPhoto1FilePath;

    private boolean BudgetAuthorized;
    private boolean ApprovalAuthorized;

    private List<FlowStep> Steps;
    private List<FlowDoc> Attachments;
    private List<FlowProcurement> Procurements;
    private String mPhoto2FilePath;
    private String mPhoto3FilePath;

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

    public boolean isBudgetAuthorized() {
        return BudgetAuthorized;
    }

    public boolean isApprovalAuthorized() {
        return ApprovalAuthorized;
    }

    public String getReviewWords() {
        return ReviewWords;
    }

    public void setReviewWords(String reviewWords) {
        ReviewWords = reviewWords;
    }

    public String getCurrentDocPath() {
        return CurrentDocPath;
    }

    public String getFlowFiles() {
        return FlowFiles;
    }

    public String getDocBody() {
        return DocBody;
    }

    public List<FlowStep> getSteps() {
        return Steps;
    }

    public List<FlowDoc> getAttachments() {
        return Attachments;
    }

    public List<FlowProcurement> getProcurements() {
        return Procurements;
    }

    public String getPhotoName1() {
        return mPhotoName1;
    }

    public String getPhotoName2() {
        return mPhotoName2;
    }

    public String getPhotoName3() {
        return mPhotoName3;
    }

    public String getPhoto1FilePath() {
        return mPhoto1FilePath;
    }

    public void setPhoto1FilePath(String photo1FilePath) {
        mPhoto1FilePath = photo1FilePath;

        mPhotoName1 = null;

        String subString[] = mPhoto1FilePath.split("/");

        if (subString != null && subString.length > 0) {
            mPhotoName1 = subString[subString.length - 1];
        }
    }

    public String getPhoto2FilePath() {
        return mPhoto2FilePath;
    }

    public void setPhoto2FilePath(String photo2FilePath) {
        mPhoto2FilePath = photo2FilePath;

        mPhotoName2 = null;

        String subString[] = mPhoto2FilePath.split("/");

        if (subString != null && subString.length > 0) {
            mPhotoName2 = subString[subString.length - 1];
        }
    }

    public String getPhoto3FilePath() {
        return mPhoto3FilePath;
    }

    public void setPhoto3FilePath(String photo3FilePath) {
        mPhoto3FilePath = photo3FilePath;

        mPhotoName3 = null;

        String subString[] = mPhoto3FilePath.split("/");

        if (subString != null && subString.length > 0) {
            mPhotoName3 = subString[subString.length - 1];
        }
    }

    public String getPaymentTerm() {
        return PaymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        PaymentTerm = paymentTerm;
    }

    public void setFlowName(String flowName) {
        FlowName = flowName;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public void setDocBody(String docBody) {
        DocBody = docBody;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public void setFlowFiles(String flowFiles) {
        FlowFiles = flowFiles;
    }

}