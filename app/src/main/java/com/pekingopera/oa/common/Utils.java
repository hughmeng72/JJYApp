package com.pekingopera.oa.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.ksoap2.serialization.PropertyInfo;

/**
 * Created by wayne on 9/22/2016.
 */
public class Utils {
    public static boolean isNetworkConnected(Context context) {
        boolean ret = false;

        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            ret = true;
        }

        return ret;
    }

    public static PropertyInfo newPropertyInstance(String name, Object value, Object type) {
        PropertyInfo para = new PropertyInfo();
        para.setName(name);
        para.setValue(value);
        para.setType(type);

        return para;
    }

    public static String getWsNamespace() {
        return "http://192.168.10.107/";
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
}