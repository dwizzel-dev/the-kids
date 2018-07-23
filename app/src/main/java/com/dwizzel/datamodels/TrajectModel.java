package com.dwizzel.datamodels;

import java.util.Date;
import com.google.firebase.firestore.GeoPoint;

/**
 * Created by Dwizzel on 23/08/2018.
 */

//---------------------------------------------------------------------------------------------
//Firestore Data Object

public class TrajectModel{

    private Date createTime;
    private Date updateTime;
    private String uid; //the uid of the user who started the traject
    private int status;
    private String[] watchers;
    private GeoPoint[] positions;

    TrajectModel(){}

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status= status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String[] getWatchers() {
        return watchers;
    }

    public void setWatchers(String[] watchers) {
        this.watchers = watchers;
    }

    public GeoPoint[] getPositions() {
        return positions;
    }

    public void setPositions(GeoPoint[] positions) {
        this.positions = positions;
    }



}
