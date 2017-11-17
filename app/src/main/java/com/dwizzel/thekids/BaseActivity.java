package com.dwizzel.thekids;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.dwizzel.models.UserModel;
import com.dwizzel.objects.UserObject;
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
    private UserObject mUser;
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
            Intent intent = TrackerService.getIntent(this);
            startService(intent);
            //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {
        private static final String TAG = "BaseActivity.ITrackerBinder";
        public void handleResponse(long counter){
            Tracer.log(TAG, String.format("thread counter: %d", counter));
        }
        public void onSignedIn(Object obj){
            Tracer.log(TAG, "onSignedIn");
        }
        public void onSignedOut(Object obj){
            Tracer.log(TAG, "onSignedOut");
        }
        public void onGpsEnabled(Object obj){
            Tracer.log(TAG, "onGpsEnabled");
        }
        public void onGpsDisabled(Object obj){
            Tracer.log(TAG, "onGpsDisabled");
        }
        public void onGpsEnable(Object obj){
            Tracer.log(TAG, "onGpsEnable");
        }
        public void onGpsDisable(Object obj){
            Tracer.log(TAG, "onGpsDisable");
        }
        public void onGpsUpdate(Object obj){
            Tracer.log(TAG, "onGpsUpdate");
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

    public UserObject getUser(){
        //accessible via les classes qui extend celle-ci
        return mUser;
    }

    protected void startActivity(){
        Tracer.log(TAGBASE, "startActivity");
    }

    private void checkIfSignedIn(){
        Tracer.log(TAGBASE, "checkIfSignedIn");

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
                //car ceux qui extends cette class vont l'utiliser
                // pour faire des call a FirestoreService
                if(mUser == null){
                    mUser = mTrackerBinder.getUser();
                    }
                //sinon repart toujours l'activity de la classe qui extends BaseActivity
                //si on est la c'est quand on redonne le focus a l'application
                startActivity();
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
        //show le msg
        Utils.getInstance().showToastMsg(this, R.string.toast_signed_out);
        //on reload l'activity dans laquelle il est, qui check si va checker si logue
        recreate();
    }

}
