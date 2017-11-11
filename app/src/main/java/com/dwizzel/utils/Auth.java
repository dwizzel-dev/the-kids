package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dwizzel.Const;
import com.dwizzel.models.NotifObjectObserver;
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
    private static Auth sInst;
    private static FirebaseAuth sFirebaseAuth;
    private static FirebaseUser sFirebaseUser;
    private static Utils sUtils;
    private FacebookLogin mFacebookLogin;
    private static int iCount = 0;


    private Auth() {
        // Required empty public constructor

    }

    public static synchronized Auth getInstance() {
        if (sInst == null) {
            synchronized (Auth.class) {
                sInst = new Auth();
                sUtils = Utils.getInstance();
                // Required empty public constructor
                sFirebaseAuth = FirebaseAuth.getInstance();
            }
        }
        Log.w(TAG, "count:" + iCount++);
        return sInst;
    }

    public String getUserLoginName() {
        if (sFirebaseUser != null) {
            return sFirebaseUser.getEmail();
        }
        /*
        if (sFirebaseUser != null) {
            //si firebase user
            Log.w(TAG, "getUserLoginName: firebase");
            return sFirebaseUser.getEmail();
        } else if (mFacebookLogin != null) {
            //si facebook user
            Log.w(TAG, "getUserLoginName: facebook");
            return mFacebookLogin.getUserLoginName();
        }
        */
        return "...";
    }

    public String getUserID() throws Exception{
        if (sFirebaseUser != null) {
            return sFirebaseUser.getUid();
        }
        throw new Exception("no uid");
    }


    public boolean isSignedIn() {
        //check via le firebase si on est logue ou pas
        if (sFirebaseAuth != null) {
            sFirebaseUser = sFirebaseAuth.getCurrentUser();
            if (sFirebaseUser != null) {
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
        if (sFirebaseAuth == null){
            sFirebaseAuth = FirebaseAuth.getInstance();
        }
        try {
            mFacebookLogin.logOut();
            sFirebaseAuth.signOut();
            sFirebaseUser = null;
        } catch (Exception e) {
            //
        }


    }

    public Task<AuthResult> createUser(Activity activity, String email, String psw) throws Exception {

        Log.w(TAG, String.format("createUser: \"%s\" | \"%s\"", email, psw));

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = sUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if (!isConnected) {
                throw new Exception("no internet connection");
            } else {
                return sFirebaseAuth.createUserWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.w(TAG, "createUserWithEmailAndPassword.onComplete");
                                if (task.isSuccessful()) {
                                    //on va chercher les infos du user
                                    sFirebaseUser = sFirebaseAuth.getCurrentUser();
                                } else {
                                    Log.w(TAG, "createUserWithEmailAndPassword.exception: ", task.getException());
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Log.w(TAG, "createUser.exception: ", e);
            throw new Exception("no internet connection");
        }

    }

    public Task<AuthResult> signInUser(Activity activity, String email, String psw) throws Exception {

        Log.w(TAG, String.format("signInUser: \"%s\" | \"%s\"", email, psw));

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = sUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if (!isConnected) {
                throw new Exception("no internet connection");
            } else {
                return sFirebaseAuth.signInWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.w(TAG, "signInWithEmailAndPassword.onComplete");
                                if (task.isSuccessful()) {
                                    //on va chercher les infos du user
                                    sFirebaseUser = sFirebaseAuth.getCurrentUser();
                                } else {
                                    Log.w(TAG, "signInWithEmailAndPassword.exception", task.getException());
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Log.w(TAG, "signInUser.exception: ", e);
            throw new Exception("no internet connection");
        }

    }

    public void signInCredential(Activity activity, AccessToken accessToken) throws Exception {

        Log.w(TAG, "signInCredential");

        sFirebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(accessToken.getToken()))
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {

                    private void notifyParent(Object arg){
                        Log.w(TAG, "notifyParent:" + arg);
                        setChanged();
                        notifyObservers(arg);
                    }

                    @Override
                    public void onComplete (@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.w(TAG, "signInWithCredential: success");
                            sFirebaseUser = sFirebaseAuth.getCurrentUser();
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
            boolean isConnected = sUtils.checkConnectivity(activity.getApplicationContext());
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
                        Log.w(TAG, "notifyParent:" + arg);
                        setChanged();
                        notifyObservers(arg);
                    }

                    // notifier le observer qui a besoin du nom pour afficher un Toast
                    public void update(Observable obj, Object arg) {
                        Log.w(TAG, "mFacebookLogin.update:" + arg);
                        NotifObjectObserver observerObject = (NotifObjectObserver)arg;
                        if(observerObject != null) {
                            switch (observerObject.getType()) {
                                case Const.notif.TYPE_NOTIF_LOGIN:
                                    //
                                    try {
                                        //on a le ok alors on va mettre un loader
                                        notifyParent(Const.notif.TYPE_NOTIF_LOADING);
                                        //on va se auth chez firebase
                                        signInCredential(observerObject.getActivity(), observerObject.getToken());

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
