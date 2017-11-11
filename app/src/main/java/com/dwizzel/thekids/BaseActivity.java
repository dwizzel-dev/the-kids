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


import com.dwizzel.services.TrackerService;
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

    protected ITrackerService mTrackerService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAGBASE, "onServiceConnected");
            mServiceBound = true;
            mTrackerService = ITrackerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAGBASE, "onServiceDisconnected");
            mTrackerService = null;
            mServiceBound = false;
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
        Intent intent = new Intent(this, TrackerService.class);
        intent.setAction(TrackerService.class.getName());
        //si on le start pas il va s'arreter avec l'application et ne sera pas reparti par le systeme
        startService(intent);
        //bind sur le sevice
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
        Log.w(TAGBASE, "onDestroy[0]");
        super.onDestroy();
        if(mTrackerService!= null){
            unbindService(mConnection);
            Log.w(TAGBASE, "onDestroy.unbindService");
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
            setServiceStatus(false);
            bFetchUserData = true;
            //le login page
            Intent intent = new Intent(this, LoginActivity.class);
            //start activity de login car pas encore logue
            //start activity and clear the backStack
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

    private void setServiceStatus(boolean bLogued){
        //le binder
        /*
        if(mBinder != null && mServiceBound) {
            mBinder.setLoguedStatus(bLogued);
        }
        */


    }

    protected void signOutUser(){
        Log.w(TAGBASE, "signOutUser");
        if(sAuth != null){
            sAuth.signOut();
        }
        //le service
        setServiceStatus(false);
        //
        Utils utils = Utils.getInstance();
        utils.showToastMsg(this, R.string.toast_signed_out);
        //on reload l'activity dans laquelle il est,
        //qui va checker si est logue ou pas
        recreate();
    }


    /**
     * This implementation is used to receive callbacks from the remote
     * service.
     */
    /*
    private ITrackerServiceCallback mCallback = new ITrackerServiceCallback.Stub() {
        public void valueChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0));
        }
    };
    */

}
