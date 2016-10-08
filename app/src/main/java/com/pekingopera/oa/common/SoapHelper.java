package com.pekingopera.oa.common;

/**
 * Created by wayne on 10/3/2016.
 */

public class SoapHelper {
    public static String getWsNamespace() {
        return "http://192.168.9.31/";
    }

    public static String getWsUrl() {
        return getWsNamespace() + "WebUI/WebService/Perkingopera.asmx";
    }

    public static String getWsSoapAction() {
        return getWsNamespace();
    }

    public static String getWsMethodOfUserAuthentication() {
        return "GetTokenByUserNameAndPassword";
    }

    public static String getWsMethodOfNoticeList() {
        return "GetNoticeList";
    }

    public static String getWsMethodOfMailList() {
        return "GetMailList";
    }

    public static String getWsMethodOfCalendarList() {
        return "GetCalendarList";
    }

    public static String getWsMethodOfApprovalFlowList() {
        return "GetApprovalFlowList";
    }

    public static String getWsMethodOfFinancialFlowList() {
        return "GetFinancialFlowList";
    }

    public static String getWsMethodOfGeneralFlowList() {
        return "GetGeneralFlowList";
    }

    public static String getWsMethodOfFlowDetail() {
        return "GetFlowDetail";
    }

    public static String getWsMethodOfApprovalGovList() {
        return "GetApprovalGovList";
    }

    public static String getWsMethodOfGovList() {
        return "GetGovList";
    }

    public static String getWsMethodOfGovDetail() {
        return "GetGovDetail";
    }

    public static String getWsMethodOfGovRequest() {
        return "SubmitGovRequest";
    }

    public static String getWsMethodOfGovFinalizeRequest() {
        return "FinalizeGovRequest";
    }}