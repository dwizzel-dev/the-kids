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
    private String token = "";
    private String locale = "";
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

    public static UserObject getInstance(){
        Tracer.log(TAG, "getInstance: " + (sRefCount++));
        if(sInst == null){
            sInst = new UserObject();
        }
        return sInst;
    }

    public void resetUser(){
        email = "";
        uid = "";
        token = "";
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
        //car ce sera le databaseService qui aura un listener sur ces array pour les update et autres
        fetchWatchers = true;
        fetchWatchings = true;
        fetchInvitations = true;
    }




    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }




    public boolean isFetchWatchers() {
        return fetchWatchers;
    }

    public void setFetchWatchers(boolean fetchWatchers) {
        this.fetchWatchers = fetchWatchers;
    }

    public void updateWatcher(String uid, ActiveModel activeModel) {
        //ces infos ne viennent pas de la meme collection et arrive apres
        //du au limitation de firestore
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

    public boolean updateWatcher(String uid, WatcherModel watcher) {
        //on a un probleme car le users->watchers->[status, gps, updateTime, position] est toujours rien
        //car c'est ActiveModel qui maintient ca
        //alors on va prendre ce qu'il y avait avant et le remetttre dedans si existait deja
        if(watchers.containsKey(uid)){
            WatcherModel watcherModel = watchers.get(uid);
            watcher.setStatus(watcherModel.getStatus());
            watcher.setGps(watcherModel.isGps());
            watcher.setUpdateTime(watcherModel.getUpdateTime());
            watcher.setPosition(watcherModel.getPosition());
            //et la on le remet dedans
            watchers.put(uid, watcher);
            //on notifie les observers d'un update
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.WATCHER_UPDATE, uid));
            //car existe deja
            return false;
        }else{
            //on le met directement dedans
            watchers.put(uid, watcher);
            //on notifie les observers d'un addded
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.WATCHER_ADDED, uid));
        }
        return true;
    }

    public void removeWatcher(String uid) {
        if (watchers.containsKey(uid)) {
            watchers.remove(uid);
            //on notifie les observers
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.WATCHER_REMOVE, uid));
        }
    }

    public HashMap<String, WatcherModel> getWatchers(){
        return watchers;
    }

    public WatcherModel getWatcher(String uid){
        if (watchers.containsKey(uid)) {
            return watchers.get(uid);
            }
        return null;
    }




    public boolean isFetchWatchings() {
        return fetchWatchings;
    }

    public void setFetchWatchings(boolean fetchWatchings) {
        this.fetchWatchings = fetchWatchings;
    }

    public void updateWatching(String uid, ActiveModel active) {
        //ces infos ne viennent pas de la meme collection et arrive apres
        //du au limitation de firestore
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

    public boolean updateWatching(String uid, WatchingModel watching) {
        //on a un probleme car le users->watchers->[status, gps, updateTime, position] est toujours rien
        //car c'est ActiveModel qui maintient ca
        //alors on va prendre ce qu'il y avait avant et le remetttre dedans si existait deja
        if(watchings.containsKey(uid)){
            WatchingModel watchingModel = watchings.get(uid);
            watching.setStatus(watchingModel.getStatus());
            watching.setGps(watchingModel.isGps());
            watching.setUpdateTime(watchingModel.getUpdateTime());
            watching.setPosition(watchingModel.getPosition());
            //et la on le remet dedans
            watchings.put(uid, watching);
            //on notifie les observers d'un update
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.WATCHING_UPDATE, uid));
            //car existe deja
            return false;
        }else{
            //on le met directement dedans
            watchings.put(uid, watching);
            //on notifie les observers d'un addded
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.WATCHING_ADDED, uid));
        }
        return true;
    }

    public void removeWatching(String uid) {
        if (watchings.containsKey(uid)) {
            watchings.remove(uid);
            //on notifie les observers
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.WATCHING_REMOVE, uid));
        }
    }

    public HashMap<String, WatchingModel> getWatchings(){
        return watchings;
    }

    public WatchingModel getWatching(String uid){
        if (watchings.containsKey(uid)) {
            return watchings.get(uid);
        }
        return null;
    }




    public boolean isFetchInvitations() {
        return fetchInvitations;
    }

    public void setFetchInvitations(boolean fetchInvitations) {
        this.fetchInvitations = fetchInvitations;
    }

    public void updateInvitation(String inviteId, InvitationModel invite) {
        if (invitations.containsKey(inviteId)){
            invitations.put(inviteId, invite);
            //on notifie les observers
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.INVITATION_UPDATE, inviteId));
        }else{
            invitations.put(inviteId, invite);
            //on notifie les observers
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.INVITATION_ADDED, inviteId));
        }
    }

    public void removeInvitation(String inviteId) {
        if (invitations.containsKey(inviteId)) {
            invitations.remove(inviteId);
            //on notifie les observers
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.INVITATION_REMOVE, inviteId));
        }
    }

    public HashMap<String, InvitationModel> getInvitations(){
        return invitations;
    }

    public InvitationModel getInvitation(String inviteId){
        if (invitations.containsKey(inviteId)) {
            return invitations.get(inviteId);
        }
        return null;
    }




    //gett and setter
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public int getLoginType(){
        return loginType;
    }

    public void setLoginType(int type){
        this.loginType = type;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public UserModel getUser(){
        return user;
    }

    public void setUser(UserModel user){
        this.user = user;
        try{
            status = this.user.getStatus();
        }catch (Exception e){
           //
        }
    }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
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

    public boolean isGps() {
        return gps;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public GeoPoint getPosition(){
        return position;
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
        Map<String, Object> map = new HashMap<>(8);
        map.put("email", getEmail() );
        map.put("uid", getUid());
        map.put("status", getStatus());
        map.put("createTime", FieldValue.serverTimestamp());
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("loginType", getLoginType());
        map.put("token", getToken());
        map.put("locale", getLocale());
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
        map.put("gps", false);
        return map;
    }

    public Map<String, Object> toInviteData(){
        Map<String, Object> map = new HashMap<>(3);
        map.put("from", getUid());
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("code", Const.invitation.DEFAULT_CODE);
        return map;
    }

    public Map<String, Object> toInvitationData(String inviteId, String name, String phone, String email, String code){
        Map<String, Object> map = new HashMap<>(6);
        map.put("email", email);
        map.put("name", name);
        map.put("phone", phone);
        map.put("inviteId", inviteId);
        map.put("updateTime", FieldValue.serverTimestamp());
        map.put("code", code);
        return map;
    }





}