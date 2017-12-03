package com.dwizzel.objects;

import android.content.Context;

import com.dwizzel.Const;
import com.dwizzel.datamodels.ActiveModel;
import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.UserModel;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.datamodels.WatchingModel;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;


/**
 * Created by Dwizzel on 15/11/2017.
 */

public class UserObject extends Observable{

    //instance et tracer
    private static final String TAG = "UserObject";
    private static UserObject sInst;

    //les infos de base
    private static int sRefCount = 0;
    private String email = "";
    private String uid = "";
    private boolean created = false;
    private boolean signed = false;
    private boolean active = false;
    private int status = Const.status.OFFLINE;
    private boolean gps = false;
    private GeoPoint position = new GeoPoint(0.0,0.0);
    private int loginType = 0; //facebook, twitter, email, instagram, etc...

    //les infos sur user retourne du serveur
    private UserModel user;

    //les array des listings
    private HashMap<String, WatcherModel> watchers = new HashMap<>();
    private HashMap<String, InvitationModel> invitations = new HashMap<>();
    private HashMap<String, WatchingModel> watchings = new HashMap<>();

    //les flag pour savoir si on doir ou pas aller chercher les infos sur le serveur
    //ce qui initie aussi de databaseservice listener sur les differents array
    private boolean fetchWatchers = true;
    private boolean fetchWatchings = true;
    private boolean fetchInvitations = true;

    private UserObject(){}



    public boolean isFetchWatchers() {
        return fetchWatchers;
    }

    public void setFetchWatchers(boolean fetchWatchers) {
        this.fetchWatchers = fetchWatchers;
    }

    public boolean isFetchWatchings() {
        return fetchWatchings;
    }

    public void setFetchWatchings(boolean fetchWatchings) {
        this.fetchWatchings = fetchWatchings;
    }

    public boolean isFetchInvitations() {
        return fetchInvitations;
    }

    public void setFetchInvitations(boolean fetchInvitations) {
        this.fetchInvitations = fetchInvitations;
    }



    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



    public void updateWatchers(String uid, ActiveModel activeModel) {
        //ces infos ne viennent pas de la meme collection et arrive apres
        //du au limitation de firestore
        if (watchers != null) {
            if (watchers.containsKey(uid)) {
                try {
                    WatcherModel watcher = watchers.get(uid);
                    watcher.setGps(activeModel.isGps());
                    watcher.setStatus(activeModel.getStatus());
                    watcher.setPosition(activeModel.getPosition());
                    watcher.setUpdateTime(activeModel.getUpdateTime());
                    //et on replace
                    watchers.put(uid, watcher);
                    //on notifie les observers
                    setChanged();
                    notifyObservers(new ObserverNotifObject(Const.notif.WATCHER_UPDATE, uid));
                }catch(Exception e){
                    // null pointer
                }
            }
        }
    }

    public void updateWatchers(String uid, WatcherModel watcher) {
        if (watchers != null) {
            watchers.put(uid, watcher);
        }
    }

    public void addWatcher(String uid, WatcherModel watcher) {
        if (!watchers.containsKey(uid)){
            watchers.put(uid, watcher);
        }
    }

    public void removeWatcher(String uid) {
        if (watchers != null) {
            if (watchers.containsKey(uid)) {
                watchers.remove(uid);
            }
        }
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



    public void updateWatchings(String uid, ActiveModel active) {
        //ces infos ne viennent pas de la meme collection et arrive apres
        //du au limitation de firestore
        if (watchings != null) {
            if (watchings.containsKey(uid)) {
                try {
                    WatchingModel watching = watchings.get(uid);
                    watching.setGps(active.isGps());
                    watching.setStatus(active.getStatus());
                    watching.setPosition(active.getPosition());
                    watching.setUpdateTime(active.getUpdateTime());
                    //et on replace
                    watchings.put(uid, watching);
                    //on notifie les observers
                    setChanged();
                    notifyObservers(new ObserverNotifObject(Const.notif.WATCHING_UPDATE, uid));
                }catch(Exception e){
                    // null pointer
                }
            }
        }
    }

    public void updateWatchings(String uid, WatchingModel watching) {
        if (watchings != null) {
            watchings.put(uid, watching);
        }
    }

    public void addWatching(String uid, WatchingModel watching) {
        if (!watchings.containsKey(uid)){
            watchings.put(uid, watching);
        }
    }

    public void removeWatching(String uid) {
        if (watchings != null) {
            if (watchings.containsKey(uid)) {
                watchings.remove(uid);
            }
        }
    }

    public HashMap<String, WatchingModel> getWatchings(){
        return watchings;
    }

    public WatchingModel getWatching(String uid){
        if (watchings != null) {
            if (watchings.containsKey(uid)) {
                return watchings.get(uid);
            }
        }
        return null;
    }



    public void updateInvitation(String inviteId, InvitationModel invite) {
        //ces infos ne viennent pas de la meme collection et arrive apres
        //du au limitation de firestore
        if (invitations != null) {
            //et on replace
            invitations.put(inviteId, invite);
        }
    }

    public void addInvitation(String inviteId, InvitationModel invite) {
        if (!invitations.containsKey(inviteId)){
            invitations.put(inviteId, invite);
        }
    }

    public void removeInvitation(String inviteId) {
        if (invitations != null) {
            if (invitations.containsKey(inviteId)) {
                invitations.remove(inviteId);
            }
        }
    }

    public HashMap<String, InvitationModel> getInvitations(){
        return invitations;
    }

    public InvitationModel getInvitation(String inviteId){
        if (invitations != null) {
            if (invitations.containsKey(inviteId)) {
                return invitations.get(inviteId);
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
        user = null;
        position = new GeoPoint(0.0,0.0);
        loginType = 0;
        watchers = new HashMap<>();
        invitations = new HashMap<>();
        watchings = new HashMap<>();
        status = Const.status.OFFLINE;

        //flag the fetch different listings
        fetchWatchers = true;
        fetchWatchings = true;
        fetchInvitations = true;
    }

    public String getEmail(){
        return email;
    }

    public UserModel getUser(){
        return user;
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

    public void setUser(UserModel user){
        this.user = user;
        try{
            status = this.user.getStatus();
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
        if(user != null){
            return Utils.getInstance().formatDate(context, user.getUpdateTime());
        }
        return "";
    }

    //---------------------------------------------------------------------------------------------
    //Firestore Data Model


    public Map<String, Object> toUserData(){
        Map<String, Object> map = new HashMap<>(6);
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

    public Map<String, Object> toInviteData(){
        Map<String, Object> map = new HashMap<>(5);
        map.put("from", getUid());
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("to", ""); //since we don't know yet
        map.put("state", Const.invitation.INNACTIVE);
        map.put("code", Const.invitation.DEFAULT_CODE);
        return map;
    }

    public Map<String, Object> toInvitationData(String inviteId, String name, String phone, String email){
        Map<String, Object> map = new HashMap<>(4);
        map.put("email", email);
        map.put("name", name);
        map.put("phone", phone);
        map.put("inviteId", inviteId);
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("state", Const.invitation.PENDING);
        return map;
    }





}