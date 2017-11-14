package com.dwizzel.auth;

/**
 * Created by Dwizzel on 13/11/2017.
 */

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.util.Log;

import com.dwizzel.services.ITrackerBinder;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthService {

    private static final String TAG = "TheKids.AuthService";
    private FirebaseAuth mFirebaseAuth;
    private static int count = 0;
    private Context context;

    public AuthService(Context context) throws Exception {
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

    public void getUserData(){
        //TODO: get data from FirestoreDatabse
    }

    private boolean hasUserData(){
        Log.w(TAG, "hasUserData");
        //TODO: if we have the infos from the FirestoreDatabse
        return false;
    }

    public boolean isSignedIn(){
        Log.w(TAG, "isSignedIn");
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
        Log.w(TAG, "signOut");
        try {
            //le facebook
            LoginManager.getInstance().logOut();
            //le firebase
            mFirebaseAuth.signOut();
        } catch (Exception e) {
            Log.w(TAG, "signOut.exception: ", e);
        }
    }

    public String getUserLoginName() {
        Log.w(TAG, "getUserLoginName");
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
        Log.w(TAG, "getUserID");
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



