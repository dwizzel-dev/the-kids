package com.dwizzel.services;

import android.support.annotation.NonNull;
import com.dwizzel.Const;
import com.dwizzel.datamodels.ActiveModel;
import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.InviteInfoModel;
import com.dwizzel.datamodels.InviteModel;
import com.dwizzel.datamodels.InviteStateModel;
import com.dwizzel.datamodels.UserModel;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.datamodels.WatchingModel;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.utils.Tracer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
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
 *  CODE LAB:
 *  https://codelabs.developers.google.com/codelabs/firestore-android/#0
 *
 * NOTES: les onEvent sur les docuements sont trigger tout de suite apres avoir ete rajoute
 *
 */

class DatabaseService implements IDatabaseService{

    private final static String TAG = "DatabaseService";
    private FirebaseFirestore mDb;
    private UserObject mUser;
    private ITrackerService mTrackerService;

    private Map<String, ListenerRegistration> mWatchersActiveListener = new HashMap<>();
    private Map<String, ListenerRegistration> mWatchingsActiveListener = new HashMap<>();

    private ListenerRegistration mUserListener;
    private ListenerRegistration mInvitesCodeListener;
    private ListenerRegistration mInvitationsListener;
    private ListenerRegistration mWatchersListener;
    private ListenerRegistration mWatchingsListener;


    DatabaseService (ITrackerService trackerService) {
        mDb = FirebaseFirestore.getInstance();
        mUser = UserObject.getInstance();
        mTrackerService = trackerService;
        //on disable/enable la cache
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);
        }


    //listener sur le status du watching par ID de la collection "actives"
    private void addListenerOnWatchingsActive(String watchingUid){
        Tracer.log(TAG, "addListenerOnWatchingsActive: " + watchingUid);
        //trigger a chaque fois qu'il y aune modifications sur le serveur du status ou position
        mWatchingsActiveListener.put(watchingUid,
                mDb.collection("actives").document(watchingUid)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                if (e != null) {
                                    Tracer.log(TAG, "watchings-active.exception[0]", e);
                                    return;
                                }
                                if(documentSnapshot.exists()) {
                                    Tracer.data(TAG, "watchings-active[" + documentSnapshot.getId()
                                            + "]: " + documentSnapshot.getData());
                                    try {
                                        //on tranforme en activeModel et on met dans mUser
                                        mUser.updateWatchings(documentSnapshot.getId(),
                                                documentSnapshot.toObject(ActiveModel.class));
                                    }catch(Exception excpt){
                                        Tracer.log(TAG, "watchings-active[" + documentSnapshot.getId()
                                                + "].exception[1]", excpt);
                                    }
                                }else {
                                    Tracer.data(TAG, "watchings-active[" + documentSnapshot.getId()
                                            + "]: NO DATA");
                                }
                            }
                        })
        );
    }

    private void removeListenersOnWatchingsActive(){
        Tracer.log(TAG, "removeListenersOnWatchingsActive");
        if(mWatchingsActiveListener != null) {
            Iterator<Map.Entry<String, ListenerRegistration>> it = mWatchingsActiveListener.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, ListenerRegistration> entry = it.next();
                ListenerRegistration listenerRegistration = entry.getValue();
                if(listenerRegistration != null){
                    listenerRegistration.remove();
                }
                it.remove();
            }
            mWatchingsActiveListener = new HashMap<>();
        }

    }



    //listener sur le status du watchers par ID de la collection "actives"
    private void addListenerOnWatchersActive(String watcherUid){
        Tracer.log(TAG, "addListenerOnWatchersActive: " + watcherUid);
        //trigger a chaque fois qu'il y aune modifications sur le serveur du status ou position
        mWatchersActiveListener.put(watcherUid,
                mDb.collection("actives").document(watcherUid)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                if (e != null) {
                                    Tracer.log(TAG, "watchers-active.exception[0]", e);
                                    return;
                                }
                                if(documentSnapshot.exists()) {
                                    Tracer.data(TAG, "watchers-active[" + documentSnapshot.getId()
                                            + "]: " + documentSnapshot.getData());
                                    try {
                                        //on tranforme en activeModel et on met dans mUser
                                        mUser.updateWatchers(documentSnapshot.getId(),
                                                documentSnapshot.toObject(ActiveModel.class));
                                    }catch(Exception excpt){
                                        Tracer.log(TAG, "watchers-active[" + documentSnapshot.getId()
                                                + "].exception[1]: ", excpt);
                                    }
                                }else {
                                    Tracer.data(TAG, "watchers-active[" + documentSnapshot.getId()
                                            + "]: NO DATA");
                                }
                            }
                        })
        );
    }

    private void removeListenersOnWatchersActive(){
        Tracer.log(TAG, "removeListenersOnWatchersActive");
        if(mWatchersActiveListener != null) {
            Iterator<Map.Entry<String, ListenerRegistration>> it = mWatchersActiveListener.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, ListenerRegistration> entry = it.next();
                ListenerRegistration listenerRegistration = entry.getValue();
                if(listenerRegistration != null){
                    listenerRegistration.remove();
                }
                it.remove();
            }
            mWatchersActiveListener = new HashMap<>();
        }

    }



    //listener sur les invitations de la collection "users->watchers"
    private void addListenerOnWatchers(){
        Tracer.log(TAG, "addListenerOnWatchers");
        //trigger a chaque fois qu'il y aune modifications sur le serveur du status ou position
        mWatchersListener = mDb.collection("users").document(mUser.getUid()).collection("watchers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            Tracer.log(TAG, "watchers.exception[0]", e);
                            return;
                        }
                        for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            switch(dc.getType()){
                                case ADDED:
                                    Tracer.data(TAG, "watchers[" + dc.getDocument().getId() +
                                            "].ADDED: " + dc.getDocument().getData());
                                    //on rajoute a mUser
                                    if(mUser.addWatcher(dc.getDocument().getId(),
                                            dc.getDocument().toObject(WatcherModel.class))){
                                        //si pas deja la alors on met un listener
                                        addListenerOnWatchersActive(dc.getDocument().getId());
                                    }
                                    break;
                                case REMOVED:
                                    Tracer.data(TAG, "watchers[" + dc.getDocument().getId() +
                                            "].REMOVED: " + dc.getDocument().getData());
                                    //remove de la liste
                                    mUser.removeWatcher(dc.getDocument().getId());
                                    break;
                                case MODIFIED:
                                    Tracer.data(TAG, "watchers[" + dc.getDocument().getId() +
                                            "].MODIFIED: " + dc.getDocument().getData());
                                    //update les infos
                                    mUser.updateWatchers(dc.getDocument().getId(),
                                            dc.getDocument().toObject(WatcherModel.class));
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });

    }

    private void removeListenersOnWatchers(){
        Tracer.log(TAG, "removeListenersOnWatchers");
        if(mWatchersListener != null) {
            mWatchersListener.remove();
            mWatchersListener = null;
        }
    }



    //listener sur les invitations de la collection "users->watchings"
    private void addListenerOnWatchings(){
        Tracer.log(TAG, "addListenerOnWatchings");
        //trigger a chaque fois qu'il y aune modifications sur le serveur du status ou position
        mWatchingsListener = mDb.collection("users").document(mUser.getUid()).collection("watchings")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            Tracer.log(TAG, "watchings.exception[0]: ", e);
                            return;
                        }
                        for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            switch(dc.getType()){
                                case ADDED:
                                    Tracer.data(TAG, "watchings[" + dc.getDocument().getId() +
                                            "].ADDED: " + dc.getDocument().getData());
                                    //on rajoute a mUser
                                    if(mUser.addWatching(dc.getDocument().getId(),
                                            dc.getDocument().toObject(WatchingModel.class))){
                                        //si pas deja la alors on met un listener
                                        addListenerOnWatchingsActive(dc.getDocument().getId());
                                    }
                                    break;
                                case REMOVED:
                                    Tracer.data(TAG, "watchings[" + dc.getDocument().getId() +
                                            "].REMOVED: " + dc.getDocument().getData());
                                    //remove de la liste
                                    mUser.removeWatching(dc.getDocument().getId());
                                    break;
                                case MODIFIED:
                                    Tracer.data(TAG, "watchings[" + dc.getDocument().getId() +
                                            "].MODIFIED: " + dc.getDocument().getData());
                                    //update les infos
                                    mUser.updateWatchings(dc.getDocument().getId(),
                                            dc.getDocument().toObject(WatchingModel.class));
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });

    }

    private void removeListenersOnWatchings(){
        Tracer.log(TAG, "removeListenersOnWatchings");
        if(mWatchingsListener != null) {
            mWatchingsListener.remove();
            mWatchingsListener = null;
        }
    }



    //listener sur les invitations de la collection "users->invitations"
    private void addListenerOnInvitations(){
        Tracer.log(TAG, "addListenerOnInvitations");
        //trigger a chaque fois qu'il y aune modifications sur le serveur du status ou position
        mInvitationsListener = mDb.collection("users").document(mUser.getUid()).collection("invitations")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            Tracer.log(TAG, "invitations.exception[0]: ", e);
                            return;
                        }
                        for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            switch(dc.getType()){
                                case ADDED:
                                    Tracer.data(TAG, "invitations[" + dc.getDocument().getId() +
                                            "].ADDED: " + dc.getDocument().getData());
                                    //on rajoute invitation a mUser
                                    mUser.addInvitation(dc.getDocument().getId(),
                                            dc.getDocument().toObject(InvitationModel.class));
                                    break;
                                case REMOVED:
                                    Tracer.data(TAG, "invitations[" + dc.getDocument().getId() +
                                            "].REMOVED: " + dc.getDocument().getData());
                                    //si jamais on avait un listener sur un invites on l'enleve
                                    //en attente de random code number genere par le serveur
                                    removeListenerOnInvitesCode();
                                    //remove de la liste
                                    mUser.removeInvitation(dc.getDocument().getId());
                                    break;
                                case MODIFIED:
                                    Tracer.data(TAG, "invitations[" + dc.getDocument().getId() +
                                            "].MODIFIED: " + dc.getDocument().getData());
                                    //update les infos
                                    mUser.updateInvitation(dc.getDocument().getId(),
                                            dc.getDocument().toObject(InvitationModel.class));
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });

    }

    private void removeListenersOnInvitations(){
        Tracer.log(TAG, "removeListenersOnInvitations");
        if(mInvitationsListener != null) {
            mInvitationsListener.remove();
            mInvitationsListener = null;
        }
    }



    //listener sur les invitations de la collection "invites"
    //on le recoit quand le serveur genere un random number code
    private void addListenerOnInvitesCode(String inviteId){
        Tracer.log(TAG, "addListenerOnInvitesCode: " + inviteId);
        //trigger a chaque fois qu'il y aune modifications sur le serveur
        mInvitesCodeListener = mDb.collection("invites").document(inviteId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    //va surement cause une exception sur le deactivate vu
                    // que l'on signout du FirebaseAuth donc plus de Uid
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            Tracer.log(TAG, " invites.exception[0]: ", e);
                            return;
                        }
                        if(documentSnapshot.exists()) {
                            Tracer.data(TAG, "invites.code[" + documentSnapshot.getId() +
                                    "]: " + documentSnapshot.getData());
                            InviteModel inviteModel = documentSnapshot.toObject(InviteModel.class);
                            if(inviteModel.getCode() != 0){
                                HashMap<String, Object> args = new HashMap<>();
                                args.put("inviteId", documentSnapshot.getId());
                                args.put("code", String.valueOf(inviteModel.getCode()));
                                //on peut supprimer le event listener car le invites n'existe plus
                                //et a ete tranforme en watchers et watchings
                                removeListenerOnInvitesCode();
                                //c'est le code que l'on recoit alors on peut renvoyer
                                //au service qui gere le reste
                                mTrackerService.onInviteIdCreated(
                                        new ServiceResponseObject(
                                                Const.response.ON_INVITE_ID_CREATED,
                                                args
                                        ));
                            }

                        }else {
                            Tracer.data(TAG, "invites[" + documentSnapshot.getId()
                                    + "]: NO DATA");
                        }
                    }
                });
    }

    private void removeListenerOnInvitesCode(){
        Tracer.log(TAG, "removeListenerOnInvitesCode");
        if(mInvitesCodeListener != null) {
            mInvitesCodeListener.remove();
            mInvitesCodeListener = null;
        }
    }



    //listener sur les infos de mUser,
    // si jamais utilise plus de un appareil et doit etre synchro
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
                            Tracer.log(TAG, " users.exception[0]: ", e);
                            return;
                        }
                        if(documentSnapshot.exists()) {
                            Tracer.data(TAG, "users[" + documentSnapshot.getId() +
                                    "]: " + documentSnapshot.getData());
                        }else {
                            Tracer.data(TAG, "users[" + documentSnapshot.getId()
                                    + "]: NO DATA");
                        }
                    }
                });

    }

    private void removeListenerOnUser(){
        Tracer.log(TAG, "removeListenerOnUser");
        if(mUserListener != null) {
            mUserListener.remove();
            mUserListener = null;
        }
    }



    //en rapport aec le user connecte
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
                            Tracer.log(TAG, "setUserInfos.addOnFailureListener.exception: ", e);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "setUserInfos.exception: ", e);
        }

    }

    public void updateUserInfos(){
        Tracer.log(TAG, "updateUserInfos");
        try{
            // il faut que le user soit creer avant tout
            if(mUser.isCreated()) {
                WriteBatch batch = mDb.batch();
                batch.update(mDb.collection("users").document(mUser.getUid()),
                        "updateTime", FieldValue.serverTimestamp(),
                        "loginType", mUser.getLoginType(),
                        "token", mUser.getToken()
                );
                batch.set(mDb.collection("actives").document(mUser.getUid()), mUser.toActiveData());
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
                                Tracer.log(TAG, "updateUserInfos.addOnFailureListener.exception: ", e);
                            }
                        });
            }else {
                Tracer.log(TAG, "updateUserInfos: NO DATA");
            }
        }catch (Exception e){
            Tracer.log(TAG, "updateUserInfos.exception: ", e);
        }

    }

    public void updateTokenId(){
        Tracer.log(TAG, "updateTokenId");
        try{
            mDb.collection("users").document(mUser.getUid())
                    .update(
                            "token", mUser.getToken()
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "updateTokenId.addOnSuccessListener");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG,"updateTokenId.addOnFailureListener.Exception: ", e);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "updateTokenId.Exception: ", e);
        }
    }

    public void updateUserPosition(){
        Tracer.log(TAG, "updateUserPosition");
        try{
            mDb.collection("actives").document(mUser.getUid())
                    .update(
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
                            Tracer.log(TAG, "getUserInfos.addOnSuccessListener");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "getUserInfos.addOnFailureListener.exception: ", e);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    Tracer.data(TAG, "users.info[" + documentSnapshot.getId() +
                                            "]: " + documentSnapshot.getData());
                                    try {
                                        mUser.setUser(documentSnapshot.toObject(UserModel.class));
                                    } catch (Exception e) {
                                        Tracer.log(TAG, "getUserInfos.addOnCompleteListener.exception[0]: ", e);
                                    }
                                    //il avait deja ete cree precedement
                                    mUser.setCreated(true);
                                    //on call le Trackerservice pour dire qu'il est pret
                                    mTrackerService.onUserCreated(new ServiceResponseObject());
                                }else{
                                    Tracer.log(TAG, "getUserInfos: NO DATA");
                                    // si on a rien alors on a un nouveau user
                                    // alors on l'enregistre dans la collection
                                    // "thekids-dab99 > users"
                                    setUserInfos();
                                }
                            } else {
                                Tracer.log(TAG, "getUserInfos.addOnCompleteListener.exception[1]: ", task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "getUserInfos.exception: ", e);
        }
    }

    public void keepUserActive(){
        Tracer.log(TAG, "keepUserActive");
        try{
            //add the new user collection with his id
            mDb.collection("actives").document(mUser.getUid())
                    .update(mUser.toActiveData())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //on set le dernier UID actif pour la verif au delete
                            Tracer.log(TAG, "keepUserActive.addOnSuccessListener: " + mUser.getUid());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "keepUserActive.addOnFailureListener.Exception: ", e);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "keepUserActive.Exception: ", e);
        }

    }

    public void deactivateUser(){
        Tracer.log(TAG, "deactivateUser");
        try{
            //on enleve les listener sur le user
            removeListenerOnUser();
            //on enleve les listener sur le invites code
            removeListenerOnInvitesCode();
            //on enleve les listener sur les invitations
            removeListenersOnInvitations();
            //on enleve les listener sur les users->watchers
            removeListenersOnWatchers();
            //on enleve les listener sur les users->watching
            removeListenersOnWatchings();
            // enleve les listener sur les watchers status du user
            removeListenersOnWatchersActive();
            //on enleve les listener sur les watchings status du user
            removeListenersOnWatchingsActive();
            //  1. remove user collection with his id  = delete()
            //  OR
            //  2. change the status only put a listener on the status for status changes = update()
            mDb.collection("actives").document(mUser.getUid())
                    //.delete()
                    .update(mUser.toInactiveData())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //on set le dernier UID actif pour la verif au delete
                            Tracer.data(TAG, "users.deactivate[" + mUser.getUid() +
                                    "]");
                            mTrackerService.onUserSignedOut(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "deactivateUser.addOnFailureListener.exception: ", e);
                            mTrackerService.onUserSignedOut(null);
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "deactivateUser.exception: ", e);
            mTrackerService.onUserSignedOut(null);
        }

    }



    //different listing
    public void getWatchingsList(){
        Tracer.log(TAG, "getWatchingsList");
        try{
            mDb.collection("users").document(mUser.getUid()).collection("watchings")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if(!querySnapshot.isEmpty()){
                                    for(DocumentSnapshot documentSnapshot : querySnapshot) {
                                        Tracer.data(TAG, "watching.info[" + documentSnapshot.getId() +
                                                "]: " + documentSnapshot.getData());
                                        try{
                                            //rajoute la liste en interne a mUser
                                            mUser.addWatching(documentSnapshot.getId(),
                                                    documentSnapshot.toObject(WatchingModel.class));
                                            //on mets un listener sur les changement de state du watchings
                                            //si n'est pas deja dans la liste des watchers, car c'est mUser
                                            //qui va trigger un observer
                                            //le listener sur les watchings state
                                            addListenerOnWatchingsActive(documentSnapshot.getId());
                                        }catch (Exception e){
                                            Tracer.log(TAG, "getWatchingsList.addOnCompleteListener.exception[0]: ", e);
                                        }
                                    }
                                    mTrackerService.onUserWatchingsList(
                                            new ServiceResponseObject(Const.response.ON_WATCHINGS_LIST));
                                }else{
                                    Tracer.data(TAG, "watchings-list: NO DATA");
                                    mTrackerService.onUserWatchingsList(
                                            new ServiceResponseObject(Const.response.ON_EMPTY_WATCHINGS_LIST));
                                }
                                //le listeners sur les watchings
                                addListenerOnWatchings();

                            } else {
                                Tracer.log(TAG, "getWatchingsList.addOnCompleteListener.exception[1]: ",
                                        task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "getWatchingsList.exception: ", e);
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
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if(!querySnapshot.isEmpty()){
                                    for(DocumentSnapshot documentSnapshot : querySnapshot) {
                                        Tracer.data(TAG, "watcher.info[" + documentSnapshot.getId() +
                                                "]: " + documentSnapshot.getData());
                                        try{
                                            //rajoute la liste en interne a mUser
                                            mUser.addWatcher(documentSnapshot.getId(),
                                                    documentSnapshot.toObject(WatcherModel.class));
                                            //on mets un listener sur les changement de ceux qui peuvent nous watcher
                                            addListenerOnWatchersActive(documentSnapshot.getId());
                                        }catch (Exception e){
                                            Tracer.log(TAG, "getWatchersList.addOnCompleteListener.exception[0]: ", e);
                                        }
                                    }
                                    mTrackerService.onUserWatchersList(
                                            new ServiceResponseObject(Const.response.ON_WATCHERS_LIST));
                                }else{
                                    Tracer.data(TAG, "watchers-list: NO DATA");
                                    mTrackerService.onUserWatchersList(
                                            new ServiceResponseObject(Const.response.ON_EMPTY_WATCHERS_LIST));
                                }
                                //le listeners sur les watchers
                                addListenerOnWatchers();
                            } else {
                                Tracer.log(TAG, "getWatchersList.addOnCompleteListener.exception[1]: ",
                                        task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "getWatchersList.exception: ", e);
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
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if(!querySnapshot.isEmpty()){
                                    for(DocumentSnapshot documentSnapshot : querySnapshot) {
                                        Tracer.data(TAG, "invitation.info[" + documentSnapshot.getId() +
                                                "]: " + documentSnapshot.getData());
                                        try{
                                            //rajoute la liste en interne a mUser
                                            mUser.addInvitation(documentSnapshot.getId(),
                                                    documentSnapshot.toObject(InvitationModel.class));
                                            //on mets un listener sur les changement de ceux qui peuvent nous accepter
                                            //on recoit le event tout de suite apres
                                            //addListenerOnInvitations(document.getId());
                                        }catch (Exception e){
                                            Tracer.log(TAG, "getInvitationsList.addOnCompleteListener.exception[0]: ", e);
                                        }
                                    }
                                    mTrackerService.onUserInvitationsList(
                                            new ServiceResponseObject(Const.response.ON_INVITATIONS_LIST));
                                }else{
                                    Tracer.data(TAG, "invitations-list: NO DATA");
                                    mTrackerService.onUserInvitationsList(
                                            new ServiceResponseObject(Const.response.ON_EMPTY_INVITATIONS_LIST));
                                }
                                //on va mettre un listener sur la collection "invitations"
                                //pour tout changement de rajout, delete par le serveur ou userger, etc...
                                addListenerOnInvitations();

                            } else {
                                Tracer.log(TAG, "getInvitationsList.addOnCompleteListener.exception[1]: ",
                                        task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "getInvitationsList.exception: ", e);
        }
    }



    //creer une inviation avec un code pour ajouter a la liste des watchers une fois active
    public void createInviteId(){
        Tracer.log(TAG, "createInviteId");
        try{
            //add the new invites to the collection and retrieve de inviteId
            mDb.collection("invites")
                    .add(mUser.toInviteData())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Tracer.data(TAG, "invites.create[" + documentReference.getId() + "]");
                            //maintenant il est cree alors on attend que le random number soit genere
                            //alors on met un listener sur l'invites pour avoir le code pour le SMS
                            addListenerOnInvitesCode(documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "createInviteId.addOnFailureListener.exception: ", e);
                            mTrackerService.onInviteIdCreated(new ServiceResponseObject(
                                    Const.error.ERROR_INVITE_CREATION_FAILURE));
                        }
                    });

        }catch (Exception e){
            Tracer.log(TAG, "createInviteId.exception: ", e);
            mTrackerService.onInviteIdCreated(new ServiceResponseObject(
                    Const.error.ERROR_INVITE_CREATION_FAILURE));
        }

    }

    public void createInvitation(String inviteId, String name, String phone, String email){
        Tracer.log(TAG, "createInvitation: " + inviteId);
        try{
            //add the new user collection with his id
            WriteBatch batch = mDb.batch();
            batch.set(mDb.collection("users").document(mUser.getUid()).collection("invitations").document(inviteId),
                    mUser.toInvitationData(inviteId, name, phone, email)
            );
            batch.commit()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "createInvitation.addOnSuccessListener");
                            //maintenant il est cree alors on set les infos

                            //invitaion du user
                            //mUser.addInvitation(inviteId, new InvitationModel(inviteId, name, phone, email));

                            //on mets un listener sur les changement de ceux qui accepte ou refuse l'invitation
                            //on recoit le event tout de suite apres
                            //addListenerOnInvitations(inviteId);
                            //on call le service commeq quoi c'est fait
                            mTrackerService.onInvitationCreated(new ServiceResponseObject(
                                    Const.response.ON_INVITATION_CREATED));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG, "createInvitation.addOnFailureListener.Exception: ", e);
                            mTrackerService.onInvitationCreated(new ServiceResponseObject(
                                    Const.error.ERROR_INVITATION_CREATION_FAILURE));
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "createInvitation.Exception: ", e);
            mTrackerService.onInvitationCreated(new ServiceResponseObject(
                    Const.error.ERROR_INVITATION_CREATION_FAILURE));
        }

    }



    //est appele par activateInvitation une fois que le code entre est valide
    private void activateInvite(InviteInfoModel inviteInfoModel){
        Tracer.log(TAG, "activateInvite");
        //c'est bon on peut faire le call avec un update
        try{
            mDb.collection("invites")
                    .document(inviteInfoModel.getInviteId())
                    .collection("state")
                    .document(mUser.getUid())
                    .set(new InviteStateModel(inviteInfoModel.getFrom()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "activateInvite.addOnSuccessListener");
                            mTrackerService.onActivateInvite(
                                    new ServiceResponseObject(Const.response.ON_INVITE_ID_ACTIVATED));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG,"activateInvite.addOnFailureListener.Exception: ", e);
                            mTrackerService.onActivateInvite(
                                    new ServiceResponseObject(Const.error.ERROR_INVITE_ID_FAILURE));
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "activateInvite.Exception: ", e);
            mTrackerService.onActivateInvite(
                    new ServiceResponseObject(Const.error.ERROR_INVITE_ID_FAILURE));
        }

    }

    public void validateInviteCode(final String code){
        Tracer.log(TAG, "validateInviteCode: " + code);
        //c'est bon on peut faire le call avec un update
        try{
            //on va chercher le id de l'invites avec le code
            mDb.collection("invites")
                    .whereEqualTo("code", Integer.parseInt(code))
                    .limit(1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if(!querySnapshot.isEmpty()){
                                    for(DocumentSnapshot documentSnapshot : querySnapshot) {
                                        Tracer.data(TAG, "code-validate.info[" + documentSnapshot.getId() +
                                                "]: " + documentSnapshot.getData());
                                        try{
                                            //ca nous prend le "from:" aussi
                                            InviteModel inviteModel = documentSnapshot.toObject(InviteModel.class);
                                            //on a un invites ID relie au code alors on fait le update du invites
                                            //activateInvites(documentSnapshot.getId(), inviteModel.getFrom());
                                            InviteInfoModel inviteInfoModel = new InviteInfoModel(
                                                    documentSnapshot.getId(),
                                                    code,
                                                    inviteModel.getFrom(),
                                                    mUser.getUid()
                                            );
                                            mTrackerService.onValidateInviteCode(
                                                    new ServiceResponseObject(Const.response.ON_INVITE_CODE_VALIDATED,
                                                            inviteInfoModel));
                                            //vu que l'on en a juste un seul on break tout de suite
                                            break;
                                        }catch (Exception e){
                                            Tracer.log(TAG, "validateInviteCode.addOnCompleteListener.exception[0]: "
                                                    , e);
                                            mTrackerService.onValidateInviteCode(
                                                    new ServiceResponseObject(
                                                            Const.error.ERROR_INVALID_INVITE_CODE));
                                        }
                                    }
                                }else{
                                    Tracer.data(TAG, "code-validate: NO DATA");
                                    mTrackerService.onValidateInviteCode(
                                            new ServiceResponseObject(Const.error.ERROR_INVALID_INVITE_CODE));
                                }
                            } else {
                                Tracer.log(TAG, "validateInviteCode.addOnCompleteListener.exception[1]: "
                                        , task.getException());
                                mTrackerService.onValidateInviteCode(
                                        new ServiceResponseObject(
                                                Const.error.ERROR_INVALID_INVITE_CODE_FAILURE));
                            }
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "validateInviteCode.Exception: ", e);
            mTrackerService.onValidateInviteCode(
                    new ServiceResponseObject(Const.error.ERROR_INVALID_INVITE_CODE_FAILURE));
        }

    }

    //est appele par activateInvitation une fois que le code entre est valide
    public void saveInviteInfo(final InviteInfoModel inviteInfoModel){
        Tracer.log(TAG, "saveInviteInfo");
        try{
            mDb.collection("invites")
                    .document(inviteInfoModel.getInviteId())
                    .collection("infos")
                    .document(mUser.getUid())
                    .set(inviteInfoModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Tracer.log(TAG, "saveInviteInfo.addOnSuccessListener");
                            //les infos sont sauve alors on peut active le cloud functions fera le reste
                            activateInvite(inviteInfoModel);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Tracer.log(TAG,"saveInviteInfo.addOnFailureListener.Exception: ", e);
                            mTrackerService.onActivateInvite(
                                    new ServiceResponseObject(Const.error.ERROR_INVITE_INFOS_FAILURE));
                        }
                    });
        }catch (Exception e){
            Tracer.log(TAG, "saveInviteInfo.Exception: ", e);
            mTrackerService.onActivateInvite(
                    new ServiceResponseObject(Const.error.ERROR_INVITE_INFOS_FAILURE));
        }

    }


 }
