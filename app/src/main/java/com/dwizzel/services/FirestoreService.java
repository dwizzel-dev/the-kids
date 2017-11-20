package com.dwizzel.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dwizzel.datamodels.DataModel;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.utils.Tracer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
 *  TODO: checker la connectivity
 *
 */

class FirestoreService implements IFirestoreService{

    //name of database collection and fields
    class DB{
        class Users {
            static final String collection = "users";
            class Field {
                static final String uid = "uid";
                static final String email = "email";
                static final String updateTime = "updateTime";
                static final String createTime = "createTime";
                static final String loginType = "loginType";
            }
        }
        class Actives {
            static final String collection = "actives";
            class Field {
                static final String updateTime = "updateTime";
                static final String updateTimePosition = "updateTimePosition";
                static final String gps = "gps";
                static final String position = "position";
            }
        }
    }

    private final static String TAG = "FirestoreService";
    private FirebaseFirestore mDb;
    private UserObject mUser;
    private Context mContext;
    private ITrackerService mTrackerService;

    FirestoreService (Context context, ITrackerService trackerService) {
        mDb = FirebaseFirestore.getInstance();
        mUser = UserObject.getInstance();
        mTrackerService = trackerService;
        mContext = context;
        }

    private void setUserInfos(){
        Tracer.log(TAG, "setUserInfos");
        try{
            //add the new user collection with his id
            mDb.collection(DB.Users.collection).document(mUser.getUid())
                    .set(mUser.toUserData())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "createUser.addOnSuccessListener");
                            //maintenant il est cree alors on set et cherche les infos
                            getUserInfos();
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

    public void updateUserInfos(){
        Tracer.log(TAG, "updateUserInfos");
        try{
            // il faut que le user soit creer avant tout
            if(mUser.isCreated()) {
                //update juste le updateTime
                mDb.collection(DB.Users.collection).document(mUser.getUid())
                        .update(
                                DB.Users.Field.updateTime, FieldValue.serverTimestamp(),
                                DB.Users.Field.loginType, mUser.getLoginType()
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
                                Tracer.log(TAG, "updateUserInfos.addOnFailureListener.Exception: ", e);
                            }
                        });
            }else {
                Tracer.log(TAG, "updateUserInfos: user not created yet");
            }
        }catch (Exception e){
            Tracer.log(TAG, "updateUserInfos.Exception: ", e);
        }

    }

    public void updateUserPosition(){
        Tracer.log(TAG, "updateUserPosition");
        try{
            mDb.collection(DB.Actives.collection).document(mUser.getUid())
                    .update(
                            DB.Actives.Field.updateTimePosition, FieldValue.serverTimestamp(),
                            DB.Actives.Field.position, mUser.getPosition(),
                            DB.Actives.Field.gps, mUser.isGps(),
                            DB.Actives.Field.updateTime, FieldValue.serverTimestamp()
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "updateUserPosition.addOnSuccessListener");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG,"updateUserPosition.addOnFailureListener.Exception: ", e);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "updateUserPosition.Exception: ", e);
        }

    }

    public void getUserInfos(){
        Tracer.log(TAG, "getUserInfos");
        try{
            mDb.collection(DB.Users.collection).document(mUser.getUid())
                    .get()
                    /*
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Tracer.log(TAG, "getUserInfos.onSuccess");
                                    //on set le data du user
                                    if(documentSnapshot.exists()) {
                                        Tracer.log(TAG, "onSuccess.DATA: " + documentSnapshot.getData());
                                        try {
                                            DataModel data = documentSnapshot.toObject(DataModel.class);
                                            mUser.setData(data);
                                        } catch (IllegalStateException ise) {
                                            Tracer.log(TAG, "getUserInfos.mUser.setData.IllegalStateException: ", ise);
                                        } catch (Exception e) {
                                            Tracer.log(TAG, "getUserInfos.mUser.setData.Exception: ", e);
                                        }
                                    }
                                }
                            })
                    */
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Tracer.log(TAG, "getUserInfos.onComplete");
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        Tracer.log(TAG, "getUserInfos.document: " +  document.exists());
                                        if(document.exists()){
                                            Tracer.log(TAG, "onComplete.DATA: " + document.getData());
                                            try {
                                                mUser.setData(document.toObject(DataModel.class));
                                            } catch (IllegalStateException ise) {
                                                Tracer.log(TAG, "getUserInfos.mUser.setData.IllegalStateException: ", ise);
                                            } catch (Exception e) {
                                                Tracer.log(TAG, "getUserInfos.mUser.setData.Exception: ", e);
                                            }
                                            //il avait deja ete cree precedement
                                            mUser.setCreated(true);
                                            //on call le tracker pour dire qu'il est pret
                                            mTrackerService.onUserCreated(new ServiceResponseObject());
                                        }else{
                                            Tracer.log(TAG, "no document, creating new user");
                                            // si on a rien alors on a un nouveau user
                                            // alors on l'enregistre dans la collection
                                            // "thekids-dab99 > users"
                                            setUserInfos();
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

    public void activateUser(){
        Tracer.log(TAG, "activateUser");
        try{
            //add the new user collection with his id
            mDb.collection(DB.Actives.collection).document(mUser.getUid())
                    .set(mUser.toActiveData())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //on set le dernier UID actif pour la verif au delete
                            Tracer.log(TAG, "activateUser.addOnSuccessListener: " + mUser.getUid());
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

    public void deactivateUser(){
        Tracer.log(TAG, "deactivateUser");
        try{
            //add the new user collection with his id
            mDb.collection(DB.Actives.collection).document(mUser.getUid())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //on set le dernier UID actif pour la verif au delete
                            Tracer.log(TAG, "deactivateUser.addOnSuccessListener: " + mUser.getUid());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "deactivateUser.addOnFailureListener.Exception: ", e);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "deactivateUser.Exception: ", e);
        }

    }

 }
