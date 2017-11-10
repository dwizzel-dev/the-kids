package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dwizzel.Const;
import com.dwizzel.thekids.ObserverObject;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Observable;
import java.util.Observer;

public class Auth extends Observable {

    private static final String TAG = "TheKids.Auth";
    private static Auth sInst = null;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Utils mUtils;
    private FacebookLogin mFacebookLogin;


    private Auth() {
        // Required empty public constructor
        if (mUtils == null) {
            mUtils = Utils.getInstance();
        }
        // Required empty public constructor
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
    }

    public static synchronized Auth getInstance() {
        if (sInst == null) {
            synchronized (Auth.class) {
                sInst = new Auth();
            }
        }
        return sInst;
    }

    public String getUserLoginName() {
        if (mFirebaseUser != null) {
            return mFirebaseUser.getEmail();
        }
        /*
        if (mFirebaseUser != null) {
            //si firebase user
            Log.w(TAG, "getUserLoginName: firebase");
            return mFirebaseUser.getEmail();
        } else if (mFacebookLogin != null) {
            //si facebook user
            Log.w(TAG, "getUserLoginName: facebook");
            return mFacebookLogin.getUserLoginName();
        }
        */
        return "...";
    }


    public boolean isSignedIn() {
        //check via le firebase si on est logue ou pas
        if (mFirebaseAuth != null) {
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if (mFirebaseUser != null) {
                Log.w(TAG, "isSignedIn: firebase");
                return true;
            }
        }
        //
        Log.w(TAG, "isSignedIn: false");
        return false;
    }

    public void signOut() {
        Log.w(TAG, "signOut");

        if(mFacebookLogin == null){
            mFacebookLogin = new FacebookLogin();
            mFacebookLogin.logOut();
        }
        if (mFirebaseAuth == null){
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        try {
            mFacebookLogin.logOut();
            mFirebaseAuth.signOut();
            mFirebaseUser = null;
        } catch (Exception e) {
            //
        }


    }

    public Task<AuthResult> createUser(Activity activity, String email, String psw) throws Exception {

        Log.w(TAG, String.format("createUser: \"%s\" | \"%s\"", email, psw));

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = mUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if (!isConnected) {
                throw new Exception("no internet connection");
            } else {
                return mFirebaseAuth.createUserWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.w(TAG, "createUserWithEmailAndPassword.onComplete");
                                if (task.isSuccessful()) {
                                    //on va chercher les infos du user
                                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                } else {
                                    Log.w(TAG, "createUserWithEmailAndPassword: failure", task.getException());
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Log.w(TAG, "createUser exception: ", e);
            throw new Exception("no internet connection");
        }

    }

    public Task<AuthResult> signInUser(Activity activity, String email, String psw) throws Exception {

        Log.w(TAG, String.format("signInUser: \"%s\" | \"%s\"", email, psw));

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = mUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if (!isConnected) {
                throw new Exception("no internet connection");
            } else {
                return mFirebaseAuth.signInWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.w(TAG, "signInWithEmailAndPassword.onComplete");
                                if (task.isSuccessful()) {
                                    //on va chercher les infos du user
                                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                } else {
                                    Log.w(TAG, "signInWithEmailAndPassword: failure", task.getException());
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Log.w(TAG, "signInUser exception: ", e);
            throw new Exception("no internet connection");
        }

    }

    public void signInCredential(Activity activity, AccessToken accessToken) throws Exception {

        Log.w(TAG, "signInCredential");

        mFirebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(accessToken.getToken()))
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {

                    private void notifyParent(Object arg){
                        Log.d(TAG, "notifyParent:" + arg);
                        setChanged();
                        notifyObservers(arg);
                    }

                    @Override
                    public void onComplete (@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential: success");
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            notifyParent(Const.notif.TYPE_NOTIF_SIGNED);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential: failure", task.getException());
                        }
                    }
                });
    }

    public void initFacebookLogin(Activity activity) throws Exception {

        Log.w(TAG, "initFacebookLogin");

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
                //on creer un observer sur le login de facebook
                mFacebookLogin.addObserver(new Observer() {

                    private void notifyParent(Object arg){
                        Log.d(TAG, "notifyParent:" + arg);
                        setChanged();
                        notifyObservers(arg);
                    }

                    // notifier le observer qui a besoin du nom pour afficher un Toast
                    public void update(Observable obj, Object arg) {
                        Log.w(TAG, "mFacebookLogin.update:" + arg);
                        ObserverObject observerObject = (ObserverObject)arg;
                        if(observerObject != null) {
                            switch (observerObject.getType()) {
                                case Const.notif.TYPE_NOTIF_LOGIN:
                                    //
                                    try {
                                        notifyParent(Const.notif.TYPE_NOTIF_LOADING);
                                        signInCredential(observerObject.getActivity(), observerObject.getToken());
                                        //on a le loader alors on va
                                    } catch (Exception e) {

                                    }
                                    break;
                                case Const.notif.TYPE_NOTIF_PROFILE:
                                    Log.w(TAG, "mFacebookLogin.update: " + "TYPE_NOTIF_PROFILE");
                                    break;
                                default:
                                    break;
                            }

                        }
                    }
                });
                //on ramene le observer

            }
        }catch(Exception e){
            Log.w(TAG, "initFacebookLogin exception: ", e);
            throw new Exception("no internet connection");
        }

    }

    public void facebookCallBackManager(int requestCode, int resultCode, Intent data){
        if(mFacebookLogin != null) {
            mFacebookLogin.facebookCallBackManager(requestCode, resultCode, data);
        }
    }

    public void disableFacebookButton(){
        mFacebookLogin.disableFacebookButton();

    }

}
