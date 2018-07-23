package com.dwizzel.datamodels;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Created by Dwizzel on 22/11/2017.
 * /actives/GAGPWfDW9QYEIGNol1hLjinfoTF3
 */

public class ActiveModel {

    private boolean gps;
    private GeoPoint position;
    private int status;
    private Date updateTime;

    ActiveModel(){}

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
