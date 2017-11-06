package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Observable;
import java.util.Observer;

public class Auth extends Observable{

    private static Auth sInst = null;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Utils mUtils;
    private FacebookLogin mFacebookLogin;
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

    public String getUserLoginName(){
        if(mFirebaseUser != null) {
            //si firebase user
            Log.w(TAG, "getUserLoginName::firebase");
            return mFirebaseUser.getEmail();
        }else if(mFacebookLogin != null){
            //si facebook user
            return mFacebookLogin.getUserLoginName();
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
        }else{
            Log.w(TAG, "isSignedIn::firebase");
            if(mFacebookLogin == null){
                mFacebookLogin = new FacebookLogin();
            }
            return mFacebookLogin.isSignedIn();
        }
        //
        Log.w(TAG, "isSignedIn::FALSE");
        return false;
    }

    public void signOut(){
        //TODO: https://firebase.google.com/docs/auth/android/facebook-login
        if(mFirebaseAuth != null){
            try{
                mFirebaseAuth.signOut();
                mFirebaseUser = null;
            }catch(Exception e){
                //
            }
        }else if(mFacebookLogin != null){
            mFacebookLogin.logOut();
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

    public void signInFacebookUser(Activity activity) throws Exception {

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = mUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if (!isConnected) {
                throw new Exception("no internet connection");
            }else{
                if (mFacebookLogin == null) {
                    mFacebookLogin = new FacebookLogin();
                }
                mFacebookLogin.setFacebookLogin(activity);
                //on met un seul observer
                mFacebookLogin.deleteObservers();
                //on creer un observer
                mFacebookLogin.addObserver(new Observer() {
                    public void update(Observable obj, Object arg) {
                        Log.w(TAG, arg.toString());
                        setChanged();
                        notifyObservers("successfull login[1]");
                    }
                });
                //on ramene le observer

            }
        }catch(Exception e){
            Log.w(TAG, "AUTH_SIGNIN::EXCEPTION[000]");
            throw new Exception("no internet connection");
        }

    }

    public void facebookCallBackManager(int requestCode, int resultCode, Intent data){
        if(mFacebookLogin != null) {
            mFacebookLogin.facebookCallBackManager(requestCode, resultCode, data);
        }
    }

}
