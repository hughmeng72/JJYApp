package com.pekingopera.oa.model;

/**
 * Created by wayne on 9/22/2016.
 */
public class User {
    private ResponseBase error;
    private String token = "";
    private int userId;
    private int depId;
    private String userName;
    private String RealName;

    public String getRealName() {
        return RealName;
    }

    private static User sUser;

    public String getToken() {
        return token;
    }

    private User() {
    }

    public static User get() {
        if (sUser == null) {
            sUser = new User();
        }
        return sUser;
    }

    public static void setUser(User user) {
        sUser = user;
    }

    public ResponseBase getError() {
        return error;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }


    public int getDepId() {
        return depId;
    }
}
