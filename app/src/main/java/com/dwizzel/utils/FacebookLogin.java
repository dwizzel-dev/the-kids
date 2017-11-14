package com.dwizzel.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.dwizzel.models.CommunicationObject.NotifObjectObserver;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
    private Activity mActivity;

    protected void logOut(){
        Log.d(TAG, "logOut");
        LoginManager.getInstance().logOut();
    }

    protected void notifyParent(int type, AccessToken token){

        Log.d(TAG, "notifyParent:[" + type + "] " + token);

        setChanged();

        switch (type){
            case Const.notif.TYPE_NOTIF_LOGIN:
                notifyObservers(new NotifObjectObserver(type, token, mActivity));
                break;
            default:
                notifyObservers(new NotifObjectObserver(type, token, mActivity));
                break;
        }

    }

    protected void facebookCallBackManager(int requestCode, int resultCode, Intent data){
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                        notifyParent(Const.notif.TYPE_NOTIF_LOGIN, loginResult.getAccessToken());
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

}
