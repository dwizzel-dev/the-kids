package com.dwizzel.thekids;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.dwizzel.Const;
import com.dwizzel.adapters.WatchersListAdapter;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.ListPaddingDecoration;
import com.dwizzel.utils.Tracer;
import com.dwizzel.utils.Utils;

import java.util.ArrayList;

public class WatchOverMeActivity extends BaseActivity {

    private static final String TAG = "WatchOverMeActivity";
    private TrackerService.TrackerBinder mTrackerBinder;
    private boolean isActivityCreated = false;
    private UserObject mUser = UserObject.getInstance();

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
        }
        isActivityCreated = true;
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
                            setWatchersListView();
                            break;
                        case Const.response.ON_INVITES_LIST:
                            setInvitesListView();
                            break;
                        default:
                            break;
                    }
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

    private void setWatchersListView() {
        mRecyclerView = findViewById(R.id.rvWatcher);
        //mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(WatchOverMeActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new WatchersListAdapter(
                new ArrayList<WatcherModel>(UserObject.getInstance().getWatchers().values()));
        mRecyclerView.setAdapter(mAdapter);
        //le padding de 8px au dessus et dessous
        mRecyclerView.addItemDecoration(new ListPaddingDecoration(WatchOverMeActivity.this));
        //l'aniamation
        //int resId = R.anim.layout_animation_fall_down;
        int resId = R.anim.layout_animation_slide_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(WatchOverMeActivity.this, resId);
        mRecyclerView.setLayoutAnimation(animation);


    }

    private void setInvitesListView(){

    }



}
