package com.dwizzel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dwizzel.thekids.ITrackerService;


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
 */

public class TrackerService extends Service{

    private final static String TAG = "TheKids.Tracker";
    private Thread mThread;
    private static long mTimer = 0;
    private static boolean mLoguedIn = false;
    private boolean mAllowRebind;

    /*
    //depends on the AndroidManifest.xml
    //for local Binder when service run in the same process has the application

    private TrackerBinder mBinder = new TrackerBinder();

    public class TrackerBinder extends Binder {

        TrackerBinder(){
            Log.w(TAG, "TrackerBinder");
        }

        public void setLoguedStatus(boolean logued) {
            Log.w(TAG, "TrackerBinder.setLoguedStatus: " + logued);
            mLoguedIn = logued;
        }
    }
    */

    @NonNull
    public static Intent getIntent(Context context) {
        Log.w(TAG, "getIntent");
        return new Intent(context, TrackerService.class);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand");
        /*
        if(mTimer == 0) {
            startTimer();
        }
        */
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "onBind");
        if(mTimer == 0) {
            startTimer();
        }
        return mBinder;
    }

    @Override
    public void onDestroy(){
        Log.w(TAG, "onDestroy");
        super.onDestroy();

    }

    @Override
    public void onCreate(){
        Log.w(TAG, "onCreate");
        if(mTimer == 0) {
            startTimer();
        }

        super.onCreate();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.w(TAG, "onUnbind");
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        Log.w(TAG, "onRebind");
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    private void startTimer() {
        Log.w(TAG, "startTimer");
        if(mThread == null) {
            mThread = new Thread(new Runnable() {
                private final static String TAG = "TheKids.Thread";
                @Override
                public void run() {
                    try {
                        while (true) {
                            Thread.sleep(3000);
                            mTimer++;
                            Log.w(TAG, "Thread.counter[" + mTimer + "]: " + mLoguedIn);
                        }
                    }catch (InterruptedException ie){
                        Log.w(TAG, "Thread.run.exception: ", ie);
                    }
                }
            });
            mThread.start();
        }
    }

    // TESTING AIDL

    private final ITrackerService.Stub mBinder = new ITrackerService.Stub() {
        public int getPid(){
            return Process.myPid();
        }
        public void basicTypes(int anInt, long aLong, boolean aBoolean,
                               float aFloat, double aDouble, String aString) {
            // Does nothing
        }
    };



}
