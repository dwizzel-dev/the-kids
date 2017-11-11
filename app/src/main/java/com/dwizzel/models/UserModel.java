package com.dwizzel.models;

/**
 * Created by Dwizzel on 10/11/2017.
 */

public class UserModel {

    private String email;
    private String uid;

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



}
