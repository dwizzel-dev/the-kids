package com.dwizzel.objects;

/**
 * Created by Dwizzel on 15/11/2017.
 */

public final class UserObject{

    private String username;
    private String uid;

    public UserObject(String username, String uid) {
        this.username = username;
        this.uid = uid;
    }

    public String getUsername(){
        return username;
    }

    public Object getUserId(){
        return uid;
    }

}