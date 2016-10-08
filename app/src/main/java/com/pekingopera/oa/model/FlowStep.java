package com.pekingopera.oa.model;

import java.io.Serializable;

public class FlowStep {
    private String StepName;
    private String UserName;
    private String AddTime;
    private String Action;
    private String Result;
    private String Description;

    public String getStepName() {
        return StepName;
    }

    public String getUserName() {
        return UserName;
    }

    public String getAddTime() {
        return AddTime;
    }

    public String getAction() {
        return Action;
    }

    public String getResult() {
        return Result;
    }

    public String getDescription() {
        return Description;
    }
}
