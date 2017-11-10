package com.dwizzel.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dwizzel.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.dwizzel.models.UserModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dwizzel on 09/11/2017.
 * https://firebase.google.com/docs/firestore/quickstart
 * https://console.firebase.google.com/project/thekids-dab99/database/firestore/data~2F
 * https://firebase.google.com/docs/firestore/query-data/get-data
 * https://firebase.google.com/docs/firestore/manage-data/add-data
 */

public class FirestoreData {

    private final static String TAG = "TheKids.FirestoreData";
    private static FirestoreData sInst;
    private FirebaseFirestore mDb;
    private static Auth sAuth;

    private FirestoreData() {
        sAuth = Auth.getInstance();
        mDb = FirebaseFirestore.getInstance();
    }

    public static FirestoreData getInstance() {
        if (sInst == null) {
            sInst = new FirestoreData();
        }
        return sInst;
    }

    public void createNewUser(){
        try{
            createUser();
        }catch (Exception e){
            Log.w(TAG, "createNewUser.Exception: ", e);
        }
    }

    public void createUser() throws Exception{
        Log.w(TAG, "createUser");
        try{
            //use a models
            UserModel user = new UserModel(sAuth.getUserLoginName(), sAuth.getUserID());
            //add the new user collection with his id
            mDb.collection("users").document(sAuth.getUserID())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Log.w(TAG, "addOnSuccessListener");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "addOnFailureListener.Exception: ", e);
                        }
                    });
        }catch (Exception e){
            Log.w(TAG, "createUser.Exception: ", e);
            throw e;
        }

    }

    public void getUserinfos() throws Exception{
        Log.w(TAG, "getUserinfos");
        try{
            mDb.collection("users").document(sAuth.getUserID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Log.w(TAG, "getUserinfos.onComplete");
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.w(TAG, "getUserinfos.document: " +  document.exists());
                                if(document.exists()){
                                    Log.w(TAG, "DATA: " + document.getData());
                                }else{
                                    Log.w(TAG, "no document, creating new user");
                                    //si on a rien alors on le creer
                                    createNewUser();
                                }

                            } else {
                                Log.w(TAG, "onComplete.exception: ", task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            throw e;
        }
    }

}
