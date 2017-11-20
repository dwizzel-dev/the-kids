package com.dwizzel.objects;

import com.dwizzel.datamodels.DataModel;
import com.dwizzel.utils.Tracer;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Dwizzel on 15/11/2017.
 */

public class UserObject{

    private static final String TAG = "UserObject";
    private static UserObject sInst;
    private static int sRefCount = 0;
    private String email = "";
    private String uid = "";
    private boolean created = false;
    private boolean signed = false;
    private boolean active = false;
    private boolean gps = false;
    private DataModel data;
    private GeoPoint position = new GeoPoint(0.0,0.0);
    private int loginType = 0; //facebook, twitter, email, instagram, etc...
    private HashMap<String, WatcherObject> watchers = new HashMap<>();

    private UserObject(){
        //default
    }

    public boolean addWatcher(String uid, WatcherObject watcher) {
        if (!watchers.containsKey(uid)){
            watchers.put(uid, watcher);
            return true;
        }
        return false;
    }

    public boolean removeWatcher(String uid) {
        if (watchers.containsKey(uid)){
            watchers.remove(uid);
            return true;
        }
        return false;
    }

    public HashMap<String, WatcherObject> getWatchers(){
        return watchers;
    }

    public static UserObject getInstance(){
        Tracer.log(TAG, "getInstance: " + (sRefCount++));
        if(sInst == null){
            sInst = new UserObject();
        }
        return  sInst;
    }

    public void resetUser(){
        email = "";
        uid = "";
        active = false;
        gps = false;
        created = false;
        signed = false;
        data = null;
        position = new GeoPoint(0.0,0.0);
        loginType = 0;
    }

    public String getEmail(){
        return email;
    }

    public String getUserId(){
        return uid;
    }

    public Object getData(){
        return data;
    }

    public boolean isActive(){
        return active;
    }

    public int getLoginType(){
        return loginType;
    }

    public boolean isGps() {
        return gps;
    }

    public GeoPoint getPosition(){
        return position;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public void setLoginType(int type){
        this.loginType = type;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setData(DataModel data){
        this.data = data;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getUid(){
        return uid;
    }

    public boolean isSigned(){
        return signed;
    }

    public void setSigned(boolean signed){
        this.signed = signed;
    }

    public boolean isCreated(){
        return created;
    }

    public void setCreated(boolean created){
        this.created = created;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public void setPosition(double latitude, double longitude){
        this.position = new GeoPoint(longitude, latitude);
    }

    public void setPosition(GeoPoint position){
        this.position = position;
    }

    //---------------------------------------------------------------------------------------------
    //Firestore Data Model


    public Map<String, Object> toUserData(){
        Map<String, Object> map = new HashMap<>(3);
        map.put("email", getEmail() );
        map.put("uid", getUid());
        map.put("createTime", FieldValue.serverTimestamp());
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("loginType", getLoginType());
        return map;
    }

    public Map<String, Object> toActiveData(){
        Map<String, Object> map = new HashMap<>(3);
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("position", getPosition());
        map.put("gps", isGps());
        return map;
    }





}