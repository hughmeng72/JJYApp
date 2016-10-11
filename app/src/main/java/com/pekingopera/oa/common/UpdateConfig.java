package com.pekingopera.oa.common;

import android.content.Context;

import com.dou361.update.ParseData;
import com.dou361.update.UpdateHelper;
import com.dou361.update.bean.Update;
import com.dou361.update.type.RequestType;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pekingopera.oa.R;
import com.pekingopera.oa.model.ResponseResult;
import com.pekingopera.oa.model.Version;

import java.lang.reflect.Type;

/**
 * Created by wayne on 10/11/2016.
 */

public class UpdateConfig {
//    private static String checkUrl = "WebService/Pages/Update.aspx"; // For JJY production server
    private static String checkUrl = "WebUI/WebService/Pages/Update.aspx"; // For debug in pc

    /**
     * Update check via Http Get
     */
    public static void initGet(Context context) {
        UpdateHelper.init(context);
        UpdateHelper.getInstance()
                .setMethod(RequestType.get)
                .setCheckUrl(SoapHelper.getWsNamespace() + checkUrl)
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