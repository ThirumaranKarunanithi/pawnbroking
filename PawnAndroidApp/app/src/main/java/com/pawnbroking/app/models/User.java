package com.pawnbroking.app.models;

import org.json.JSONObject;

public class User {
    public final String userId;
    public final String userName;
    public final String employeeName;
    public final String roleId;
    public final String roleName;
    public final String token;

    public User(String userId, String userName, String employeeName,
                String roleId, String roleName, String token) {
        this.userId = userId;
        this.userName = userName;
        this.employeeName = employeeName;
        this.roleId = roleId;
        this.roleName = roleName;
        this.token = token;
    }

    public static User fromJson(JSONObject j) throws Exception {
        return new User(
            j.optString("userId",       String.valueOf(j.optInt("userId", 0))),
            j.getString("userName"),
            j.getString("employeeName"),
            j.optString("roleId",       String.valueOf(j.optInt("roleId", 0))),
            j.optString("roleName",     ""),
            j.getString("token")
        );
    }
}
