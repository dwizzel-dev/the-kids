package com.dwizzel.thekids;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.observers.BooleanObserver;
import com.dwizzel.services.*;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dwizzel on 30/10/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAGBASE = "BaseActivity";
    private BooleanObserver mServiceBoundObservable = new BooleanObserver(false);
    public TrackerService.TrackerBinder mTrackerBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Tracer.log(TAGBASE, "onServiceConnected");
            mTrackerBinder = (TrackerService.TrackerBinder)service;
            mTrackerBinder.registerCallback(mServiceCallback);
            mServiceBoundObservable.set(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Tracer.log(TAGBASE, "onServiceDisconnected");
            mServiceBoundObservable.set(false);
            mTrackerBinder = null;
        }
    };

    public TrackerService.TrackerBinder getTrackerBinder(){
        return mTrackerBinder;
    }

    private void bindToAuthService(){
        //start le service de base, sinon il va s'arreter des que l'apli est ferme
        if(!mServiceBoundObservable.get()) {
            Intent intent = TrackerService.getIntent(BaseActivity.this);
            startService(intent);
            //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {
        private static final String TAG = "BaseActivity.ITrackerBinder";
        public void handleResponse(ServiceResponseObject sro){
            Tracer.log(TAG, String.format("handleResponse: %s", sro));
        }
        public void onSignedIn(ServiceResponseObject sro){
            Tracer.log(TAG, "onSignedIn");
        }
        public void onSignedOut(ServiceResponseObject sro){
            Tracer.log(TAG, "onSignedOut");
            //show le msg
            Utils.getInstance().showToastMsg(BaseActivity.this, R.string.toast_signed_out);
            startLoginActivity();
        }
    };

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Tracer.log(TAGBASE, "onPostCreate");
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tracer.log(TAGBASE, "onCreate");
        super.onCreate(savedInstanceState);
        bindToAuthService();
    }

    @Override
    protected void onStart() {
        Tracer.log(TAGBASE, "onStart");
        super.onStart();
        checkIfSignedIn();
    }

    @Override
    protected void onDestroy(){
        Tracer.log(TAGBASE, "onDestroy");
        super.onDestroy();
        //end l'activity qui instencie
        onSubDestroy();
        //clear le binder
        if(mTrackerBinder != null) {
            unbindService(mConnection);
            //reset
            mServiceCallback = null;
            mServiceBoundObservable.set(false);
            mConnection = null;
        }
    }

    @Override
    protected void onStop() {
        Tracer.log(TAGBASE, "onStop");
        super.onStop();
    }

    protected void onSubCreate(){
        Tracer.log(TAGBASE, "onSubCreate");
    }
    protected void onSubDestroy(){
        Tracer.log(TAGBASE, "onSubDestroy");
    }

    private void checkIfSignedIn(){
        Tracer.log(TAGBASE, "checkIfSignedIn");
        if( mTrackerBinder != null && mServiceBoundObservable.get()){
            if(!mTrackerBinder.isSignedIn()){
                startLoginActivity();
            }else{
                //sinon repart toujours l'activity de la classe qui extends BaseActivity
                //si on est la c'est quand on redonne le focus a l'application
                onSubCreate();
            }
        }else {
            Tracer.log(TAGBASE, "checkIfSigneddIn: service not bound yet");
            //on va mettre un observer dessus pour caller la method une fois connecte
            mServiceBoundObservable.addObserver(new Observer() {
                @Override
                public void update(Observable observable, Object o) {
                    Tracer.log(TAGBASE, "checkIfSigneddIn.mServiceBoundObservable.update: " + o);
                    //plus besoin d'etre observe
                    observable.deleteObserver(this);
                    //si ok on check
                    if((boolean)o){
                        checkIfSignedIn();
                    }
                }
            });
        }
    }

    protected void signOutUser(){
        Tracer.log(TAGBASE, "signOutUser");
        //on avretit le service que l'on sign out
        mTrackerBinder.signOut();

    }

    protected void startLoginActivity(){
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        //start activity de login car pas encore logue, clear le backStack aussi
        /*
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        */
        //TODO: a tester si c'est plus clean
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_TASK_ON_HOME
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();

    }

}
