package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Auth {

    private static Auth sInst = null;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private AccessToken mFacebookAccessToken;
    private Profile mFacebookProfile;
    private Utils mUtils;
    private static final String TAG = "THEKIDS::";


    private Auth() {
        // Required empty public constructor
        if(mUtils == null) {
            mUtils = Utils.getInstance();
        }
    }

    public static synchronized Auth getInstance() {
        if(sInst  == null) {
            synchronized (Auth.class) {
                sInst  = new Auth();
            }
        }
        return sInst;
    }

    /*
    public FirebaseUser getUserInfos(){
        return mFirebaseUser;
    }
    */

    public String getUserLoginName(){
        if(mFirebaseUser != null) {
            //si firebase user
            Log.w(TAG, "getUserLoginName::firebase");
            return mFirebaseUser.getEmail();
        }else{
            //peut-etre un facebook login
            Log.w(TAG, "getUserLoginName::facebook[0]");
            mFacebookProfile = Profile.getCurrentProfile();
            if(mFacebookProfile != null){
                Log.w(TAG, "getUserLoginName::facebook[1]");
                return mFacebookProfile.getName();
            }
        }
        return "...";
    }

    private void initFirebaseAuth(){
        if(mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
    }

    public boolean isSignedIn(){
        //check via le firebase si on est logue ou pas
        Log.w(TAG, "isSignedIn::firebase");
        if(mFirebaseAuth != null) {
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if(mFirebaseUser != null) {
                Log.w(TAG, "isSignedIn::firebase");
                return true;
            }
        }
        //sinon on check via le facebook
        mFacebookAccessToken = AccessToken.getCurrentAccessToken();
        if(mFacebookAccessToken != null){
            Log.w(TAG, "isSignedIn::facebook");
            return true;
        }
        //
        Log.w(TAG, "isSignedIn::FALSE");
        return false;
    }

    public void signOut(){
        //TODO: https://firebase.google.com/docs/auth/android/facebook-login
        //
        //check si un firebase user
        //attention si on fait un signIN/createUser/signInUser
        //avec firebase alors on instencie
        //et un apres avec facebook
        //le mFirebaseAuth ne sera pas null
        //alors ne delogue pas celui de facebook

        if(mFirebaseAuth != null){
            try{
                mFirebaseAuth.signOut();
                mFirebaseUser = null;
            }catch(Exception e){
                //
            }
        }else{
            //check si un facebook user
            LoginManager.getInstance().logOut();

        }
    }

    public Task<AuthResult> createUser(Activity activity, String email, String psw) throws Exception {

        Log.w(TAG, String.format("AUTH::createUser[%s | %s]", email, psw));

        //instanciate
        initFirebaseAuth();

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = mUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if(!isConnected) {
                throw new Exception("no internet connection");
            }else {
                return mFirebaseAuth.createUserWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.w(TAG, "AUTH_CREATE::onComplete[000]");
                                if (task.isSuccessful()) {
                                    //on va chercher les infos du user
                                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                } else {
                                    Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                                }
                            }
                        });
            }
        }catch(Exception e){
            Log.w(TAG, "AUTH_CREATE::EXCEPTION[000]");
            throw new Exception("no internet connection");
        }

    }

    public Task<AuthResult> signInUser(Activity activity, String email, String psw) throws Exception {

        Log.w(TAG, String.format("AUTH::signInUser[%s | %s]", email, psw));

        //instanciate
        initFirebaseAuth();

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = mUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if (!isConnected) {
                throw new Exception("no internet connection");
            }else{
                return mFirebaseAuth.signInWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.w(TAG, "AUTH_SIGNIN::onComplete[000]");
                                if (task.isSuccessful()) {
                                    //on va chercher les infos du user
                                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                } else {
                                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());
                                }
                            }
                        });
            }
        }catch(Exception e){
            Log.w(TAG, "AUTH_SIGNIN::EXCEPTION[000]");
            throw new Exception("no internet connection");
            }


    }



}
