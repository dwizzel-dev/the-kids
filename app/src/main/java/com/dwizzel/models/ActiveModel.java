package com.dwizzel.models;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Dwizzel on 15/11/2017.
 */

public class ActiveModel {

    private String position;
    private @ServerTimestamp Date createTime;
    private @ServerTimestamp Date updateTime;
    private String uid;

    public ActiveModel(){}

    public ActiveModel(String uid, String position) {
        this.uid = uid;
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public String getUid() {
        return uid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

}
