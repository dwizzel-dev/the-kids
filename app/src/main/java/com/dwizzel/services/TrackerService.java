package com.dwizzel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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

    private com.dwizzel.auth.Service mServiceAuth;

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
        super.onCreate();
        Log.w(TAG, "onCreate");
        try {
            mServiceAuth = new com.dwizzel.auth.Service(getApplicationContext());
            //le timer for testing callBack alive
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
                                //Log.w(TAG, "run.counter[" + mTimer + "sec][" +
                                // mServiceAuth.isSignedIn() + "] -> " +
                                // mServiceAuth.getUserLoginName());
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


    //sans le AIDL
    //TODO: pas oublier de faire rouler dans le meme process
    private final IBinder mBinder = new TrackerBinder();

    public class TrackerBinder extends Binder implements ITrackerBinder {

        private Thread mThCounter;
        private ITrackerBinderCallback mBinderCallback;

        public long getCounter() {
            Log.w(TAG, "TrackerBinder.getCounter");
            return mTimer;
        }

        public void registerCallback(ITrackerBinderCallback callback){
            Log.w(TAG, "TrackerBinder.registerCallback");
            mBinderCallback = callback;
            trackCounter();
        }

        public void unregisterCallback(){
            Log.w(TAG, "TrackerBinder.unregisterCallback");
            untrackCounter();
            mBinderCallback = null;
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
                                Thread.sleep(5000);
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
