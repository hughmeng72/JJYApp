package com.pekingopera.oa.common;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pekingopera.oa.R;
import com.pekingopera.oa.model.ResponseResult;
import com.pekingopera.oa.model.Version;
import com.pekingopera.versionupdate.ParseData;
import com.pekingopera.versionupdate.UpdateHelper;
import com.pekingopera.versionupdate.bean.Update;
import com.pekingopera.versionupdate.type.RequestType;

import java.lang.reflect.Type;

/**
 * Created by wayne on 10/11/2016.
 */

public class UpdateConfig {

    private static String checkUrl = "WebUI/WebService/Pages/Update.aspx"; // For debug in pc
//private static String checkUrl = "WebService/Pages/Update.aspx";

    /**
     * Update check via Http Get
     */
    public static void initGet(Context context) {
        UpdateHelper.init(context);

        String url = String.format("%1$s%2$s", SoapHelper.getWsNamespace(), checkUrl);
        UpdateHelper.getInstance()
                .setMethod(RequestType.get)
                .setCheckUrl(url)
                .setDialogLayout(R.layout.dialog_update)
                .setCheckJsonParser(new ParseData() {
                    @Override
                    public Update parse(String response) {
                        Update update = new Update();

                        ResponseResult<Version> result;

                        GsonBuilder gson = new GsonBuilder();
                        Type resultType = new TypeToken<ResponseResult<Version>>() {
                        }.getType();

                        try {
                            result = gson.create().fromJson(response, resultType);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return update;
                        }

                        Version version = result.getEntity();

                        update.setUpdateUrl(version.getUpdateUrl());
                        update.setVersionCode(version.getVersionCode());
                        update.setApkSize(version.getApkSize());
                        update.setVersionName(version.getVersionName());
                        update.setUpdateContent(version.getUpdateContent());
                        update.setForce(version.isForce());

                        return update;
                    }
                });
    }
}