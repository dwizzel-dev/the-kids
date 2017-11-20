package com.dwizzel.objects;

/**
 * Created by Dwizzel on 20/11/2017.
 */

public class WatcherObject {

    private String uid = "";
    private String name = "";
    private String phone = "";
    private String email = "";
    private boolean active = false;
    private boolean gps = false;

    WatcherObject(String uid, String name, String phone, String email){
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isGps() {
        return gps;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }









}
