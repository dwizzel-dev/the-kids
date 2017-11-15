package com.dwizzel.thekids;

/***
 * https://dzone.com/articles/managing-multiple-ui-layouts
 * https://stackoverflow.com/questions/4817900/android-fragments-and-animation
 * https://developers.facebook.com/apps/135994336994028/fb-login/quickstart/
 * https://developers.facebook.com/docs/facebook-login/android
 * */


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.dwizzel.Const;
import com.dwizzel.models.CommunicationObject;
import com.dwizzel.observers.BooleanObserver;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;

/**
 * Created by Dwizzel on 09/11/2017.
 */

public class FacebookLoginActivity extends AppCompatActivity {

    private final static String TAG = "TheKids.FacebookLoginAc";
    private CallbackManager mFacebookCallbackManager;
    private BooleanObserver mServiceBoundObservable = new BooleanObserver(false);
    public TrackerService.TrackerBinder mTrackerBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAG, "onServiceConnected");
            mTrackerBinder = (TrackerService.TrackerBinder)service;
            mTrackerBinder.registerCallback(mServiceCallback);
            mServiceBoundObservable.set(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected");
            mServiceBoundObservable.set(false);
            mTrackerBinder = null;
        }
    };

    public TrackerService.TrackerBinder getTrackerBinder(){
        return mTrackerBinder;
    }

    private void bindToAuthService(){
        if(!mServiceBoundObservable.get()) {
            Intent intent = TrackerService.getIntent(this);
            startService(intent);
            //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {

        private static final String TAG = "TheKids.ITrackerBinder";

        @Override
        public void handleResponse(long counter){
            //Log.d(TAG, String.format("thread counter: %d", counter));
        }
        @Override
        public void onSignedIn(Object obj){
            Log.d(TAG, "onSignedIn");
            //on enleve le loader
            Utils.getInstance().hideProgressDialog();
            //check les erreurs et exception
            int err = ((CommunicationObject.ServiceResponseObject)obj).getErr();
            switch(err){
                case Const.except.NO_CONNECTION:
                    Utils.getInstance().showToastMsg(FacebookLoginActivity.this,
                            R.string.err_no_connectivity);
                    break;
                case Const.error.NO_ERROR:
                    userIsSignedInRoutine();
                    break;
                default:
                    break;
            }
        }
        @Override
        public void onSignedOut(Object obj){
            Log.d(TAG, "onSignedOut");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        bindToAuthService();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //reset
        mServiceCallback = null;
        mServiceBoundObservable.set(false);
        //clear le binder
        if(mTrackerBinder != null) {
            unbindService(mConnection);
            mConnection = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //to receive the result from the facebook callback
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void setFacebookLogin(){
        Log.w(TAG, "setFacebookLogin");
        //facebook
        LoginButton loginButton = findViewById(R.id.facebook_button);
        loginButton.setReadPermissions("public_profile", "email");
        //facebookCallBack
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult){
                        Log.w(TAG, "mFacebookCallbackManager.onSuccess");
                        signInUser(FacebookAuthProvider.getCredential(
                                loginResult.getAccessToken().getToken()));
                    }
                    @Override
                    public void onCancel() {
                        Log.w(TAG, "mFacebookCallbackManager.onCancel");
                    }
                    @Override
                    public void onError(FacebookException e) {
                        Log.w(TAG, "mFacebookCallbackManager.execption:", e);
                    }
                });
    }

    private void signInUser(AuthCredential authCredential) {
        //on va faire un listener sur le resultat
        if (mTrackerBinder != null) {
            //on met un loader
            Utils.getInstance().showProgressDialog(this);
            //on call le service
            mTrackerBinder.signIn(authCredential);
        }
    }

    private void userIsSignedInRoutine(){
        //on affiche qu'il est logue
        Utils.getInstance().showToastMsg(FacebookLoginActivity.this,
                getResources().getString(R.string.toast_connected_as,
                        mTrackerBinder.getUserLoginName()));
        //on va a activity principal
        Intent intent = new Intent(FacebookLoginActivity.this,
                HomeActivity.class);
        //start activity and clear the backStack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



}
