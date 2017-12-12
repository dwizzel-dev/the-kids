package com.dwizzel.thekids;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dwizzel.Const;
import com.dwizzel.adapters.IRecyclerViewItemClickListener;
import com.dwizzel.adapters.WatchOverMeListAdapter;
import com.dwizzel.adapters.WatchOverSomeoneListAdapter;
import com.dwizzel.objects.ListItems;
import com.dwizzel.objects.ObserverNotifObject;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.ListPaddingDecoration;
import com.dwizzel.utils.Tracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/*
* NOTES:
* https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Recycler.html#getViewForPosition(int)
* https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#notifyItemChanged(int)
*
* */

public class WatchOverMeActivity extends BaseActivity implements IRecyclerViewItemClickListener.ActivityClickListener {

    private static final String TAG = "WatchOverMeActivity";
    private boolean isActivityCreated = false;
    private boolean isWatchersLoaded = false;
    private boolean isInvitationsLoaded = false;
    private UserObject mUser;
    private HashMap<String, Integer> mWatchersPair;
    private HashMap<String, Integer> mInvitationsPair;
    private RecyclerView mRecyclerView;
    TrackerService.TrackerBinder mTrackerBinder;

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici
        //TODO: on enleve les observer de mUser car plus besoin
    }

    public void onSubCreate(){
        Tracer.log(TAG, "onSubCreate");
        //pas qu'il recommence au onStart
        if(!isActivityCreated) {
            setContentView(R.layout.activity_watch_over_me);
            setTitle(R.string.watch_over_me_title);
            setFloatingActionButton();
            //set un nouveau callback au lieu de celui de BaseActivity
            //vu qu'il va recevoir une notif quand aura ca liste de Watchers et de Invites
            setTrackerBinderCallback();
            //on cherche la list
            try {
                mTrackerBinder.getWatchersList();
                mTrackerBinder.getInvitationsList();
            } catch (NullPointerException npe) {
                Tracer.log(TAG, "onSubCreate.NullPointerException: ", npe);
            }
            mUser = UserObject.getInstance();
        }
        isActivityCreated = true;
    }

    private void setFloatingActionButton(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(WatchOverMeActivity.this, SendInvitationForWatchingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setTrackerBinderCallback(){
        //on peut le caller sinon l'activity ne serait meme pas partis
        //on overwrite celui de BaseActivity
        ITrackerBinderCallback serviceCallback = new ITrackerBinderCallback() {
            private static final String TAG = "WatchOverMeActivity.ITrackerBinder";
            public void handleResponse(ServiceResponseObject sro){
                Tracer.log(TAG, "handleResponse", sro);
                if(sro.getErr() == 0){
                    switch(sro.getMsg()){
                        case Const.response.ON_WATCHERS_LIST:
                        case Const.response.ON_EMPTY_WATCHERS_LIST:
                            isWatchersLoaded = true;
                            //ca nous prend les 2, watchers et invitations
                            if(isContentLoaded()) {
                                contentListLoaded();
                            }
                            break;
                        case Const.response.ON_INVITATIONS_LIST:
                        case Const.response.ON_EMPTY_INVITATIONS_LIST:
                            isInvitationsLoaded = true;
                            //ca nous prend les 2, watchers et invitations
                            if(isContentLoaded()) {
                                contentListLoaded();
                            }
                            break;
                        default:
                            break;
                    }
                }else if(sro.getErr() == Const.conn.NOT_CONNECTED){
                    Tracer.log(TAG, "handleResponse: NOT_CONNECTED");
                }else if(sro.getErr() == Const.conn.RECONNECTED){
                    Tracer.log(TAG, "handleResponse: RECONNECTED");
                }
            }
            public void onSignedIn(ServiceResponseObject sro){}
            public void onSignedOut(ServiceResponseObject sro){}
            public void onCreated(ServiceResponseObject sro){}
        };
        //get le binder
        mTrackerBinder = getTrackerBinder();
        //on enleve le precedenet callback de BaseActivity
        mTrackerBinder.unregisterCallback();
        //set le nouveau callback qui overwrite celui de BaseActivity
        mTrackerBinder.registerCallback(serviceCallback);
    }

    private boolean isContentLoaded(){
        return (isInvitationsLoaded && isWatchersLoaded);
    }

    private void contentListLoaded() {
        Tracer.log(TAG, "contentListLoaded");
        //on enleve le loader
        final View loader = findViewById(R.id.loading_spinner);
        Animation animLoader = AnimationUtils.loadAnimation(WatchOverMeActivity.this,
                R.anim.loader_animation_from_center_to_top_fade);
        animLoader.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Tracer.log(TAG, "contentListOnLoad.onAnimationStart");
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Tracer.log(TAG, "contentListOnLoad.onAnimationEnd");
                loader.setVisibility(View.INVISIBLE);
                showWatchOverMeListView();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                Tracer.log(TAG, "contentListOnLoad.onAnimationRepeat");
            }
        });
        loader.startAnimation(animLoader);

    }

    private void showWatchOverMeListView(){
        Tracer.log(TAG, "showWatchOverMeListView");
        mRecyclerView = new RecyclerView(WatchOverMeActivity.this);
        mRecyclerView.setPaddingRelative(0,0,0,0);
        mRecyclerView.setHorizontalScrollBarEnabled(true);
        mRecyclerView.setLayoutParams(
                new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.MATCH_PARENT
                )
        );
        //mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        WatchOverMeActivity.this
                )
        );
        // specify an adapter
        mRecyclerView.setAdapter(
                new WatchOverMeListAdapter(
                        createWatchersList(),
                        WatchOverMeActivity.this
                )
        );
        setRecycleViewClickListener();
        //le padding de 8px au dessus et dessous
        mRecyclerView.addItemDecoration(
                new ListPaddingDecoration(
                        WatchOverMeActivity.this
                )
        );
        //l'animation
        mRecyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(
                        WatchOverMeActivity.this,
                        R.anim.layout_animation_slide_from_bottom
                )
        );
        //rajoute a la view principale
        CoordinatorLayout layout = findViewById(R.id.mainView);
        layout.addView(mRecyclerView, layout.getChildCount()); //en dessous du floating button
        //observer sur le user
        setUserObserver();
    }

    private void setUserObserver(){
        Tracer.log(TAG, "setUserObserver");
        //on enleve ceux deja mis avant au cas ou
        if(mUser.countObservers() > 0) {
            mUser.deleteObservers();
        }
        //on met un observer sur les possible modifs de watchers et invitations
        mUser.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                ObserverNotifObject observerNotifObject = (ObserverNotifObject)o;
                if(observerNotifObject != null){
                    //test case
                    switch (observerNotifObject.getType()){
                        case Const.notif.WATCHER_UPDATE:
                            updateWatchers((String)observerNotifObject.getValue());
                            break;
                        case Const.notif.WATCHER_REMOVE:
                            removeWatchers((String)observerNotifObject.getValue());
                            break;
                        case Const.notif.WATCHER_ADDED:
                            addWatchers((String)observerNotifObject.getValue());
                            break;
                        case Const.notif.INVITATION_UPDATE:
                            break;
                        case Const.notif.INVITATION_REMOVE:
                            removeInvitations((String)observerNotifObject.getValue());
                            break;
                        case Const.notif.INVITATION_ADDED:
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    private ArrayList<ListItems.Item> createWatchersList(){
        Tracer.log(TAG, "createWatchersList");
        ArrayList<ListItems.Item> list = new ArrayList<>();
        mWatchersPair = new HashMap<>();
        mInvitationsPair = new HashMap<>();
        try {
            int pos = 0;
            //on met un header pour les watchers
            list.add(new ListItems.HeaderItem(getResources().getString(R.string.watchers_header)));
            pos++;
            if(mUser.getWatchers() != null && !mUser.getWatchers().isEmpty()) {
                //on fill la list avec les watchers
                for (String uid : mUser.getWatchers().keySet()) {
                    list.add(new ListItems.WatcherItem(uid));
                    mWatchersPair.put(uid, pos++);
                }
            }else{
                list.add(new ListItems.TextItem(getResources().getString(R.string.watchers_list_empty)));
                pos++;
            }
            //on met un header pour les invitations
            list.add(new ListItems.HeaderItem(getResources().getString(R.string.invitations_header)));
            pos++;
            //on fill la list avec les invitationa
            if(mUser.getInvitations() != null && !mUser.getInvitations().isEmpty()) {
                for (String inviteId : mUser.getInvitations().keySet()) {
                    list.add(new ListItems.InvitationItem(inviteId));
                    mInvitationsPair.put(inviteId, pos++);
                }
            }else{
                list.add(new ListItems.TextItem(getResources().getString(R.string.invitations_list_empty)));
            }
        }catch(Exception e){
            Tracer.log(TAG, "createWatchersList.exception: ", e);
        }
        return list;
    }

    private void updateWatchers(String uid){
        Tracer.log(TAG, "updateWatchers: " + uid);
        if(mWatchersPair.containsKey(uid)){
            try {
                mRecyclerView.getAdapter().notifyItemChanged(mWatchersPair.get(uid));
            }catch(Exception e){
                Tracer.log(TAG, "updateWatchers.exception: ", e);
            }
        }
    }

    private void removeWatchers(String uid){
        Tracer.log(TAG, "removeWatchers: " + uid);
        if(mWatchersPair.containsKey(uid)){
            try {
                int removedPos = mWatchersPair.get(uid);
                //on le retire en callant la methode de l'adapter qui lui fera le clean de sa liste
                ((WatchOverMeListAdapter)mRecyclerView.getAdapter()).removeItem(removedPos);
                //on le retire de la liste des pairs
                mWatchersPair.remove(uid);
                //si plus rien dans le mWatchersPair,
                // alors on doit remettre le message de base apres le watchers title
                if(mWatchersPair.size() == 0){
                    //il remplace la position du dernier watchers enleve avec le text
                    //donc on a pas besoin de decaler les mInvitationsPair
                    ((WatchOverMeListAdapter)mRecyclerView.getAdapter()).addItem(
                            ListItems.Type.TYPE_TEXT,
                            new ListItems.TextItem(getResources().getString(R.string.watchers_list_empty)),
                            removedPos
                            ); //1 car tout de suite apres le watchers title
                }else{
                    //si non il faut recalculer les positions
                    int pos = 1; // 1 = tout de suite apres le titre
                    for(String invitationUid : mWatchersPair.keySet()){
                        mWatchersPair.put(invitationUid, pos++);
                    }
                    //si non il faut decaler les mInvitationsPair aussi
                    //on decale les mInvitationPair de -1
                    for(String invitationUid : mInvitationsPair.keySet()){
                        mInvitationsPair.put(invitationUid, (mInvitationsPair.get(invitationUid) - 1));
                    }
                }
            }catch(Exception e){
                Tracer.log(TAG, "removeWatchers.exception: ", e);
            }
        }
    }

    private void addWatchers(String uid){
        Tracer.log(TAG, "addWatchers: " + uid);

        //0 = watchers title
        //1 to n = watcher item ou text item si liste watchers vide
        //n + 1 = invitation title
        //n to m = inviation item ou text item si liste invitation vide

        if(!mWatchersPair.containsKey(uid)){
            try {
                //il est deja rajouter a mUser
                //on va chercher la derniere position des mWatcherPair
                int size = mWatchersPair.size();
                if(size > 0){
                    //si on a au moins un item on le rajoute a la fin de la liste
                    mWatchersPair.put(uid, (size + 1)); //+1 le titre watchers
                    //on decale les mInvitationPair de +1
                    for (String invitationUid : mInvitationsPair.keySet()) {
                        mInvitationsPair.put(invitationUid, (mInvitationsPair.get(invitationUid) + 1));
                    }
                    //on le rajoute a la liste du adapter
                    //on va chercher sa nouvelle position dans le mList du adapter
                    // +1 car il y a le Watchers Title Item
                    ((WatchOverMeListAdapter)mRecyclerView.getAdapter()).addItem(
                            ListItems.Type.TYPE_WATCHER,
                            new ListItems.WatcherItem(uid),
                            (size + 1)
                            );
                }else{
                    //si la liste de watchers est vide
                    //0 = watchers title
                    //1 = text items car la liste est vide,
                    // maintenant il faut le remplacer par le watcher et virer le text item
                    //donc on a pas besoin de decaler les mInvitationsPair
                    mWatchersPair.put(uid, 1); //1 car le watchers title est en premier
                    ((WatchOverMeListAdapter)mRecyclerView.getAdapter()).removeItem(1);
                    ((WatchOverMeListAdapter)mRecyclerView.getAdapter()).addItem(
                            ListItems.Type.TYPE_WATCHER,
                            new ListItems.WatcherItem(uid),
                            1
                            );
                }
            }catch(Exception e){
                Tracer.log(TAG, "addWatchers.exception: ", e);
            }
        }
    }

    private void removeInvitations(String uid){
        Tracer.log(TAG, "removeInvitations: " + uid);
        if(mInvitationsPair.containsKey(uid)){
            try {
                //on le retire en callant la methode de l'adapter qui lui fera le clean de sa liste
                int removedPos = mInvitationsPair.get(uid);
                ((WatchOverMeListAdapter)mRecyclerView.getAdapter()).removeItem(removedPos);
                //on le retire de la liste des pairs
                mInvitationsPair.remove(uid);
                //si plus rien dans le mInviationsPair,
                // alors on doit remettre le message de base apres le watchers title
                if(mInvitationsPair.size() == 0){
                    //il remplace la position du dernier invitations enleve
                    ((WatchOverMeListAdapter)mRecyclerView.getAdapter()).addItem(
                            ListItems.Type.TYPE_TEXT,
                            new ListItems.TextItem(getResources().getString(R.string.invitations_list_empty)),
                            removedPos
                    );
                }else{
                    //si non il faut decaler les mInvitationsPair
                    //on decale les mInvitationPair de -1
                    for(String invitationUid : mInvitationsPair.keySet()){
                        mInvitationsPair.put(invitationUid, (mInvitationsPair.get(invitationUid) - 1));
                    }
                }

            }catch(Exception e){
                Tracer.log(TAG, "removeWatchers.exception: ", e);
            }
        }
    }

    public void onRecycleViewItemClick(int position, String uid, int type){
        Tracer.log(TAG, "onItemClick[" + position + ":" + type + "]: " + uid);
        switch(type){
            case IRecyclerViewItemClickListener.TYPE_DELETE_ITEM_WATCHER:
                //on enleve sur les serveur DB aussi
                mTrackerBinder.deleteWatchersItem(uid);
                break;
            case IRecyclerViewItemClickListener.TYPE_MODIFY_ITEM_WATCHER:
                break;
            default:
                break;
        }
    }

    public void setRecycleViewClickListener(){
        //get de l'adapter
        ((WatchOverMeListAdapter)mRecyclerView.getAdapter()).setClickListener(WatchOverMeActivity.this);
    }


}
