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
import com.dwizzel.Const;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.observers.BooleanObserver;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;

public abstract class AFacebookLoginActivity extends ACommonSignInActivity {

    private final static String TAG = "AFacebookLoginActivity";
    private CallbackManager mFacebookCallbackManager;
    private BooleanObserver mServiceBoundObservable = new BooleanObserver(false);
    public TrackerService.TrackerBinder mTrackerBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Tracer.log(TAG, "onServiceConnected");
            mTrackerBinder = (TrackerService.TrackerBinder)service;
            mTrackerBinder.registerCallback(mServiceCallback);
            mServiceBoundObservable.set(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Tracer.log(TAG, "onServiceDisconnected");
            mServiceBoundObservable.set(false);
            mTrackerBinder = null;
        }
    };

    private void bindToAuthService(){
        if(!mServiceBoundObservable.get()) {
            Intent intent = TrackerService.getIntent(AFacebookLoginActivity.this);
            startService(intent);
            //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {
        private static final String TAG = "FacebookLoginActivity.ITrackerBinder";
        public void handleResponse(ServiceResponseObject sro){
            Tracer.log(TAG, "handleResponse", sro);
            //only handle the signin
            if(sro.getErr() == Const.error.NO_ERROR){
                switch(sro.getMsg()){
                    case Const.response.ON_USER_SIGNIN:
                        break;
                    case Const.response.ON_USER_CREATED:
                        userIsCreated();
                        break;
                    default:
                        break;
                }
            }else {
                //on enleve le loader
                Utils.getInstance().hideProgressDialog();
                //check les erreurs et exception
                switch(sro.getErr()){
                    case Const.except.NO_CONNECTION:
                        Utils.getInstance().showToastMsg(AFacebookLoginActivity.this,
                                R.string.err_no_connectivity);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Tracer.log(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        bindToAuthService();
    }

    @Override
    public void onDestroy(){
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //to receive the result from the facebook callback
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void setFacebookLogin(){
        Tracer.log(TAG, "setFacebookLogin");
        //facebook
        LoginButton loginButton = findViewById(R.id.facebook_button);
        loginButton.setReadPermissions("public_profile", "email");
        //facebookCallBack
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult){
                        Tracer.log(TAG, "mFacebookCallbackManager.onSuccess");
                        signInUser(FacebookAuthProvider.getCredential(
                                loginResult.getAccessToken().getToken()));
                    }
                    @Override
                    public void onCancel() {
                        Tracer.log(TAG, "mFacebookCallbackManager.onCancel");
                    }
                    @Override
                    public void onError(FacebookException e) {
                        Tracer.log(TAG, "mFacebookCallbackManager.execption:", e);
                    }
                });
    }

    private void signInUser(AuthCredential authCredential) {
        //on va faire un listener sur le resultat
        if (mTrackerBinder != null) {
            //on met un loader
            Utils.getInstance().showProgressDialog(AFacebookLoginActivity.this);
            //on call le service
            mTrackerBinder.signIn(authCredential);
        }
    }
    /*
    private void userIsCreated(){
        //on affiche qu'il est logue
        try {
            Utils.getInstance().showToastMsg(
                    AFacebookLoginActivity.this,
                    getResources().getString(R.string.toast_connected_as_and_last,
                            UserObject.getInstance().getEmail(),
                            UserObject.getInstance().getLastConnection(AFacebookLoginActivity.this)));
            //on va a activity principal
            Intent intent = new Intent(AFacebookLoginActivity.this,
                    HomeActivity.class);
            //start activity and clear the backStack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }catch (NullPointerException npe){
            Tracer.log(TAG, "userIsCreated.NullPointerException: " , npe);
        }catch (Exception e){
            Tracer.log(TAG, "userIsCreated.Exception: " , e);
        }
    }
    */


}
