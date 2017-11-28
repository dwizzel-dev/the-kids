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

import com.dwizzel.Const;
import com.dwizzel.adapters.WatchOverSomeoneListAdapter;
import com.dwizzel.datamodels.WatchingModel;
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

public class WatchOverSomeoneActivity extends BaseActivity {

    private static final String TAG = "WatchOverSomeoneActivity";
    private boolean isActivityCreated = false;
    private UserObject mUser;
    private HashMap<String, Integer> mWatchingsPair;
    private RecyclerView mRecyclerView;

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici
    }

    public void onSubCreate(){
        Tracer.log(TAG, "onSubCreate");
        //pas qu'il recommence au onStart
        if(!isActivityCreated) {
            setContentView(R.layout.activity_watch_over_someone);
            setTitle(R.string.watch_over_someone_title);
            //setFloatingActionButton();
            //set un nouveau callback au lieu de celui de BaseActivity
            //vu qu'il va recevoir une notif quand aura ca liste de Watchers et de Invites
            setTrackerBinderCallback();
            //on cherche la list des Watching On
            try {
                getTrackerBinder().getWatchingsList();
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
                Intent intent = new Intent(WatchOverSomeoneActivity.this,
                        ActivateInvitationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setTrackerBinderCallback(){
        //on peut le caller sinon l'activity ne serait meme pas partis
        //on overwrite celui de BaseActivity
        ITrackerBinderCallback serviceCallback = new ITrackerBinderCallback() {
            private static final String TAG = "WatchOverSomeoneActivity.ITrackerBinder";
            public void handleResponse(ServiceResponseObject sro){
                Tracer.log(TAG, "handleResponse: " + sro);
                if(sro.getErr() == 0){
                    Tracer.log(TAG, "handleResponse: " + sro.getMsg());
                    switch(sro.getMsg()){
                        case Const.response.ON_WATCHINGS_LIST:
                        case Const.response.ON_EMPTY_WATCHINGS_LIST:
                            //ca nous prend un ou l'autre
                            contentListLoaded();
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
        TrackerService.TrackerBinder trackerBinder = getTrackerBinder();
        //on enleve le precedenet callback de BaseActivity
        trackerBinder.unregisterCallback();
        //set le nouveau callback qui overwrite celui de BaseActivity
        trackerBinder.registerCallback(serviceCallback);
    }

    private void contentListLoaded() {
        Tracer.log(TAG, "contentListLoaded");
        //on enleve le loader
        final View loader = findViewById(R.id.loading_spinner);
        Animation animLoader = AnimationUtils.loadAnimation(WatchOverSomeoneActivity.this,
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
                showWatchOverSomeoneListView();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                Tracer.log(TAG, "contentListOnLoad.onAnimationRepeat");
            }
        });
        loader.startAnimation(animLoader);

    }

    private void showWatchOverSomeoneListView(){
        Tracer.log(TAG, "showWatchOverSomeoneListView");
        mRecyclerView = new RecyclerView(WatchOverSomeoneActivity.this);
        mRecyclerView.setPaddingRelative(0,0,0,0);
        mRecyclerView.setHorizontalScrollBarEnabled(true);
        mRecyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        //mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WatchOverSomeoneActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new WatchOverSomeoneListAdapter(createWatchingsList());
        // specify an adapter
        mRecyclerView.setAdapter(adapter);
        //le padding de 8px au dessus et dessous
        mRecyclerView.addItemDecoration(new ListPaddingDecoration(WatchOverSomeoneActivity.this));
        //l'animation
        mRecyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(WatchOverSomeoneActivity.this,
                        R.anim.layout_animation_slide_from_bottom));
        //rajoute a la view principale
        CoordinatorLayout layout = findViewById(R.id.bottomView);
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
                        case Const.notif.WATCHING_UPDATE:
                            updateWatchingsListSingleViewItem((String)observerNotifObject.getValue());
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    private ArrayList<ListItems.Item> createWatchingsList(){
        Tracer.log(TAG, "createWatchingsList");
        ArrayList<ListItems.Item> list = new ArrayList<>();
        mWatchingsPair = new HashMap<>();
        try {
            int pos = 0;
            //on met un header pour les watchers
            list.add(new ListItems.HeaderItem(getResources().getString(R.string.watchings_header)));
            pos++;
            if(mUser.getWatchings() != null && !mUser.getWatchings().isEmpty()) {
                //on fill la list avec les watchers
                for (String uid : mUser.getWatchings().keySet()) {
                    list.add(new ListItems.WatchingItem(uid));
                    mWatchingsPair.put(uid, pos++);
                }
            }else{
                list.add(new ListItems.TextItem(getResources().getString(R.string.watchings_list_empty)));
            }

        }catch(Exception e){
            Tracer.log(TAG, "createWatchingsList.exception: ", e);
        }
        return list;
    }


    private void updateWatchingsListSingleViewItem(String uid){
        Tracer.log(TAG, "updateWatchingsListSingleViewItem: " + uid);
        //get la position selon le uid avec le array ref/pos list
        WatchingModel watchingModel = mUser.getWatching(uid);
        if(mWatchingsPair.containsKey(uid) && watchingModel != null) {
            //avec la position on cherche la view
            View itemView = mRecyclerView.getLayoutManager()
                    .findViewByPosition(mWatchingsPair.get(uid));
            if(itemView != null){
                //changer l'etat
                ImageView image = itemView.findViewById(R.id.imageView);
                switch(watchingModel.getStatus()){
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


}