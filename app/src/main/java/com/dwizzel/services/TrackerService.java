package com.dwizzel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.dwizzel.auth.AuthService;
import com.facebook.AccessToken;
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
 */

public class TrackerService extends Service{

    private final static String TAG = "TheKids.TrackerService";
    private Thread mThTimer;
    private static long mTimer = 0;

    private AuthService mAuthService;

    @NonNull
    public static Intent getIntent(Context context) {
        Log.w(TAG, "getIntent");
        return new Intent(context, TrackerService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand: " + intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "onBind:" + intent);
        return mBinder;
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
            mAuthService = new AuthService(getApplicationContext());
            startTimer();
        }catch(Exception e){
            Log.w(TAG, "onCreate.exception: ", e);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.w(TAG, "onUnbind:" + intent);
        //si clear le callback
        try {
            ((TrackerService.TrackerBinder) mBinder).unregisterCallback();
        }catch (Exception e){
            Log.w(TAG, "onUnbind.Exception: ", e);
        }
        //TODO: check the difference avec true ou false
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

    private void startTimer() {
        Log.w(TAG, "startTimer");
        if(mThTimer == null) {
            try {
                mThTimer = new Thread(new Runnable() {

                    private final static String TAG = "TheKids.mThTimer";

                    @Override
                    public void run() {
                        try {
                            while (true) {
                                Thread.sleep(1000);
                                mTimer++;
                                /*
                                Log.w(TAG, String.format("run.counter[%s] -> %s",
                                        getTimer(),
                                        mAuthService.getUserLoginName()));
                                       */
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



    //--------------------------------------------------------------------------------------------



    //TODO: pas oublier de faire rouler dans le meme process
    private final IBinder mBinder = new TrackerBinder();

    public class TrackerBinder extends Binder implements ITrackerBinder {

        private Thread mThCounter;
        private ITrackerBinderCallback mBinderCallback;

        @Override
        public long getCounter() {
            Log.w(TAG, "TrackerBinder.getCounter");
            return mTimer;
        }

        @Override
        public String getUserLoginName() {
            Log.w(TAG, "TrackerBinder.getUserLoginName");
            return mAuthService.getUserLoginName();
        }

        @Override
        public String getUserID() {
            Log.w(TAG, "TrackerBinder.getUID");
            return mAuthService.getUserID();
        }

        @Override
        public void registerCallback(ITrackerBinderCallback callback){
            Log.w(TAG, "TrackerBinder.registerCallback");
            mBinderCallback = callback;
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
            return mAuthService.isSignedIn();
        }

        @Override
        public void signOut(){
            mAuthService.signOut();
        }

        @Override
        public void signIn(String email, String psw){
            mAuthService.signInUser(this, email, psw);
        }

        @Override
        public void signIn(AuthCredential authCredential){
            mAuthService.signInUser(this, authCredential);
        }

        @Override
        public void createUser(String email, String psw){
            mAuthService.createUser(this, email, psw);
        }

        @Override
        public void onSignedIn(Object obj){
            //on tranmet la reponse object au caller
            mBinderCallback.onSignedIn(obj);
        }

        @Override
        public void onSignedOut(Object obj){
            //on tranmet la reponse object au caller
            mBinderCallback.onSignedOut(obj);
        }

        private void untrackCounter(){
            Log.w(TAG, "TrackerBinder.untrackCounter");
            try{
                mThCounter.interrupt();
                mThCounter = null;

            }catch (Exception e){
                Log.w(TAG, "untrackCounter.Exception: ", e);
            }
        }

        private void trackCounter(){
            Log.w(TAG, "TrackerBinder.trackCounter");
            try {
                mThCounter = new Thread(new Runnable() {

                    private final static String TAG = "TheKids.mThCounter";

                    @Override
                    public void run() {
                        try {
                            while (true) {
                                //callback every 10 seconds
                                Thread.sleep(10000);
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

    }
}
