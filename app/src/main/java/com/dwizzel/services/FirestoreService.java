package com.dwizzel.services;

import android.support.annotation.NonNull;

import com.dwizzel.utils.Tracer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.dwizzel.models.UserModel;

/**
 * Created by Dwizzel on 09/11/2017.
 * https://firebase.google.com/docs/firestore/quickstart
 * https://console.firebase.google.com/project/thekids-dab99/database/firestore/data~2F
 * https://firebase.google.com/docs/firestore/query-data/get-data
 * https://firebase.google.com/docs/firestore/manage-data/add-data
 *
 *  pour update des positons on va utiliser le realtime change avec un listener
 *  sur les documents avec FireStore
 *  https://firebase.google.com/docs/firestore/query-data/listen
 *
 *
 *  TODO: on va faire des triggers avec le CloudFunction
 *  - sur le signIn pour les mettre dans la collection "active"
 *  - sur un signOut pour les enlever de la collection "active"
 *
 */

class FirestoreService {

    //name of database collection and fields
    class DB{
        class Users {
            static final String collection = "users";
            class Field {
                static final String active = "active";
                static final String updateTime = "updateTime";
                static final String position = "position";
            }
        }
    }

    private final static String TAG = "FirestoreService";
    private static FirestoreService sInst;
    private FirebaseFirestore mDb;

    private FirestoreService() {
        mDb = FirebaseFirestore.getInstance();
    }

    static FirestoreService getInstance() {
        if (sInst == null) {
            sInst = new FirestoreService();
        }
        return sInst;
    }

    private void setUserInfos(UserModel user){
        Tracer.log(TAG, String.format("setUserInfos: %s | %s", user.getEmail(), user.getUid()));
        try{
            //add the new user collection with his id
            mDb.collection(DB.Users.collection).document(user.getUid())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "createUser.addOnSuccessListener");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "createUser.addOnFailureListener.Exception: ", e);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "createUser.Exception: ", e);
        }

    }

    void updateUserInfos(UserModel user){
        Tracer.log(TAG, String.format("updateUserInfos: %s", user.getUid()));
        try{
            //https://firebase.google.com/docs/firestore/manage-data/add-data
            //update juste le updateTime
            mDb.collection(DB.Users.collection).document(user.getUid())
                    .update(
                            DB.Users.Field.updateTime, FieldValue.serverTimestamp(),
                            DB.Users.Field.position, user.getPosition()
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "updateUserInfos.addOnSuccessListener");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG,"updateUserInfos.addOnFailureListener.Exception: ", e);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "updateUserInfos.Exception: ", e);
        }

    }

    void getUserInfos(final UserModel user){
        Tracer.log(TAG, String.format("getUserInfos: %s | %s", user.getEmail(), user.getUid()));
        try{
            mDb.collection(DB.Users.collection).document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Tracer.log(TAG, "getUserInfos.onComplete");
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Tracer.log(TAG, "getUserInfos.document: " +  document.exists());
                                if(document.exists()){
                                    Tracer.log(TAG, "DATA: " + document.getData());
                                    //vu qu'il est deja creer on fait un updateTime
                                    updateUserInfos(user);
                                }else{
                                    Tracer.log(TAG, "no document, creating new user");
                                    // si on a rien alors on a un nouveau user
                                    // alors on l'enregistre dans la collection
                                    // "thekids-dab99 > users"
                                    setUserInfos(user);
                                }
                            } else {
                                Tracer.log(TAG, "getUserInfos.onComplete.exception: ", task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "getUserInfos.Exception: ", e);
        }
    }

    void activateUser(final String uid, String position){
        Tracer.log(TAG, String.format("activateUser: %s | %s", uid, position));
        //get a timestamp for activity timer pending
        try{
            //add the new user collection with his id
            mDb.collection(DB.Users.collection).document(uid)
                    .update(DB.Users.Field.active, true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //on set le dernier UID actif pour la verif au delete
                            Tracer.log(TAG, "activateUser.addOnSuccessListener: " + uid);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "activateUser.addOnFailureListener.Exception: ", e);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "activateUser.Exception: ", e);
        }

    }

    void deactivateUser(final String uid){
        //TODO: on risque d'avoir le retour du listener apres, car le Auth sera deja signOut
        Tracer.log(TAG, String.format("deactivateUser: %s", uid));
        //minor check
        if(uid != null && !uid.equals("")) {
            //get a timestamp for activity timer pending
            try {
                //add the new user collection with his id
                mDb.collection(DB.Users.collection).document(uid)
                        .update(DB.Users.Field.active, false)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Tracer.log(TAG, "deactivateUser.addOnSuccessListener: " + uid);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Tracer.log(TAG, "deactivateUser.addOnFailureListener.Exception: ", e);
                            }
                        });
            } catch (Exception e) {
                Tracer.log(TAG, "deactivateUser.Exception: ", e);
            }
        }else {
            Tracer.log(TAG, "deactivateUser.Exception: ++ UID is empty ++");
        }
    }


}
