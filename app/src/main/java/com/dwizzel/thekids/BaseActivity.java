package com.dwizzel.thekids;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Auth;
import com.dwizzel.utils.Utils;

/**
 * Created by Dwizzel on 30/10/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG2 = "TheKids.BaseActivity";
    private static Auth sAuth;
    private static boolean bFetchUserData = true;
    boolean mServiceBound;
    //private TrackerService.TrackerBinder mBinder;

    ITrackerService mTrackerService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAG2, "onServiceConnected");
            mServiceBound = true;
            mTrackerService = ITrackerService.Stub.asInterface(service);
            /*
            mBinder = (TrackerService.TrackerBinder) service;
            //un fois connecte on set le service status
            setServiceStatus(sAuth.isSignedIn());
            */
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG2, "onServiceDisconnected");
            mTrackerService = null;
            mServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG2, "onCreate");
        super.onCreate(savedInstanceState);
        /*
        //start le service de base, sinon il va s'arreter des que l'apli est ferme
        startService(TrackerService.getIntent(this));
        //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
        bindService(TrackerService.getIntent(this),
                mConnection,
                Context.BIND_AUTO_CREATE);
        */
        Intent intent = new Intent(this, TrackerService.class);
        //intent.setAction(TrackerService.class.getName());
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        /*
        bindService(TrackerService.getIntent(this),
                mConnection,
                Context.BIND_AUTO_CREATE);
        */

    }




    @Override
    protected void onStart() {
        Log.w(TAG2, "onStart");
        super.onStart();
        checkIfLoguedIn();
    }

    @Override
    protected void onDestroy(){
        Log.w(TAG2, "onDestroy[0]");
        super.onDestroy();
        if(mTrackerService!= null){
            unbindService(mConnection);
            Log.w(TAG2, "onDestroy.unbindService");
        }
        /*
        //TODO: enlver pour garder le service vivant en prod
        if(mBinder != null) {
            unbindService(mConnection);
            Log.w(TAG2, "onDestroy.unbindService");
        }
        Log.w(TAG2, "onDestroy[1]");
        */

    }

    @Override
    protected void onStop() {
        Log.w(TAG2, "onStop");
        super.onStop();


    }

    private void checkIfLoguedIn(){
        //TODO: trouver un moyen pour ne pas qu'il restart le checkUserInfos() du startMainActivity()
        Log.w(TAG2, "checkIfLoguedIn");
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
        Log.w(TAG2, "signOutUser");
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

}
