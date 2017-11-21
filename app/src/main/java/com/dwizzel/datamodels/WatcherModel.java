package com.dwizzel.datamodels;

/**
 * Created by Dwizzel on 20/11/2017.
 */

public class WatcherModel{

    private String email;
    private String uid;
    private String phone;
    private String name;

    public WatcherModel(){

    }

    public WatcherModel(String uid, String email, String phone, String name){
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




}
