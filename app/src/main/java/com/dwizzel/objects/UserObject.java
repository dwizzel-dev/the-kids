package com.dwizzel.objects;

import android.content.Context;

import com.dwizzel.Const;
import com.dwizzel.datamodels.ActiveModel;
import com.dwizzel.datamodels.DataModel;
import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
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
    private int status = Const.status.OFFLINE;
    private boolean gps = false;
    private DataModel data;
    private GeoPoint position = new GeoPoint(0.0,0.0);
    private int loginType = 0; //facebook, twitter, email, instagram, etc...
    private HashMap<String, WatcherModel> watchers;
    private HashMap<String, InvitationModel> invites;

    private UserObject(){
        //default
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void updateWatchers(String uid, int state, boolean gps, GeoPoint position, Date updateTime) {
        if (watchers != null) {
            if (watchers.containsKey(uid)) {
                WatcherModel watcherModel = watchers.get(uid);
                watcherModel.setStatus(state);
                watcherModel.setGps(gps);
                watcherModel.setPosition(position);
                watcherModel.setUpdateTime(updateTime);
                //et on replace
                watchers.put(uid, watcherModel);
            }
        }
    }

    public boolean addWatcher(String uid, WatcherModel watcher) {
        if (watchers == null) {
            watchers = new HashMap<>();
        }
        if (!watchers.containsKey(uid)){
            watchers.put(uid, watcher);
            return true;
        }
        return false;
    }

    public boolean removeWatcher(String uid) {
        if (watchers != null) {
            if (watchers.containsKey(uid)) {
                watchers.remove(uid);
                return true;
            }
        }
        return false;
    }

    public HashMap<String, WatcherModel> getWatchers(){
        return watchers;
    }

    public WatcherModel getWatcher(String uid){
        if (watchers != null) {
            if (watchers.containsKey(uid)) {
                return watchers.get(uid);
            }
        }
        return null;
    }

    public void updateInvite(String inviteId, int state, Date updateTime, Date createTime) {
        if (invites != null) {
            if (invites.containsKey(inviteId)) {
                InvitationModel inviteModel = invites.get(inviteId);
                inviteModel.setState(state);
                inviteModel.setCreateTime(createTime);
                inviteModel.setUpdateTime(updateTime);
                //et on replace
                invites.put(inviteId, inviteModel);
            }
        }
    }

    public boolean addInvite(String inviteId, InvitationModel invite) {
        if (invites == null) {
            invites = new HashMap<>();
        }
        if (!invites.containsKey(inviteId)){
            invites.put(inviteId, invite);
            return true;
        }
        return false;
    }

    public boolean removeInvite(String inviteId) {
        if (invites != null) {
            if (invites.containsKey(inviteId)) {
                invites.remove(inviteId);
                return true;
            }
        }
        return false;
    }

    public HashMap<String, InvitationModel> getInvites(){
        return invites;
    }

    public InvitationModel getInvite(String inviteId){
        if (invites != null) {
            if (invites.containsKey(inviteId)) {
                return invites.get(inviteId);
            }
        }
        return null;
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
        watchers = null;
        invites = null;
        status = Const.status.OFFLINE;
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
        try{
            status = this.data.getStatus();
        }catch (Exception e){
           //
        }
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

    public String getLastConnection(Context context){
        //cherche la date
        if(data != null){
            return Utils.getInstance().formatDate(context, data.getUpdateTime());
        }
        return "";
    }

    //---------------------------------------------------------------------------------------------
    //Firestore Data Model


    public Map<String, Object> toUserData(){
        Map<String, Object> map = new HashMap<>(3);
        map.put("email", getEmail() );
        map.put("uid", getUid());
        map.put("status", getStatus());
        map.put("createTime", FieldValue.serverTimestamp());
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("loginType", getLoginType());
        return map;
    }

    public Map<String, Object> toActiveData(){
        Map<String, Object> map = new HashMap<>(4);
        map.put("status", getStatus());
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("position", getPosition());
        map.put("gps", isGps());
        return map;
    }

    public Map<String, Object> toInactiveData(){
        Map<String, Object> map = new HashMap<>(4);
        map.put("status", Const.status.OFFLINE);
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("position", getPosition());
        map.put("gps", isGps());
        return map;
    }





}