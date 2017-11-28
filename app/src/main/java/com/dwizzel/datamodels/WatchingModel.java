package com.dwizzel.datamodels;

import com.dwizzel.Const;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Created by Dwizzel on 20/11/2017.
 */

public class WatchingModel{

    private String email;
    private String uid;
    private String phone;
    private String name;
    private boolean gps;
    private GeoPoint position;
    private int status = Const.status.OFFLINE;
    private Date updateTime;

    public WatchingModel(){

    }

    public WatchingModel(String uid, String email, String phone, String name){
        this.uid = uid;
        this.email = email;
        this.phone = phone;
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isGps() {
        return gps;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }




}
