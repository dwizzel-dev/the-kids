package com.dwizzel.models;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Dwizzel on 10/11/2017.
 */

public class UserModel {

    private String email;
    private String uid;
    private @ServerTimestamp Date createTime;

    public UserModel(){}

    public UserModel(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public Date getCreateTime() {
        return createTime;
    }

}
