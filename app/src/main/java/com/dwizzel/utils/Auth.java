package com.dwizzel.utils;

/**
 * Created by Dwizzel on 03/11/2017.
 */

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.io.IOException;

public class Auth {

    private static Auth sInst = null;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static final String TAG = "THEKIDS::";

    private OnCompleteListener onCompleteListener;

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    private Auth() {
        // Required empty public constructor
        if(mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
    }

    public FirebaseUser getUserInfos(){
        return mUser;
    }

    public static synchronized Auth getInstance() {
        if(sInst  == null) {
            synchronized (Auth.class) {
                sInst  = new Auth();
            }
        }
        return sInst;
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

            }
        }
    }

    public Task<AuthResult> createUser(Activity activity, String email, String psw){

        Log.w(TAG, String.format("AUTH::createUser[%s | %s]", email, psw));

        return mAuth.createUserWithEmailAndPassword(email, psw)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        Log.w(TAG, "AUTH::onComplete[000]");
                        if (task.isSuccessful()) {
                            //on va chercher les infos du user
                            mUser = mAuth.getCurrentUser();
                        }else{
                            //exception will be handle by the activity method caller
                            //to display is own errors
                        }
                    }
                });

    }



}
