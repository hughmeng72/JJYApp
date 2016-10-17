package com.pekingopera.oa.common;

/**
 * Created by wayne on 10/3/2016.
 */

public class SoapHelper {
    public static String getWsNamespace() {
//        return "http://192.168.8.101/"; // For JJY Debug
        return "http://192.168.9.31/"; // For Home CS
//        return "http://192.168.1.2/"; // For Home XB
//        return "http://www.jjyoa.com:8000/";
    }

    public static String getWsUrl() {
//        return getWsNamespace() + "WebService/Perkingopera.asmx";
        return getWsNamespace() + "WebUI/WebService/Perkingopera.asmx"; // For debug
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
    }

    public static String getWsMethodOfFlowRequest() {
        return "SubmitFlowRequest";
    }

    public static String getWsMethodOfFlowRejectRequest() {
        return "RejectFlowRequest";
    }

    public static String getWsMethodOfFlowFinalizeRequest() {
        return "FinalizeFlowRequest";
    }

    public static String getWsMethodOfMissedFlowReviwer() {
        return "GetMissedFlowReviwer";
    }

    public static String getWsMethodOfUpdateFlowReviewer() {
        return "UpdateFlowReviewer";
    }

    public static String getWsMethodOfMissedGovReviwer() {
        return "GetMissedGovReviwer";
    }

    public static String getWsMethodOfUpdateGovReviewer() {
        return "UpdateGovReviewer";
    }

}