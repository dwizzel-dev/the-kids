package com.dwizzel.auth;

/**
 * Created by Dwizzel on 13/11/2017.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Observable;

public class Service extends Observable{

    private static final String TAG = "TheKids.Service";
    private FirebaseAuth mFirebaseAuth;
    private static int count = 0;
    private Context context;

    public Service(Context context) throws Exception {
        Log.w(TAG, "count:" + count++);
        this.context = context;
        try {
            FirebaseApp.initializeApp(this.context );
            mFirebaseAuth = FirebaseAuth.getInstance();
        }catch (Exception e){
            Log.w(TAG, "exception:", e);
            throw new Exception(e);
        }
    }

    private boolean checkConnectivity(){
        Log.w(TAG, "checkConnectivity");
        if(context != null) {
            if(hasPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)) {
                try {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (cm != null) {
                        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
                    }
                }catch(Exception e) {
                    Log.w(TAG, "checkConnectivity.exception: ", e);
                }
            }
        }
        return false;
    }

    private boolean hasPermission(String permission) {
        Log.w(TAG, "hasPermission");
        if(context != null) {
            return (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }

    public boolean isSignedIn(){
        //check via le firebase si on est logue ou pas
        try{
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                return true;
            }
        }catch (Exception e) {
            Log.w(TAG, "isSignedIn.exception: ", e);
        }
        return false;
    }

    public void signOut() {
        try {
            mFirebaseAuth.signOut();
        } catch (Exception e) {
            Log.w(TAG, "signOut.exception: ", e);
        }
    }

    public String getUserLoginName() {
        try {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                return firebaseUser.getEmail();
            }
        }catch (Exception e) {
            Log.w(TAG, "getUserLoginName.exception: ", e);
        }
        return null;
    }

    public String getUserID(){
        try {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                return firebaseUser.getUid();
            }
        }catch (Exception e) {
            Log.w(TAG, "getUserID.exception: ", e);
        }
        return null;
    }

    public Task<AuthResult> signInUser(String email, String psw) throws Exception {
        Log.w(TAG, String.format("signInUser: \"%s\" | \"%s\"", email, psw));
        //avertir si pas connecte
        if (!checkConnectivity()) {
            throw new Exception("no internet connection");
        } else {
            try {
                return mFirebaseAuth.signInWithEmailAndPassword(email, psw);
            }catch (Exception e){
                throw new Exception(e);
            }
        }
    }

    public Task<AuthResult> createUser(String email, String psw) throws Exception {
        Log.w(TAG, String.format("createUser: \"%s\" | \"%s\"", email, psw));
        if (!checkConnectivity()) {
            throw new Exception("no internet connection");
        } else {
            try {
                return mFirebaseAuth.createUserWithEmailAndPassword(email, psw);
            }catch (Exception e){
                throw new Exception(e);
            }
        }
    }

    public Task<AuthResult> signInCredential(String token) throws Exception {
        Log.w(TAG, String.format("signInCredential: \"%s\"", token));
        if (!checkConnectivity()) {
            throw new Exception("no internet connection");
        } else {
            try {
                return mFirebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(token));
            }catch (Exception e){
                throw new Exception(e);
            }
        }
    }


}



