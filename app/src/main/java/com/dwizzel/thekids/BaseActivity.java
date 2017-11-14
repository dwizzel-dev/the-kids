package com.dwizzel.thekids;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dwizzel.services.*;

import com.dwizzel.utils.Auth;
import com.dwizzel.utils.Utils;

/**
 * Created by Dwizzel on 30/10/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAGBASE = "TheKids.BaseActivity";
    private static Auth sAuth;
    protected static Utils sUtils;
    private static boolean bFetchUserData = true;
    boolean mServiceBound;
    public TrackerService.TrackerBinder mTrackerBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAGBASE, "onServiceConnected");
            mServiceBound = true;

            mTrackerBinder = (TrackerService.TrackerBinder)service;
            mTrackerBinder.registerCallback(mServiceCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAGBASE, "onServiceDisconnected");
            mServiceBound = false;
            mTrackerBinder = null;
        }
    };

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {
        public void handleResponse(long counter){
            Log.d(TAGBASE, String.format("thread counter: %d", counter));
        }
    };

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Log.w(TAGBASE, "onPostCreate");
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAGBASE, "onCreate");
        super.onCreate(savedInstanceState);
        //le minimum requis besoi partout
        if (sAuth == null) {
            sAuth = Auth.getInstance();
        }
        if (sUtils== null) {
            sUtils = Utils.getInstance();
        }
        //start le service de base, sinon il va s'arreter des que l'apli est ferme
        Intent intent = TrackerService.getIntent(this);
        startService(intent);
        //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        Log.w(TAGBASE, "onStart");
        super.onStart();
        checkIfLoguedIn();
    }

    @Override
    protected void onDestroy(){
        Log.w(TAGBASE, "onDestroy");
        super.onDestroy();
        //clear le binder
        if(mTrackerBinder != null) {
            unbindService(mConnection);
            mServiceCallback = null;
        }
    }

    @Override
    protected void onStop() {
        Log.w(TAGBASE, "onStop");
        super.onStop();
    }

    private void checkIfLoguedIn(){
        //TODO: trouver un moyen pour ne pas qu'il restart le checkUserInfos() du startMainActivity()
        Log.w(TAGBASE, "checkIfLoguedIn");
        if (sAuth == null) {
            sAuth = Auth.getInstance();
        }
        if(!sAuth.isSignedIn()) {
            //on dit au service que l'on est pas signe
            bFetchUserData = true;
            //le login page
            Intent intent = new Intent(this, LoginActivity.class);
            //start activity de login car pas encore logue, clear le backStack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            //sinon repart toujours l'acitivity
            startMainActivity(bFetchUserData);
            if(bFetchUserData) {
                bFetchUserData = false;
            }
        }
    }

    protected abstract void startMainActivity(boolean isChecked);

    protected void signOutUser(){
        Log.w(TAGBASE, "signOutUser");
        if(sAuth != null){
            sAuth.signOut();
        }
        Utils utils = Utils.getInstance();
        utils.showToastMsg(this, R.string.toast_signed_out);
        //on reload l'activity dans laquelle il est, qui check si est logue ou pas
        recreate();
    }



}
