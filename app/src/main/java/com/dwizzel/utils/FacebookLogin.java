package com.dwizzel.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.dwizzel.thekids.ObserverObject;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.dwizzel.thekids.R;
import com.dwizzel.Const;

import java.util.Observable;


/**
 * Created by Dwizzel on 06/11/2017.
 * https://firebase.googleblog.com/2016/09/become-a-firebase-taskmaster-part-3_29.html
 * https://thekids-dab99.firebaseapp.com/__/auth/handler
 */

class FacebookLogin extends Observable {

    private static final String TAG = "TheKids.FacebookLogin";

    private CallbackManager mFacebookCallbackManager;
    private ProfileTracker mProfileTracker;
    private Activity mActivity;

    /*
    protected boolean isSignedIn(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        //
        if(accessToken != null){
            Log.w(TAG, "isSignedIn: true");
            return true;
        }
        Log.w(TAG, "isSignedIn: false");
        return false;
    }
    */

    protected void logOut(){
        Log.d(TAG, "logOut");

        LoginManager.getInstance().logOut();
    }

    protected void notifyParent(int type, AccessToken token){

        Log.d(TAG, "notifyParent:[" + type + "] " + token);

        /**
         * Il y a 2 notifications
         * 1. quand le login est un success Const.TYPE_NOTIF_LOGIN
         * 2. quand on recoit l'infos de l'usager Const.TYPE_NOTIF_PROFILE
         * 3. Une erreur si on a besoin de handler quelque chose
         */

        setChanged();

        switch (type){
            case Const.notif.TYPE_NOTIF_LOGIN:
                notifyObservers(new ObserverObject(type, token, mActivity));
                break;
            case Const.notif.TYPE_NOTIF_PROFILE:
                notifyObservers(new ObserverObject(type, token, mActivity));
                break;
            default:
                notifyObservers(new ObserverObject(type, token, mActivity));
                break;
        }

    }

    private void handleFacebookResult(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken: " + accessToken);
        //on va setter le firebase
        notifyParent(Const.notif.TYPE_NOTIF_LOGIN, accessToken);
        /*
        //vu que l'on est avec firebase c'est google qui va communiquer avec facebook
        //pour connaitre le email
        //on cherche les infos du user
        Profile profile = Profile.getCurrentProfile();
        if(profile == null){
            Log.v(TAG, "ProfileTracker()");
            mProfileTracker = new ProfileTracker(){
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile,
                                                       Profile currentProfile) {
                    Log.v(TAG, "onCurrentProfileChanged()");
                    mProfileTracker.stopTracking();
                    Profile.setCurrentProfile(currentProfile);
                    //on load seuleement une fois l'info recu
                    notifyParent(Const.TYPE_NOTIF_PROFILE, null);
                }
            };
        }else{
            Log.v(TAG, String.format("handleFacebookAccessToken.getFirstName(): %s", profile.getFirstName()));
        }
        */
    }

    /*
    protected String getUserLoginName(){
        Profile profile = Profile.getCurrentProfile();
        if(profile != null){
            Log.w(TAG, "getUserLoginName: facebook[0]");
                return profile.getName();
            }
        Log.w(TAG, "getUserLoginName: facebook[1]");
        return "...";
    }
    */

    protected void facebookCallBackManager(int requestCode, int resultCode, Intent data){
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void disableFacebookButton(){
        LoginButton loginButton = mActivity.findViewById(R.id.facebook_button);
        loginButton.setVisibility(View.INVISIBLE);
    }

    protected void setFacebookLogin(Activity activity){
        Log.w(TAG, "setFacebookLogin");
        //
        mActivity = activity;
        //facebook
        LoginButton loginButton = mActivity.findViewById(R.id.facebook_button);
        //pour avoir au moins le nom
        loginButton.setReadPermissions("public_profile", "email");
        //facebookCallBack
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult){
                        Log.w(TAG, "mFacebookCallbackManager.onSuccess");
                        handleFacebookResult(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        Log.w(TAG, "mFacebookCallbackManager.onCancel");
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Log.w(TAG, "mFacebookCallbackManager.onError");
                    }

                });



    }


}
