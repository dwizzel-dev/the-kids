package com.dwizzel.services;

/**
 * Created by Dwizzel on 13/11/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import com.dwizzel.Const;
import com.dwizzel.objects.PermissionObject;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;
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

class AuthService implements IAuthService {

    private static final String TAG = "AuthService";
    private FirebaseAuth mFirebaseAuth;
    private static int count = 0;
    private Context mContext;
    private UserObject mUser;
    private ITrackerService mTrackerService;

    AuthService(Context context, ITrackerService trackerService) throws Exception {
        Tracer.log(TAG, "count:" + count++);
        try {
            mContext = context;
            mUser = UserObject.getInstance();
            FirebaseApp.initializeApp(mContext);
            mFirebaseAuth = FirebaseAuth.getInstance();
            mTrackerService = trackerService;
        }catch (Exception e){
            Tracer.log(TAG, "exception:", e);
            throw new Exception(e);
        }
    }

    public boolean isSignedIn(){
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

    public void signOut() {
        Tracer.log(TAG, "signOut");
        try {
            //pas de condition sinon prob quand il swap out l'appli et revient avec un restart
            //le facebook
            //if(mUser.getLoginType() == Const.user.TYPE_FACEBOOK) {
                LoginManager.getInstance().logOut();
            //}
            //le firebase
            mFirebaseAuth.signOut();
        } catch (Exception e) {
            Tracer.log(TAG, "signOut.exception: ", e);
        }
    }

    public String getEmail() {
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

    public String getUserID(){
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

    public void signInUser(String email, String psw){
        Tracer.log(TAG, String.format("signInUser: \"%s\" | \"%s\"", email, psw));
        //avertir si pas connecte
        if (Utils.getInstance().checkConnectivity(mContext)) {
            try {
                mFirebaseAuth.signInWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidCredentialsException invalidPsw) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.error.ERROR_INVALID_PASSWORD,
                                                        "error message will come one day"
                                                ));
                                    } catch (FirebaseAuthInvalidUserException invalidCredential) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.error.ERROR_INVALID_CREDENTIALS,
                                                        "error message will come one day"
                                                ));
                                    } catch (NullPointerException npe) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.except.NULL_POINTER,
                                                        "error message will come one day"
                                                ));
                                    } catch (Exception e) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.except.GENERIC,
                                                        "error message will come one day"
                                                ));
                                    }
                                } else {
                                    try {
                                        //on set le user comme signed in
                                        mUser.setLoginType(Const.user.TYPE_EMAIL);
                                        //pas erreur alors on continue
                                        mTrackerService.onUserSignedIn(new ServiceResponseObject(
                                                Const.response.ON_USER_SIGNIN
                                        ));
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
                mTrackerService.onUserSignedIn(new ServiceResponseObject(
                        Const.except.NO_CONNECTION,
                        "error message will come one day"
                        ));
            }catch (NullPointerException npe){
                Tracer.log(TAG, "signInUser.NullPointerException: ", npe);
            }
        }
    }

    public void createUser(String email, String psw) {
        Tracer.log(TAG, String.format("createUser: \"%s\" | \"%s\"", email, psw));
        //avertir si pas connecte
        if (Utils.getInstance().checkConnectivity(mContext)) {
            try {
                mFirebaseAuth.createUserWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthUserCollisionException existEmail) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.error.ERROR_EMAIL_EXIST,
                                                        "error message will come one day"
                                                        ));
                                    }catch (FirebaseAuthWeakPasswordException weakPsw) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.error.ERROR_WEAK_PASSWORD,
                                                        "error message will come one day"
                                                ));
                                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.error.ERROR_INVALID_EMAIL,
                                                        "error message will come one day"
                                                ));
                                    } catch (NullPointerException npe) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.except.NULL_POINTER,
                                                        "error message will come one day"
                                                ));
                                    } catch (Exception e) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.except.GENERIC,
                                                        "error message will come one day"
                                                ));
                                    }
                                } else {
                                    try {
                                        mUser.setLoginType(Const.user.TYPE_EMAIL);
                                        //pas erreur alors on continue
                                        mTrackerService.onUserSignedIn(new ServiceResponseObject(
                                                Const.response.ON_USER_SIGNIN
                                        ));
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
                mTrackerService.onUserSignedIn(new ServiceResponseObject(
                        Const.except.NO_CONNECTION,
                        "error message will come one day"
                ));
            }catch (NullPointerException npe){
                Tracer.log(TAG, "createUser.NullPointerException: ", npe);
            }
        }
    }

    public void signInUser(AuthCredential token){
        Tracer.log(TAG, String.format("signInCredential: \"%s\"", token));
        if (Utils.getInstance().checkConnectivity(mContext)) {
            try {
                mFirebaseAuth.signInWithCredential(token)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    } catch (NullPointerException npe) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.except.NULL_POINTER,
                                                        "error message will come one day"
                                                ));
                                    } catch (Exception e) {
                                        mTrackerService.onUserSignedIn(
                                                new ServiceResponseObject(
                                                        Const.except.GENERIC,
                                                        "error message will come one day"
                                                ));
                                    }
                                } else {
                                    try {
                                        mUser.setLoginType(Const.user.TYPE_FACEBOOK);
                                        //pas erreur alors on continue
                                        mTrackerService.onUserSignedIn(new ServiceResponseObject(
                                                Const.response.ON_USER_SIGNIN
                                        ));
                                    } catch (NullPointerException npe) {
                                        Tracer.log(TAG, "signInCredential.onComplete.NullPointerException: ", npe);
                                    }
                                }
                            }
                        });
            } catch (Exception e) {
                Tracer.log(TAG, "signInCredential.Exception: ", e);
            }
        }else{
            try {
                mTrackerService.onUserSignedIn(new ServiceResponseObject(
                        Const.except.NO_CONNECTION,
                        "error message will come one day"
                ));
            }catch (NullPointerException npe){
                Tracer.log(TAG, "signInUser.NullPointerException: ", npe);
            }
        }
    }

}



