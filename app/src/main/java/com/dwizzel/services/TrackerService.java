package com.dwizzel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dwizzel.models.UserModel;
import com.google.firebase.auth.AuthCredential;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Dwizzel on 10/11/2017.
 * https://developer.android.com/guide/components/services.html
 * https://github.com/AltBeacon/android-beacon-library/issues/304
 * https://android.googlesource.com/platform/frameworks/base/+/483f3b06ea84440a082e21b68ec2c2e54046f5a6/services/java/com/android/server/am/ActivityManagerService.java
 * http://mylifewithandroid.blogspot.ca/2008/01/about-binders.html
 * https://developer.android.com/guide/components/aidl.html#CreateAidl
 * https://developer.android.com/guide/components/bound-services.html#Messenger
 * https://developer.android.com/reference/android/app/Service.html
 *
 *
 * NOTES: on va le faire en IBinder sans le AIDL, car pas besoin d'IPC, et j'aimerais mieux partager
 * des objets plus complexes, mais bon on l'aura pratique haha!
 *
 * NOTES: vu que le service peut repartir sans l'application on va chercher les infos de base et on set
 * les infos du user dans le service
 */

public class TrackerService extends Service{

    private final static String TAG = "TheKids.TrackerService";
    private Thread mThTimer;
    private long mTimer = 0;
    private int mKeepAliveSignal = 60; //seconds
    private Thread mThCounter;
    private ITrackerBinderCallback mBinderCallback;
    private AuthService mAuthService;
    private FirestoreService mFirestoreService;
    private UserModel mUser;

    @NonNull
    public static Intent getIntent(Context context) {
        Log.w(TAG, "getIntent");
        return new Intent(context, TrackerService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "onBind:" + intent);
        return mTrackerBinder;
    }

    @Override
    public void onDestroy(){
        Log.w(TAG, "onDestroy");
        super.onDestroy();
        if(mThTimer != null){
            mThTimer.interrupt();
        }
    }

    @Override
    public void onCreate(){
        Log.w(TAG, "onCreate");
        super.onCreate();
        try {
            mAuthService = new AuthService(getApplicationContext(), mTrackerBinder);
            mFirestoreService = FirestoreService.getInstance();
            //start running time elapsed
            startRunningTime();
            //check pour user infos si etait connecte
            setUser();
        }catch(Exception e){
            Log.w(TAG, "onCreate.exception: ", e);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.w(TAG, "onUnbind:" + intent);
        //si clear le callback
        try {
            ((TrackerService.TrackerBinder) mTrackerBinder).unregisterCallback();
        }catch (Exception e){
            Log.w(TAG, "onUnbind.Exception: ", e);
        }
        //si a TRUE alors il va faire un rebind au lieu d'un bind
        //donc il va reprendre ou il etait, si il avait deja fait un register de son callback
        //il va le reutiliser, ce qui cause des doublons de callback
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.w(TAG, "onRebind: " + intent);
    }

    private String getTimer(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date(mTimer*1000));
    }

    private void startRunningTime() {
        Log.w(TAG, "startRunningTime");
        if(mThTimer == null) {
            try {
                mThTimer = new Thread(new Runnable() {

                    private final static String TAG = "TheKids.mThTimer";

                    @Override
                    public void run() {
                        try {
                            while (true) {
                                //pause au seconde
                                Thread.sleep(1000);
                                mTimer++;
                                //keepAlive au minute
                                if(mTimer%mKeepAliveSignal == 0){
                                    keepActive();
                                }
                            }
                        } catch (InterruptedException ie) {
                            Log.w(TAG, "run.InterruptedException");
                        } catch (Exception e){
                            Log.w(TAG, "run.Exception: ", e);
                        }
                    }
                });
                mThTimer.start();
            }catch (Exception e){
                Log.w(TAG, "startTimer.Exception: ", e);
            }
        }
    }

    private void untrackCounter(){
        Log.w(TAG, "untrackCounter");
        try{
            if(mThCounter != null) {
                mThCounter.interrupt();
                mThCounter = null;
            }
        }catch (Exception e){
            Log.w(TAG, "untrackCounter.Exception: ", e);
        }
    }

    private void trackCounter(){
        Log.w(TAG, "trackCounter");
        try {
            mThCounter = new Thread(new Runnable() {

                private final static String TAG = "TheKids.mThCounter";

                @Override
                public void run() {
                    try {
                        while (true) {
                            //callback every 10 seconds
                            Thread.sleep(60000);
                            if(mBinderCallback != null) {
                                //on envoi le callback
                                mBinderCallback.handleResponse(mTimer);
                            } else {
                                //on arrete le thread si no one to callback
                                Thread.currentThread().interrupt();
                            }
                        }
                    } catch (InterruptedException ie) {
                        Log.w(TAG, "run.InterruptedException");
                    } catch (Exception e) {
                        Log.w(TAG, "run.Exception: ", e);
                    }

                }
            });
            mThCounter.start();
        }catch (Exception e){
            Log.w(TAG, "trackCounter.Exception: ", e);
        }
    }

    private void getUserInfos(){
        Log.w(TAG, "getUserInfos");
       try {
           mFirestoreService.getUserInfos(mUser);
        }catch (Exception e){
            Log.w(TAG, "getUserInfos.exception: ", e);
        }
    }

    private void activateUser(){
        Log.w(TAG, "activateUser");
        try {
            mFirestoreService.activateUser(mUser.getUid(), null);
        }catch (Exception e){
            Log.w(TAG, "activateUser.exception: ", e);
        }
    }

    private void deactivateUser(){
        Log.w(TAG, "deactivateUser");
        try {
            mFirestoreService.deactivateUser(mUser.getUid());
        }catch (Exception e){
            Log.w(TAG, "deactivateUser.exception: ", e);
        }
    }

    private void setUser(){
        Log.w(TAG, "setUser");
        //on va checker si est deja logue et on set les infos ou pas du UserModel
        if(mAuthService.isSignedIn()){
            //on creer le user de base
            mUser = new UserModel(mAuthService.getUserLoginName(), mAuthService.getUserID());
            //on va chercher les infos du user ou on les creer
            getUserInfos();
            //on active le user dans la liste des users actifs
            activateUser();
        }
    }

    private void resetUser(){
        Log.w(TAG, "resetUser");
        // on enleve de la table actif avant
        deactivateUser();
        // et on enleve du service une fois que l'on sait qu'il est retire de la table active
        // sinon ne sera plus logue alors plus capable de faire un delete vu qu'il faut etre logiue
        // et aussi car une fois relogue c'est la qu'il va recevoir le retour du
        // deactivateUser.addOnSuccessListener
        // mais on va le faire avec un script cote serveur si n'est pas actif depuix X temps
        // alors il le delete
        // ou un trigger avec CloudFunction sur le firebaseAuthentification
        mAuthService.signOut();
        mUser = null;
    }

    private void keepActive(){
        Log.w(TAG, "keepActive");
        //TODO: cronjob sur le serveur pour changer le active state en cas de trop long
        // garde le user active dans la DB, sinon sera reseter par le cronjob du serveur
        // si le updateTime est trop long de 5 minutes
        if(mAuthService.isSignedIn()){
            try {
                mFirestoreService.updateUserInfos(mUser);
            }catch (Exception e){
                Log.w(TAG, "activateUser.exception: ", e);
            }
        }

    }



    //--------------------------------------------------------------------------------------------



    //TODO: pas oublier de faire rouler dans le meme process
    private final IBinder mTrackerBinder = new TrackerBinder();

    public class TrackerBinder extends Binder implements ITrackerBinder {

        @Override
        public long getCounter() {
            Log.w(TAG, "TrackerBinder.getCounter");
            return mTimer;
        }

        @Override
        public UserModel getUser() {
            Log.w(TAG, "TrackerBinder.getUID");
            return mUser;
        }

        @Override
        public void registerCallback(ITrackerBinderCallback callback){
            Log.w(TAG, "TrackerBinder.registerCallback");
            mBinderCallback = callback;
            //si il fait un rebind() en faisant un back dans l'application
            //il ne passera pas par unbind() alors il faut stoper le thread precedent
            //avant de le repartir, sinon il y aura autant de thread que de call
            //a registerCallback() sans unregisterCallback()
            //tant que le callstack n'est pas cleare quand il passe d'une activity a l'autre
            untrackCounter();
            trackCounter();
        }

        @Override
        public void unregisterCallback(){
            Log.w(TAG, "TrackerBinder.unregisterCallback");
            untrackCounter();
            mBinderCallback = null;
        }

        @Override
        public boolean isSignedIn(){
            Log.w(TAG, "TrackerBinder.isSignedIn");
            return mAuthService.isSignedIn();
        }

        @Override
        public void signOut(){
            Log.w(TAG, "TrackerBinder.signOut");
            //on reset le user
            resetUser();
        }

        @Override
        public void signIn(String email, String psw){
            Log.w(TAG, "TrackerBinder.signIn[0]");
            mAuthService.signInUser(email, psw);
        }

        @Override
        public void signIn(AuthCredential authCredential){
            Log.w(TAG, "TrackerBinder.signIn[1]");
            mAuthService.signInUser(authCredential);
        }

        @Override
        public void createUser(String email, String psw){
            Log.w(TAG, "TrackerBinder.createUser");
            mAuthService.createUser(email, psw);
        }

        @Override
        public void onSignedIn(Object obj){
            Log.w(TAG, "TrackerBinder.onSignedIn");
            //on set le user de base du service
            setUser();
            //on tranmet la reponse object au caller
            mBinderCallback.onSignedIn(obj);
        }


        @Override
        public void onSignedOut(Object obj){
            // va etre caller par firestoreData par le listener du deactivate
            Log.w(TAG, "TrackerBinder.onSignedOut");
            //on tranmet la reponse object au caller
            mBinderCallback.onSignedOut(obj);
        }




    }
}
