package com.dwizzel.objects;

import android.content.Context;

import com.dwizzel.Const;
import com.dwizzel.datamodels.TrajectModel;
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
 * @Created by Dwizzel on 23/08/2018.
 *
 * @notes1: we can have multiple traject at the same time
 * - as a walker we can have multiple
 * - as a walking only one
 * - in both case we need the walking and walkers
 *
 * @notes2: les watchers et les watching sont gerer vie le UserObject qui les contient tous
 *
 *
 */

public class TrajectObject extends Observable{

    //instance et tracer
    private static final String TAG = "TrajectObject";
    private static TrajectObject sInst;

    //les infos de base
    private static int sRefCount = 0;

    //le uid du trajet que l'on demarre soi meme
    private String trajectUid;

    //le traject model data il peut y en avoir plusieurs un pour soi comme walking,
    // mais plusieurs en walkers
    private HashMap<String, TrajectModel> trajects = new HashMap<>();

    //les array des watchers associer le uid avec ceux du traject model qui nous accompagne
    private HashMap<String, WatcherModel> watchers = new HashMap<>();

    //le array des watching pour les personnes que l'on accompagne
    private HashMap<String, WatchingModel> watchings = new HashMap<>();

    private TrajectObject(){}

    public static TrajectObject getInstance(){
        Tracer.log(TAG, "getInstance: " + (sRefCount++));
        if(sInst == null){
            sInst = new TrajectObject();
        }
        return sInst;
    }

    public void resetTraject(){
        watchers = new HashMap<>();
        watchings = new HashMap<>();
        trajects = new HashMap<>();
        trajectUid = "";
    }

    public void removeTraject(String uid) {
        if (trajects.containsKey(uid)) {
            trajects.remove(uid);
            //on notifie les observers
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.TRAJECT_REMOVE, uid));
        }
    }

    public HashMap<String, TrajectModel> getTrajects(){
        return trajects;
    }

    public TrajectModel getTraject(String uid){
        if (trajects.containsKey(uid)) {
            trajects.get(uid);
        }
        return null;
    }

    public void updateTraject(String uid, TrajectModel traject) {
        if (trajects.containsKey(uid)) {
            //et on le nouveau data replace
            trajects.put(uid, traject);
             //on notifie les observers
            setChanged();
            notifyObservers(new ObserverNotifObject(Const.notif.TRAJECT_UPDATE, uid));
        }
    }

    public void setTrajectUid(String uid){
        this.trajectUid = uid;
    }

    public String getTrajectUid(){
        return trajectUid;
    }

    public GeoPoint getLastPosition(String uid){
        if (trajects.containsKey(uid)) {
            TrajectModel traject = trajects.get(uid);
            GeoPoint[] positions = traject.getPositions();
            try {
                if(positions.length > 0) {
                    return positions[positions.length - 1];
                }
            }catch(Exception e){
                // null pointer
            }
        }
        return null;
    }

    public GeoPoint[] getPositions(String uid){
        if (trajects.containsKey(uid)) {
            TrajectModel traject = trajects.get(uid);
            GeoPoint[] positions = traject.getPositions();
            if(positions.length > 0) {
                return positions;
            }
        }
        return null;
    }

}