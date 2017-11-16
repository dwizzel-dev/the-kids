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
import com.dwizzel.observers.BooleanObserver;
import com.dwizzel.services.*;
import com.dwizzel.utils.Utils;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dwizzel on 30/10/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAGBASE = "TheKids.BaseActivity";
    private String mUsername;
    private String mUserId;
    private BooleanObserver mServiceBoundObservable = new BooleanObserver(false);
    public TrackerService.TrackerBinder mTrackerBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAGBASE, "onServiceConnected");
            mTrackerBinder = (TrackerService.TrackerBinder)service;
            mTrackerBinder.registerCallback(mServiceCallback);
            mServiceBoundObservable.set(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAGBASE, "onServiceDisconnected");
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
            Intent intent = TrackerService.getIntent(this);
            startService(intent);
            //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {
        private static final String TAG = "TheKids.ITrackerBinderCallback";
        @Override
        public void handleResponse(long counter){
            Log.d(TAG, String.format("thread counter: %d", counter));
        }
        @Override
        public void onSignedIn(Object obj){}
        @Override
        public void onSignedOut(Object obj){}
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
        bindToAuthService();
    }

    @Override
    protected void onStart() {
        Log.w(TAGBASE, "onStart");
        super.onStart();
        checkIfSignedIn();
    }

    @Override
    protected void onDestroy(){
        Log.w(TAGBASE, "onDestroy");
        super.onDestroy();
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
        Log.w(TAGBASE, "onStop");
        super.onStop();
    }

    public String getUsername(){
        return mUsername;
    }

    public String getUserId(){
        return mUserId;
    }

    protected void startActivity(){
        Log.w(TAGBASE, "startActivity");
    }

    private void checkIfSignedIn(){
        Log.w(TAGBASE, "checkIfSignedIn");

        //TODO: trouver un moyen pour ne pas qu'il restart le checkUserInfos() du startMainActivity()
        if( mTrackerBinder != null && mServiceBoundObservable.get()){
            if(!mTrackerBinder.isSignedIn()){
                //le login page
                Intent intent = new Intent(this, LoginActivity.class);
                //start activity de login car pas encore logue, clear le backStack aussi
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }else{
                //si les infos sur l'usager ne sont setter, comme dans un retour de sign In
                //car ceux qui extends cette class vont l'utiliser pour faire des call a firestoreDB
                if(mUsername == null){
                    mUsername = mTrackerBinder.getUserLoginName();
                }
                if(mUserId == null){
                    mUserId = mTrackerBinder.getUserID();
                }
                //sinon repart toujours l'activity de la classe qui extends BaseActivity
                //si on est la c'est quand on redonne le focus a l'application
                startActivity();

            }
        }else {
            Log.w(TAGBASE, "checkIfSigneddIn: service not bound yet");
            //on va mettre un observer dessus pour caller la method une fois connecte
            mServiceBoundObservable.addObserver(new Observer() {
                @Override
                public void update(Observable observable, Object o) {
                    Log.w(TAGBASE, "checkIfSigneddIn.mServiceBoundObservable.update: " + o);
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
        Log.w(TAGBASE, "signOutUser");
        //on avretit le service que l'on sign out
        mTrackerBinder.signOut();
        //show le msg
        Utils.getInstance().showToastMsg(this, R.string.toast_signed_out);
        //on reload l'activity dans laquelle il est, qui check si va checker si logue
        recreate();
    }

}
