package com.dwizzel.thekids;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.adapters.WatchOverMeListAdapter;
import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.WatcherModel;
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
*
* */

public class WatchOverMeActivity extends BaseActivity {

    private static final String TAG = "WatchOverMeActivity";
    private TrackerService.TrackerBinder mTrackerBinder;
    private boolean isActivityCreated = false;
    private boolean isWatchersLoaded = false;
    private boolean isInvitationsLoaded = false;
    private UserObject mUser;

    private HashMap<String, Integer> mWatchersPair;
    private HashMap<String, Integer> mInvitationsPair;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici
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
                getTrackerBinder().getWatchersList();
                getTrackerBinder().getInvitationsList();
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
                Tracer.log(TAG, "handleResponse: " + sro);
                if(sro.getErr() == 0){
                    Tracer.log(TAG, "handleResponse: " + sro.getMsg());
                    switch(sro.getMsg()){
                        case Const.response.ON_WATCHERS_LIST:
                        case Const.response.ON_EMPTY_WATCHERS_LIST:
                            isWatchersLoaded = true;
                            //ca nous prend les 2, watchers et invitations
                            if(isContentLoaded()) {
                                contentListLoaded();
                            }
                            break;
                        case Const.response.ON_INVITES_LIST:
                        case Const.response.ON_EMPTY_INVITES_LIST:
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
        mRecyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        //mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(WatchOverMeActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        mAdapter = new WatchOverMeListAdapter(createWatchersList());
        mRecyclerView.setAdapter(mAdapter);
        //le padding de 8px au dessus et dessous
        mRecyclerView.addItemDecoration(new ListPaddingDecoration(WatchOverMeActivity.this));
        //l'animation
        mRecyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(WatchOverMeActivity.this,
                        R.anim.layout_animation_slide_from_bottom));
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
                    Tracer.log(TAG, String.format("mUser.update: %s = %s",
                            observerNotifObject.getType(),
                            observerNotifObject.getValue()));
                    //test case
                    switch (observerNotifObject.getType()){
                        case Const.notif.WATCHER_UPDATE:
                            updateWatchersListSingleViewItem((String)observerNotifObject.getValue());
                            break;
                        case Const.notif.INVITATION_UPDATE:
                            updateInvitationsListSingleViewItem((String)observerNotifObject.getValue());
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
                pos++;
            }
        }catch(Exception e){
            Tracer.log(TAG, "createWatchersList.exception: ", e);
        }
        return list;
    }


    private void updateWatchersListSingleViewItem(String uid){
        Tracer.log(TAG, "updateWatchersListSingleViewItem: " + uid);
        //get la position selon le uid avec le array ref/pos list
        WatcherModel watcherModel = mUser.getWatcher(uid);
        if(mWatchersPair.containsKey(uid) && watcherModel != null) {
            //avec la position on cherche la view
            View itemView = mRecyclerView.getLayoutManager()
                    .findViewByPosition(mWatchersPair.get(uid));
            if(itemView != null){
                //changer l'etat
                ImageView image = itemView.findViewById(R.id.imageView);
                switch(watcherModel.getStatus()){
                    case Const.status.OFFLINE:
                        image.setImageResource(R.drawable.icon_person_offline);
                        break;
                    case Const.status.ONLINE:
                        image.setImageResource(R.drawable.icon_person_watcher);
                        break;
                    case Const.status.OCCUPIED:
                        image.setImageResource(R.drawable.icon_person_occupied);
                        break;
                    default:
                        image.setImageResource(R.drawable.icon_person_watcher);
                        break;
                }
            }
        }
    }

    private void updateInvitationsListSingleViewItem(String invitationId){
        Tracer.log(TAG, "updateInvitationsListSingleViewItem: " + invitationId);
        //get la position selon le uid avec le array ref/pos list
        InvitationModel invitationModel = mUser.getInvitation(invitationId);
        if(mInvitationsPair.containsKey(invitationId) && invitationModel != null) {
            //avec la position on cherche la view
            View itemView = mRecyclerView.getLayoutManager()
                    .findViewByPosition(mInvitationsPair.get(invitationId));
            if(itemView != null){
                //changer l'etat
                switch(invitationModel.getState()){
                    case Const.invitation.ACCEPTED:
                        break;
                    case Const.invitation.PENDING:
                        break;
                    case Const.invitation.REFUSED:
                        break;
                    default:
                        break;
                }
            }
        }
    }


}
