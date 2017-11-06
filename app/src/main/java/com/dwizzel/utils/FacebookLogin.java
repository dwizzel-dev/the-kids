package com.dwizzel.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.dwizzel.thekids.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Observable;
import java.util.concurrent.Callable;

/**
 * Created by Dwizzel on 06/11/2017.
 * https://firebase.googleblog.com/2016/09/become-a-firebase-taskmaster-part-3_29.html
 */

public class FacebookLogin extends Observable {

    private static final String TAG = "THEKIDS::";
    private AccessToken mFacebookAccessToken;
    private Profile mFacebookProfile;
    private CallbackManager mFacebookCallbackManager;
    private Utils mUtils;
    private Task mFacebookTask;

    public boolean isSignedIn(){
        mFacebookAccessToken = AccessToken.getCurrentAccessToken();
        if(mFacebookAccessToken != null){
            Log.w(TAG, "isSignedIn::true");
            return true;
        }
        Log.w(TAG, "isSignedIn::false");
        return false;
    }

    public void logOut(){
        LoginManager.getInstance().logOut();
    }

    public String getUserLoginName(){
        mFacebookProfile = Profile.getCurrentProfile();
        if(mFacebookProfile != null){
            Log.w(TAG, "getUserLoginName::facebook[0]");
                return mFacebookProfile.getName();
            }
        Log.w(TAG, "getUserLoginName::facebook[1]");
        return "...";
    }

    public void facebookCallBackManager(int requestCode, int resultCode, Intent data){
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setFacebookLogin(Activity activity){
        Log.w(TAG, "AUTH::setFacebookLogin()");
        //facebook
        LoginButton loginButton = activity.findViewById(R.id.facebook_button);
        //pour avoir au moins le nom
        loginButton.setReadPermissions("public_profile");
        //facebookCallBack
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {

                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult){
                        Log.w(TAG, "facebook.onSuccess");
                        Profile profile = Profile.getCurrentProfile();
                        if(profile == null){
                            Log.v(TAG, "facebook.ProfileTracker()");
                            mProfileTracker = new ProfileTracker(){
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile,
                                                                       Profile currentProfile) {
                                    Log.v(TAG, "facebook.onCurrentProfileChanged()");
                                    mProfileTracker.stopTracking();
                                    Profile.setCurrentProfile(currentProfile);
                                    //on load seuleement une fois l'info recu
                                    setChanged();
                                    notifyObservers("successfull login[0]");
                                }
                            };
                        }else{
                            Log.v(TAG, String.format("facebook.getFirstName(): %s", profile.getFirstName()));
                        }

                    }
                    @Override
                    public void onCancel() {
                        Log.w(TAG, "facebook.onCancel");
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Log.w(TAG, "facebook.onError");
                    }

                });



    }


}
