package com.dwizzel.auth;

/**
 * Created by Dwizzel on 13/11/2017.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import com.dwizzel.Const;
import com.dwizzel.models.CommunicationObject;
import com.dwizzel.services.TrackerService;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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

    public void signInUser(final TrackerService.TrackerBinder binder, String email, String psw){
        Log.w(TAG, String.format("signInUser: \"%s\" | \"%s\"", email, psw));
        //avertir si pas connecte
        if (checkConnectivity()) {
            try {
                mFirebaseAuth.signInWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidCredentialsException invalidPsw) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.error.ERROR_INVALID_PASSWORD));
                                    } catch (FirebaseAuthInvalidUserException invalidCredential) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.error.ERROR_INVALID_CREDENTIALS));
                                    } catch (NullPointerException npe) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.except.NULL_POINTER));
                                    } catch (Exception e) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.except.GENERIC));
                                    }
                                } else {
                                    try {
                                        //pas erreur alors on continue
                                        binder.onSignedIn(
                                                new CommunicationObject.ServiceResponseObject(
                                                        new CommunicationObject.UserObject(
                                                                getUserLoginName(), getUserID())
                                                ));
                                    }catch(NullPointerException npe){
                                        Log.w(TAG, "signInUser.onComplete.NullPointerException: ", npe);
                                    }
                                }
                            }
                        });
            }catch(Exception e){
                Log.w(TAG, "signInUser.Exception: ", e);
            }
        }else{
            try {
                binder.onSignedIn(new CommunicationObject
                        .ServiceResponseObject(Const.except.NO_CONNECTION));
            }catch (NullPointerException npe){
                Log.w(TAG, "signInUser.NullPointerException: ", npe);
            }
        }
    }

    public void createUser(final TrackerService.TrackerBinder binder, String email, String psw) {
        Log.w(TAG, String.format("createUser: \"%s\" | \"%s\"", email, psw));
        //avertir si pas connecte
        if (checkConnectivity()) {
            try {
                mFirebaseAuth.createUserWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthUserCollisionException existEmail) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.error.ERROR_EMAIL_EXIST));
                                    }catch (FirebaseAuthWeakPasswordException weakPsw) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.error.ERROR_WEAK_PASSWORD));
                                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.error.ERROR_INVALID_EMAIL));
                                    } catch (NullPointerException npe) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.except.NULL_POINTER));
                                    } catch (Exception e) {
                                        binder.onSignedIn(new CommunicationObject
                                                .ServiceResponseObject(Const.except.GENERIC));
                                    }
                                } else {
                                    try {
                                        //pas erreur alors on continue
                                        binder.onSignedIn(
                                                new CommunicationObject.ServiceResponseObject(
                                                        new CommunicationObject.UserObject(
                                                                getUserLoginName(), getUserID())
                                                ));
                                    }catch(NullPointerException npe){
                                        Log.w(TAG, "createUser.onComplete.NullPointerException: ", npe);
                                    }
                                }
                            }
                        });
            }catch(Exception e){
                Log.w(TAG, "createUser.Exception: ", e);
            }
        }else{
            try {
                binder.onSignedIn(new CommunicationObject
                        .ServiceResponseObject(Const.except.NO_CONNECTION));
            }catch (NullPointerException npe){
                Log.w(TAG, "createUser.NullPointerException: ", npe);
            }
        }
    }

    public void signInUser(final TrackerService.TrackerBinder binder, AuthCredential token){
        Log.w(TAG, String.format("signInCredential: \"%s\"", token));
        try {
            mFirebaseAuth.signInWithCredential(token)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch (NullPointerException npe) {
                                    binder.onSignedIn(new CommunicationObject
                                            .ServiceResponseObject(Const.except.NULL_POINTER));
                                } catch (Exception e) {
                                    binder.onSignedIn(new CommunicationObject
                                            .ServiceResponseObject(Const.except.GENERIC));
                                }
                            } else {
                                try {
                                    //pas erreur alors on continue
                                    binder.onSignedIn(
                                            new CommunicationObject.ServiceResponseObject(
                                                    new CommunicationObject.UserObject(
                                                            getUserLoginName(), getUserID())
                                            ));
                                }catch(NullPointerException npe){
                                    Log.w(TAG, "signInCredential.onComplete.NullPointerException: ", npe);
                                }
                            }
                        }
                    });
        }catch(Exception e){
            Log.w(TAG, "signInCredential.Exception: ", e);
        }
    }

}



