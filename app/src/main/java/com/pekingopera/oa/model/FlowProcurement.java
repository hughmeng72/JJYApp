package com.pekingopera.oa.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wayne on 10/21/2016.
 */

public class FlowProcurement {
    @SerializedName("Id")
    private int mId;
    @SerializedName("ProductName")
    private String mProductName;
    @SerializedName("ProductSpec")
    private String mProductSpec;
    @SerializedName("ProductQuantity")
    private int mProductQuantity;
    @SerializedName("Amount")
    private double mAmount;

    @SerializedName("ProjectName")
    private String mProjectName;

    @SerializedName("ItemName")
    private String mItemName;

    @SerializedName("TotalAmount")
    private double mTotalAmount;

    @SerializedName("AmountPaidProcurement")
    private double mAmountPaidProcurement;

    @SerializedName("AmountToBePaidProcurement")
    private double mAmountToBePaidProcurement;

    @SerializedName("AmountPaidReimbursement")
    private double mAmountPaidReimbursement;

    @SerializedName("AmountToBePaidReimbursement")
    private double mAmountToBePaidReimbursement;

    @SerializedName("AmountLeft")
    private double mAmountLeft;

    public int getId() {
        return mId;
    }

    public String getProductName() {
        return mProductName;
    }

    public String getProductSpec() {
        return mProductSpec;
    }

    public int getProductQuantity() {
        return mProductQuantity;
    }

    public double getAmount() {
        return mAmount;
    }

    public String getProjectName() {
        return mProjectName;
    }

    public String getItemName() {
        return mItemName;
    }

    public double getTotalAmount() {
        return mTotalAmount;
    }

    public double getAmountPaidProcurement() {
        return mAmountPaidProcurement;
    }

    public double getAmountToBePaidProcurement() {
        return mAmountToBePaidProcurement;
    }

    public double getAmountPaidReimbursement() {
        return mAmountPaidReimbursement;
    }

    public double getAmountToBePaidReimbursement() {
        return mAmountToBePaidReimbursement;
    }

    public double getAmountLeft() {
        return mAmountLeft;
    }
}