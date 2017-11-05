package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Auth {

    private static Auth sInst = null;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Utils mUtils;
    private static final String TAG = "THEKIDS::";


    private Auth() {
        // Required empty public constructor
        if(mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
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

    public FirebaseUser getUserInfos(){
        return mUser;
    }



    public boolean isSignedIn(){
        //check via le firebase si on est logue ou pas
        if(mAuth != null) {
            mUser = mAuth.getCurrentUser();
            if(mUser != null) {
                return true;
            }
        }
        return false;
    }

    public void signOut(){
        if(mAuth != null){
            try{
                mAuth.signOut();
                mUser = null;
            }catch(Exception e){
                //
            }
        }
    }

    public Task<AuthResult> createUser(Activity activity, String email, String psw) throws Exception {

        Log.w(TAG, String.format("AUTH::createUser[%s | %s]", email, psw));

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = mUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if(!isConnected) {
                throw new Exception("no internet connection");
            }else {
                return mAuth.createUserWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.w(TAG, "AUTH_CREATE::onComplete[000]");
                                if (task.isSuccessful()) {
                                    //on va chercher les infos du user
                                    mUser = mAuth.getCurrentUser();
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

        try {
            //on va checker si est connecte au net avant
            boolean isConnected = mUtils.checkConnectivity(activity.getApplicationContext());
            //avertir si pas connecte
            if (!isConnected) {
                throw new Exception("no internet connection");
            }else{
                return mAuth.signInWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.w(TAG, "AUTH_SIGNIN::onComplete[000]");
                                if (task.isSuccessful()) {
                                    //on va chercher les infos du user
                                    mUser = mAuth.getCurrentUser();
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
