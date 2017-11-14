package com.dwizzel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/*
import com.dwizzel.thekids.ITrackerService;
import com.dwizzel.thekids.ITrackerServiceCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
*/

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

    private final static String TAG = "TheKids.Tracker";
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
        Log.w(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "onBind");
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
        Log.w(TAG, "onUnbind");
        //si le mem intent alors on le reset
        // All clients have unbound with unbindService()
        ((TrackerService.TrackerBinder) mBinder).stopTrackingCounter();
        return true; //or false haha!
    }

    @Override
    public void onRebind(Intent intent) {
        Log.w(TAG, "onRebind");
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    private void startTimer() {
        Log.w(TAG, "startTimer");
        if(mThTimer == null) {
            try {
                mThTimer = new Thread(new Runnable() {

                    private final static String TAG = "mThTimer";

                    @Override
                    public void run() {
                        try {
                            while (true) {
                                Thread.sleep(1000);
                                mTimer++;
                                Log.w(TAG, "run.counter[" + mTimer + " seconds][" + mServiceAuth.isSignedIn() + "] -> " + mServiceAuth.getUserLoginName());
                            }
                        } catch (InterruptedException ie) {
                            Log.w(TAG, "run.InterruptedException: ", ie);
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

    public class TrackerBinder extends Binder {

        private Thread mThCounter;

        public TrackerService getService(){
            return TrackerService.this;
        }

        public long getCounter() {
            return mTimer;
        }

        public void stopTrackingCounter(){
            if(mThCounter != null) {
                mThCounter.interrupt();
                mThCounter = null;
            }
        }

        //TODO: stop the counter when the callback is not there anymore
        public void trackCounter(final TrackerServiceCallback callback){
            try {
                mThCounter = new Thread(new Runnable() {

                    private final static String TAG = "mThCounter";

                    @Override
                    public void run() {
                        try {
                            while (true) {
                                //callback every 10 seconds
                                Thread.sleep(5000);
                                try {
                                    callback.handleResponse(mTimer);
                                } catch (Exception e) {
                                    Log.w(TAG, "run.Exception: ", e);
                                }finally {
                                    //Thread.currentThread().interrupt();
                                }
                            }
                        } catch (InterruptedException ie) {
                            Log.w(TAG, "run.InterruptedException: ", ie);
                        }
                    }
                });
                mThCounter.start();
            }catch (Exception e){
                Log.w(TAG, "trackCounter.Exception: ", e);
            }
        }

    }

    /*
    //avec le AIDL
    private final ITrackerService.Stub mBinder = new ITrackerService.Stub() {

        private final static String TAG = "TheKids.ITrackerService";
        private Thread mThCounter;

        public long getCounter(){
            return mTimer;
        }

        public boolean isSignedIn(){
            return mServiceAuth.isSignedIn();
        }

        public void untrackCounter(){
            if(mThCounter != null) {
                mThCounter.interrupt();
            }
        }

        public void trackCounter(final ITrackerServiceCallback callback) throws RemoteException{
            try {
                mThCounter = new Thread(new Runnable() {

                    private final static String TAG = "mThCounter";

                    @Override
                    public void run() {
                        try {
                            while (true) {
                                //callback every 10 seconds
                                Thread.sleep(10000);
                                try {
                                    callback.handleResponse(mTimer);
                                } catch (RemoteException re) {
                                    Log.w(TAG, "run.RemoteException: ", re);
                                } catch (Exception e) {
                                    Log.w(TAG, "run.Exception: ", e);
                                }finally {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        } catch (InterruptedException ie) {
                            Log.w(TAG, "run.InterruptedException: ", ie);
                        }
                    }
                });
                mThCounter.start();
            }catch (Exception e){
                Log.w(TAG, "trackCounter.Exception: ", e);
            }
        }

        public void signInUser(ITrackerServiceCallback callback, String email, String psw){

        }

    };
    */

}
