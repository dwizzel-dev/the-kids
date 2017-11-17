package com.dwizzel.models;

import com.dwizzel.Const;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Dwizzel on 10/11/2017.
 */

public class UserModel {

    private String email;
    private String uid;
    private @ServerTimestamp Date createTime;
    private @ServerTimestamp Date updateTime;
    private Date updateTimePosition;
    private boolean active = true;
    private boolean gps = false;
    private int loginType = Const.user.TYPE_EMAIL;
    private HashMap<String, Double> position;

    public UserModel(){}

    public UserModel(String email, String uid) {
        this.email = email;
        this.uid = uid;
        position = new HashMap<String, Double>(){{
                put("latitude", 0.0);
                put("longitude", 0.0);
                put("altitude", 0.0);
        }};
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public boolean isGps() {
        return gps;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Date getUpdateTimePosition() {
        return updateTimePosition;
    }

    public boolean isActive() {
        return active;
    }

    public HashMap<String, Double> getPosition() {
        return position;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public void setPosition(final double longitude, final double latitude, final double altitude) {
        position = new HashMap<String, Double>(){{
            put("latitude", longitude);
            put("longitude", latitude);
            put("altitude", altitude);
        }};
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public int getLoginType() {
        return loginType;
    }

}
