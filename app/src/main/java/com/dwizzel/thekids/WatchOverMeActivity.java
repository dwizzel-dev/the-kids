package com.dwizzel.thekids;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dwizzel.Const;
import com.dwizzel.adapters.WatchOverMeListAdapter;
import com.dwizzel.adapters.WatchersListAdapter;
import com.dwizzel.adapters.WatchersListAdapterV1;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.ListPaddingDecoration;
import com.dwizzel.utils.Tracer;

import java.util.ArrayList;

public class WatchOverMeActivity extends BaseActivity {

    private static final String TAG = "WatchOverMeActivity";
    private TrackerService.TrackerBinder mTrackerBinder;
    private boolean isActivityCreated = false;
    private boolean isWatchersLoaded = false;
    private boolean isInvitationsLoaded = false;

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
                            isWatchersLoaded = true;
                            //ca nous prend les 2, watchers et invitations
                            if(isContentLoaded()) {
                                contentListLoaded();
                            }
                            break;
                        case Const.response.ON_INVITES_LIST:
                            isInvitationsLoaded = true;
                            //ca nous prend les 2, watchers et invitations
                            if(isContentLoaded()) {
                                contentListLoaded();
                            }
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
        // specify an adapter (see also next example)
        mAdapter = new WatchOverMeListAdapter(WatchOverMeActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        //le padding de 8px au dessus et dessous
        mRecyclerView.addItemDecoration(new ListPaddingDecoration(WatchOverMeActivity.this));
        //l'aniamation
        mRecyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(WatchOverMeActivity.this,
                        R.anim.layout_animation_slide_from_bottom));
        //rajoute a la view principale
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.mainView);
        layout.addView(mRecyclerView, layout.getChildCount()); //en dessous du floating button
    }


}
