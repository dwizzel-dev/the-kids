package com.dwizzel.services;

/**
 * Created by Dwizzel on 13/11/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.dwizzel.Const;
import com.dwizzel.objects.PermissionObject;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.utils.Tracer;
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

class AuthService {

    private static final String TAG = "AuthService";
    private FirebaseAuth mFirebaseAuth;
    private TrackerService.TrackerBinder mTrackerBinder;
    private static int count = 0;
    private Context mContext;
    private UserObject mUser;

    AuthService(Context context, IBinder trackerBinder) throws Exception {
        Tracer.log(TAG, "count:" + count++);
        mContext = context;
        try {
            mUser = UserObject.getInstance();
            FirebaseApp.initializeApp(mContext);
            mFirebaseAuth = FirebaseAuth.getInstance();
            mTrackerBinder = (TrackerService.TrackerBinder) trackerBinder;
        }catch (Exception e){
            Tracer.log(TAG, "exception:", e);
            throw new Exception(e);
        }
    }

    private boolean checkConnectivity(){
        Tracer.log(TAG, "checkConnectivity");
        if(mContext != null) {
            if(hasPermission()) {
                try {
                    ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (cm != null) {
                        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
                    }
                }catch(Exception e) {
                    Tracer.log(TAG, "checkConnectivity.Exception: ", e);
                }
            }
        }
        return false;
    }

    private boolean hasPermission() {
        Tracer.log(TAG, "hasPermission");
        if(mContext != null) {
            PermissionObject perms = new PermissionObject(mContext);
            Tracer.log(TAG, "Permissions[ACCESS_NETWORK_STATE]: " + perms.isAccessNetworkState());
            Tracer.log(TAG, "Permissions[INTERNET]: " + perms.isInternet());
            return (perms.isAccessNetworkState() && perms.isInternet());
        }
        return false;
    }

    boolean isSignedIn(){
        Tracer.log(TAG, "isSignedIn");
        //check via le firebase si on est logue ou pas
        try{
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                return true;
            }
        }catch (Exception e) {
            Tracer.log(TAG, "isSignedIn.exception: ", e);
        }
        return false;
    }

    void signOut() {
        Tracer.log(TAG, "signOut");
        try {
            //le facebook
            if(mUser.getLoginType() == Const.user.TYPE_FACEBOOK) {
                LoginManager.getInstance().logOut();
            }
            //le firebase
            mFirebaseAuth.signOut();
        } catch (Exception e) {
            Tracer.log(TAG, "signOut.exception: ", e);
        }
    }

    String getEmail() {
        Tracer.log(TAG, "getEmail");
        try {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                return firebaseUser.getEmail();
            }
        }catch (Exception e) {
            Tracer.log(TAG, "getEmail.exception: ", e);
        }
        return null;
    }

    String getUserID(){
        Tracer.log(TAG, "getUserID");
        try {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                return firebaseUser.getUid();
            }
        }catch (Exception e) {
            Tracer.log(TAG, "getUserID.exception: ", e);
        }
        return null;
    }

    void signInUser(String email, String psw){
        Tracer.log(TAG, String.format("signInUser: \"%s\" | \"%s\"", email, psw));
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
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.error.ERROR_INVALID_PASSWORD));
                                    } catch (FirebaseAuthInvalidUserException invalidCredential) {
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.error.ERROR_INVALID_CREDENTIALS));
                                    } catch (NullPointerException npe) {
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.except.NULL_POINTER));
                                    } catch (Exception e) {
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.except.GENERIC));
                                    }
                                } else {
                                    try {
                                        //on set le user comme signed in
                                        mUser.setLoginType(Const.user.TYPE_EMAIL);
                                        //pas erreur alors on continue
                                        mTrackerBinder.onSignedIn(new ServiceResponseObject());
                                    }catch(NullPointerException npe){
                                        Tracer.log(TAG, "signInUser.onComplete.NullPointerException: ", npe);
                                    }
                                }
                            }
                        });
            }catch(Exception e){
                Tracer.log(TAG, "signInUser.Exception: ", e);
            }
        }else{
            try {
                mTrackerBinder.onSignedIn(new ServiceResponseObject(Const.except.NO_CONNECTION));
            }catch (NullPointerException npe){
                Tracer.log(TAG, "signInUser.NullPointerException: ", npe);
            }
        }
    }

    void createUser(String email, String psw) {
        Tracer.log(TAG, String.format("createUser: \"%s\" | \"%s\"", email, psw));
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
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.error.ERROR_EMAIL_EXIST));
                                    }catch (FirebaseAuthWeakPasswordException weakPsw) {
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.error.ERROR_WEAK_PASSWORD));
                                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.error.ERROR_INVALID_EMAIL));
                                    } catch (NullPointerException npe) {
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.except.NULL_POINTER));
                                    } catch (Exception e) {
                                        mTrackerBinder.onSignedIn(
                                                new ServiceResponseObject(Const.except.GENERIC));
                                    }
                                } else {
                                    try {
                                        mUser.setLoginType(Const.user.TYPE_EMAIL);
                                        //pas erreur alors on continue
                                        mTrackerBinder.onSignedIn(new ServiceResponseObject());
                                    }catch(NullPointerException npe){
                                        Tracer.log(TAG, "createUser.onComplete.NullPointerException: ", npe);
                                    }
                                }
                            }
                        });
            }catch(Exception e){
                Tracer.log(TAG, "createUser.Exception: ", e);
            }
        }else{
            try {
                mTrackerBinder.onSignedIn(new ServiceResponseObject(Const.except.NO_CONNECTION));
            }catch (NullPointerException npe){
                Tracer.log(TAG, "createUser.NullPointerException: ", npe);
            }
        }
    }

    void signInUser(AuthCredential token){
        Tracer.log(TAG, String.format("signInCredential: \"%s\"", token));
        try {
            mFirebaseAuth.signInWithCredential(token)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch (NullPointerException npe) {
                                    mTrackerBinder.onSignedIn(
                                            new ServiceResponseObject(Const.except.NULL_POINTER));
                                } catch (Exception e) {
                                    mTrackerBinder.onSignedIn(
                                            new ServiceResponseObject(Const.except.GENERIC));
                                }
                            } else {
                                try {
                                    mUser.setLoginType(Const.user.TYPE_FACEBOOK);
                                    //pas erreur alors on continue
                                    mTrackerBinder.onSignedIn(new ServiceResponseObject());
                                }catch(NullPointerException npe){
                                    Tracer.log(TAG, "signInCredential.onComplete.NullPointerException: ", npe);
                                }
                            }
                        }
                    });
        }catch(Exception e){
            Tracer.log(TAG, "signInCredential.Exception: ", e);
        }
    }

}



