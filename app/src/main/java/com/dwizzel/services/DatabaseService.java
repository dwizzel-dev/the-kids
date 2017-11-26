package com.dwizzel.services;

import android.support.annotation.NonNull;
import com.dwizzel.Const;
import com.dwizzel.datamodels.ActiveModel;
import com.dwizzel.datamodels.DataModel;
import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.InviteModel;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.utils.Tracer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
 *
 *
 * TODO: mettre un listeners sur les watchers du users. si il change de position ou de status
 * on sera notifie
 *
 *
 * NOTES: les onEvent sur les docuements sont trigger tout de suite apres avoir ete rajoute
 *
 */

class DatabaseService implements IDatabaseService{

    private final static String TAG = "DatabaseService";
    private FirebaseFirestore mDb;
    private UserObject mUser;
    private ITrackerService mTrackerService;
    private ListenerRegistration mUserListener;
    private Map<String, ListenerRegistration> mWatchersListener
            = new HashMap<String, ListenerRegistration>();
    private Map<String, ListenerRegistration> mInvitesListener
            = new HashMap<String, ListenerRegistration>();

    DatabaseService (ITrackerService trackerService) {
        mDb = FirebaseFirestore.getInstance();
        mUser = UserObject.getInstance();
        mTrackerService = trackerService;
        boolean useCache = true;
        //on disable/enable la cache
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(useCache)
                .build();
        mDb.setFirestoreSettings(settings);
        }

    private void addListenerOnInvites(String inviteId){
        Tracer.log(TAG, "addListenerOnInvites: " + inviteId);
        //trigger a chaque fois qu'il y aune modifications sur le serveur du status ou position
        mInvitesListener.put(inviteId,
                mDb.collection("invites").document(inviteId)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshot,
                                                FirebaseFirestoreException e) {
                                if (e != null) {
                                    Tracer.log(TAG, "invites[" + documentSnapshot.getId()
                                            + "].onEvent.exception[0]: ", e);
                                    return;
                                }
                                if(documentSnapshot.exists()) {
                                    Tracer.log(TAG, "invites[" + documentSnapshot.getId() +
                                            "].onEvent.DATA ------- : " + documentSnapshot.getData());
                                    //avec le data on va pouvoir updater le status de la liste
                                    //le invites
                                    try {
                                        //on tranforme en InvitationModel et on met dans mUser
                                        mUser.updateInvitation(documentSnapshot.getId(),
                                                documentSnapshot.toObject(InviteModel.class));
                                    }catch(Exception excpt){
                                        Tracer.log(TAG, "invites[" + documentSnapshot.getId()
                                                + "].onEvent.exception[1]: ", excpt);
                                    }
                                }else {
                                    Tracer.log(TAG, "invites[" + documentSnapshot.getId() +
                                            "].onEvent.NO_DATA ------- ");
                                }
                            }
                        })
        );
    }

    private void removeListenersOnInvites(){
        Tracer.log(TAG, "removeListenersOnInvites");
        if(mInvitesListener != null) {
            Iterator<Map.Entry<String, ListenerRegistration>> it = mInvitesListener.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, ListenerRegistration> entry = it.next();
                ListenerRegistration listenerRegistration = entry.getValue();
                if(listenerRegistration != null){
                    listenerRegistration.remove();
                }
                it.remove();
            }
        }

    }

    private void addListenerOnWatchers(String watcherUid){
        Tracer.log(TAG, "addListenerOnWatchers: " + watcherUid);
        //trigger a chaque fois qu'il y aune modifications sur le serveur du status ou position
        mWatchersListener.put(watcherUid,
                mDb.collection("actives").document(watcherUid)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                if (e != null) {
                                    Tracer.log(TAG, "watchers[" + documentSnapshot.getId()
                                            + "].onEvent.exception: ", e);
                                    return;
                                }
                                if(documentSnapshot.exists()) {
                                    Tracer.log(TAG, "watchers[" + documentSnapshot.getId()
                                            + "].onEvent.DATA ------- : " + documentSnapshot.getData());
                                    try {
                                        //on tranforme en activeModel et on met dans mUser
                                        mUser.updateWatchers(documentSnapshot.getId(),
                                                documentSnapshot.toObject(ActiveModel.class));
                                    }catch(Exception excpt){
                                        Tracer.log(TAG, "watchers[" + documentSnapshot.getId()
                                                + "].onEvent.exception[1]: ", excpt);
                                    }
                                }else {
                                    Tracer.log(TAG, "watchers[" + documentSnapshot.getId()
                                            + "].onEvent.NO_DATA ------- ");
                                }
                            }
                        })
        );
    }

    private void removeListenersOnWatchers(){
        Tracer.log(TAG, "removeListenersOnWatchers");
        if(mWatchersListener != null) {
            Iterator<Map.Entry<String, ListenerRegistration>> it = mWatchersListener.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, ListenerRegistration> entry = it.next();
                ListenerRegistration listenerRegistration = entry.getValue();
                if(listenerRegistration != null){
                    listenerRegistration.remove();
                }
                it.remove();
            }
        }

    }

    private void addListenerOnUser(){
        Tracer.log(TAG, "addListenerOnUser");
        //trigger a chaque fois qu'il y aune modifications sur le serveur
        mUserListener = mDb.collection("users").document(mUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    //va surement cause une exception sur le deactivate vu
                    // que l'on signout du FirebaseAuth donc plus de Uid
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        //Tracer.log(TAG, "users.onEvent");
                        if (e != null) {
                            Tracer.log(TAG, " users.onEvent.exception: ", e);
                            return;
                        }
                        if(documentSnapshot.exists()) {
                            Tracer.log(TAG, " users.onEvent.DATA ------- : " + documentSnapshot.getData());
                        }else {
                            Tracer.log(TAG, " users.onEvent.NO_DATA ------- ");
                        }
                    }
                });

    }

    private void removeListenerOnUser(){
        Tracer.log(TAG, "removeListenerOnUser");
        if(mUserListener != null) {
            mUserListener.remove();
        }
    }

    private void setUserInfos(){
        Tracer.log(TAG, "setUserInfos");
        try{
            //add the new user collection with his id
            WriteBatch batch = mDb.batch();
            batch.set(mDb.collection("users").document(mUser.getUid()), mUser.toUserData());
            batch.set(mDb.collection("actives").document(mUser.getUid()), mUser.toActiveData());
            batch.commit()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "setUserInfos.addOnSuccessListener");
                            //maintenant il est cree alors on set et cherche les infos
                            getUserInfos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "setUserInfos.addOnFailureListener.Exception: ", e);
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
                WriteBatch batch = mDb.batch();
                batch.update(mDb.collection("users").document(mUser.getUid()), "updateTime", FieldValue.serverTimestamp(), "loginType", mUser.getLoginType());
                batch.update(mDb.collection("actives").document(mUser.getUid()), mUser.toActiveData());
                batch.commit()
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
            mDb.collection("users").document(mUser.getUid())
                    .update(
                            "updateTimePosition", FieldValue.serverTimestamp(),
                            "position", mUser.getPosition(),
                            "gps", mUser.isGps(),
                            "updateTime", FieldValue.serverTimestamp()
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
        Tracer.log(TAG, "getUserInfos: " +  mUser.getUid());
        try{
            //IMPORTANT: la query est cache si on deconnecte et reconnecte (internet)
            //il semble aller chercher les infos la au lieu de faire la query
            //donc il doit considerer comme etant encore deconnecte, avant de refaire une connection
            //ce qui peut prendre un certain temps on dirait et ce qui cause des problemes de login
            //car ne va jamais caller le TrackerService pour dire qu'il est cree
            //alors on va disable la cache pour certaine query
            //les events listener sur ce que l'on veut
            removeListenerOnUser();
            addListenerOnUser();

            mDb.collection("users").document(mUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Tracer.log(TAG, "getUserInfos.onSuccess");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "getUserInfos.onFailure.exception: ", e);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Tracer.log(TAG, "getUserInfos.onComplete");
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Tracer.log(TAG, "getUserInfos.document: " +  document.exists());
                                if(document.exists()){
                                    Tracer.log(TAG, "getUserInfos.onComplete.DATA ------- : " + document.getData());
                                    try {
                                        mUser.setData(document.toObject(DataModel.class));
                                    } catch (Exception e) {
                                        Tracer.log(TAG, "getUserInfos.mUser.setData.Exception: ", e);
                                    }
                                    //il avait deja ete cree precedement
                                    mUser.setCreated(true);
                                    //on call le Trackerservice pour dire qu'il est pret
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
            mDb.collection("actives").document(mUser.getUid())
                    .update(mUser.toActiveData())
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
            //on enleve les listener sur le user
            removeListenerOnUser();
            // enleve les listener sur les watchers du user
            removeListenersOnWatchers();
            //on enleve les listener sur les invites
            removeListenersOnInvites();
            //  1. remove user collection with his id
            //  OR
            //  2. change the status only we will put a listener on the status for status changes
            mDb.collection("actives").document(mUser.getUid())
                    //.delete()
                    .update(mUser.toInactiveData())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //on set le dernier UID actif pour la verif au delete
                            Tracer.log(TAG, "deactivateUser.addOnSuccessListener: " + mUser.getUid());
                            mTrackerService.onUserSignedOut(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "deactivateUser.addOnFailureListener.Exception: ", e);
                            mTrackerService.onUserSignedOut(null);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "deactivateUser.Exception: ", e);
            mTrackerService.onUserSignedOut(null);
        }

    }

    public void getWatchersList(){
        Tracer.log(TAG, "getWatchersList");
        try{
            mDb.collection("users").document(mUser.getUid()).collection("watchers")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Tracer.log(TAG, "getWatchersList.onComplete");
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if(!querySnapshot.isEmpty()){
                                    for(DocumentSnapshot document : querySnapshot) {
                                        Tracer.log(TAG, "getWatchersList.onComplete.DATA ------- : " +
                                                document.getId() + " | " +  document.getData());
                                        try{
                                            //rajoute la liste en interne a mUser
                                            mUser.addWatcher(document.getId(), document.toObject(WatcherModel.class));
                                            //on mets un listener sur les changement de ceux qui peuvent nous watcher
                                            addListenerOnWatchers(document.getId());
                                        }catch (Exception e){
                                            Tracer.log(TAG, "getWatchersList.onComplete.exception: ", e);
                                        }
                                    }
                                    mTrackerService.onUserWatchersList(
                                            new ServiceResponseObject(Const.response.ON_WATCHERS_LIST));
                                }else{
                                    Tracer.log(TAG, "getWatchersList.onComplete.NO_DATA ------- ");
                                    mTrackerService.onUserWatchersList(
                                            new ServiceResponseObject(Const.response.ON_EMPTY_WATCHERS_LIST));
                                }
                            } else {
                                Tracer.log(TAG, "getWatchersList.onComplete.exception: ", task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "getWatchersList.Exception: ", e);
        }
    }

    public void getInvitationsList(){
        Tracer.log(TAG, "getInvitationsList");
        try{
            mDb.collection("users").document(mUser.getUid()).collection("invitations")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Tracer.log(TAG, "getInvitationsList.onComplete");
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if(!querySnapshot.isEmpty()){
                                    for(DocumentSnapshot document : querySnapshot) {
                                        Tracer.log(TAG, "getInvitationsList.onComplete.DATA ------- : " +
                                                document.getId() + " | " +  document.getData());
                                        try{
                                            //rajoute la liste en interne a mUser
                                            mUser.addInvitation(document.getId(), document.toObject(InvitationModel.class));
                                            //on mets un listener sur les changement de ceux qui peuvent nous watcher
                                            //on recoit le event tout de suite apres
                                            addListenerOnInvites(document.getId());
                                        }catch (Exception e){
                                            Tracer.log(TAG, "getInvitationsList.onComplete.exception: ", e);
                                        }
                                    }
                                    mTrackerService.onUserInvitationsList(
                                            new ServiceResponseObject(Const.response.ON_INVITES_LIST));
                                }else{
                                    Tracer.log(TAG, "getInvitationsList.onComplete.NO_DATA ------- ");
                                    mTrackerService.onUserWatchersList(
                                            new ServiceResponseObject(Const.response.ON_EMPTY_INVITES_LIST));
                                }
                            } else {
                                Tracer.log(TAG, "getInvitationsList.onComplete.exception: ", task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "getInvitationsList.Exception: ", e);
        }
    }

 }
