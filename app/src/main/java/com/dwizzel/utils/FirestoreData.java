package com.dwizzel.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dwizzel on 09/11/2017.
 */

public class FirestoreData {

    private final static String TAG = "TheKids.FirestoreData";
    private static FirestoreData sInst;
    private static FirebaseFirestore sDb;

    private FirestoreData() {
        // Required empty public constructor
    }

    public static FirestoreData FirestoreData() {
        if (sInst == null) {
            sInst = new FirestoreData();
            sDb = FirebaseFirestore.getInstance();
        }
        return sInst;
    }

    public void createUser(String email){
        //create the collection for a new user
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        sDb.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}
