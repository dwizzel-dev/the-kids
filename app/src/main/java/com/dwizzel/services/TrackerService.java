package com.dwizzel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dwizzel.thekids.ITrackerService;
import com.dwizzel.thekids.ITrackerServiceCallback;

//import com.dwizzel.thekids.ITrackerService;
//import com.dwizzel.thekids.ITrackerServiceCallback;



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
                            Thread.sleep(1000);
                            mTimer++;
                            //mBinder.trackCounter();

                            Log.w(TAG, "run.counter[" + mTimer + "]: " + mLoguedIn);
                        }
                    }catch (InterruptedException ie){
                        Log.w(TAG, "run.exception: ", ie);
                    }
                }
            });
            mThread.start();
        }
    }


    // TESTING AIDL SERVICE WITH IPC call
    private final ITrackerService.Stub mBinder = new ITrackerService.Stub() {

        private final static String TAG = "TheKids.ITrackerService";
        private Thread mThCounter;
        private ITrackerServiceCallback mCallback = null;

        public long getCounter(){
            return mTimer;
        }
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) {
            // Does nothing
        }
        public void untrackCounter(final ITrackerServiceCallback callback){
            mThCounter.interrupt();
        }

        public void trackCounter(final ITrackerServiceCallback callback) throws RemoteException{

            mCallback = callback;

            mThCounter = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true) {
                            Thread.sleep(10000);
                            try {
                                mCallback.handleResponse(mTimer);
                            } catch (RemoteException re) {
                                Log.w(TAG, "run.handleResponse: ", re);
                            }
                        }
                    }catch (InterruptedException ie){
                        Log.w(TAG, "run.exception: ", ie);
                    }
                }
            });
            mThCounter.start();
        }

    };



}
