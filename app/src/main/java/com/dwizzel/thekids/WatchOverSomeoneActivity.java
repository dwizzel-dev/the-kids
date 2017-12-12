package com.dwizzel.thekids;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.adapters.WatchOverSomeoneListAdapter;
import com.dwizzel.datamodels.InviteInfoModel;
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

public class WatchOverSomeoneActivity extends BaseActivity{

    private static final String TAG = "WatchOverSomeoneActivity";
    private boolean isActivityCreated = false;
    private UserObject mUser;
    private HashMap<String, Integer> mWatchingsPair;
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
            setContentView(R.layout.activity_watch_over_someone);
            setTitle(R.string.watch_over_someone_title);
            setButton();
            //set un nouveau callback au lieu de celui de BaseActivity
            //vu qu'il va recevoir une notif quand aura ca liste de Watchers et de Invites
            setTrackerBinderCallback();
            //on cherche la list des Watching On
            try {
                mTrackerBinder.getWatchingsList();
            } catch (NullPointerException npe) {
                Tracer.log(TAG, "onSubCreate.NullPointerException: ", npe);
            }
            mUser = UserObject.getInstance();
        }
        isActivityCreated = true;
    }

    private void setButton() {
        Button butt = findViewById(R.id.buttValidate);
        //butt create
        butt.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //on call la function et on met le loader
                        checkMandatoryFieldsAndValidate();

                    }
                });

    }

    private void checkMandatoryFieldsAndValidate(){
        displayErrMsg(Const.error.NO_ERROR);
        //on va chercher les infos et on les sets
        String code = String.format("%s",((EditText)findViewById(R.id.txtInviteCode)).getText());
        //minor check
        if(code.isEmpty()){
            displayErrMsg(R.string.err_invalid_invite_code);
            return;
        }
        //on a le tout allors on fait le call au service
        showSpinner(true);
        //mTrackerBinder.activateInvites(mInviteId);
        mTrackerBinder.validateInviteCode(code);

    }

    private void setTrackerBinderCallback(){
        //on peut le caller sinon l'activity ne serait meme pas partis
        //on overwrite celui de BaseActivity
        ITrackerBinderCallback serviceCallback = new ITrackerBinderCallback() {
            private static final String TAG = "WatchOverSomeoneActivity.ITrackerBinder";
            public void handleResponse(ServiceResponseObject sro){
                Tracer.log(TAG, "handleResponse", sro);
                if(sro.getErr() == 0) {
                    switch (sro.getMsg()) {
                        case Const.response.ON_WATCHINGS_LIST:
                        case Const.response.ON_EMPTY_WATCHINGS_LIST:
                            //ca nous prend un ou l'autre
                            contentListLoaded();
                            break;
                        //case Const.response.ON_INVITE_ID_ACTIVATED:
                        case Const.response.ON_INVITE_CODE_VALIDATED:
                            //les args du inviteId et FromUid
                            //ca nous prend un ou l'autre
                            createNicknameForActivation((InviteInfoModel)sro.getObj());
                            break;
                        default:
                            break;
                    }
                }else{
                    switch (sro.getErr()) {
                        case Const.conn.NOT_CONNECTED:
                            Tracer.log(TAG, "handleResponse: NOT CONNECTED");
                            break;
                        case Const.conn.RECONNECTED:
                            Tracer.log(TAG, "handleResponse: RECONNECTED");
                            break;
                        case Const.conn.RECONNECTING:
                            Tracer.log(TAG, "handleResponse: RECONNECTING");
                            break;
                        case Const.error.ERROR_INVALID_INVITE_CODE_FAILURE:
                            displayErrMsg(R.string.err_invalid_invite_id_failure);
                            break;
                        case Const.error.ERROR_INVALID_INVITE_CODE:
                            displayErrMsg(R.string.err_invalid_invite_code);
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

    private void createNicknameForActivation(InviteInfoModel inviteInfoModel){
        Tracer.log(TAG, "createNicknameForActivation", inviteInfoModel);
        //on va a edition de profil
        Intent intent = new Intent(WatchOverSomeoneActivity.this,
                ActivateInvitationActivity.class);
        //on set l'objet a a passer
        intent.putExtra("inviteInfo", inviteInfoModel);
        //start activity and clear the backStack
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //on arrete le loader et clean le reste au cas d'un backstack
        //this.finish();
        showSpinner(false);
        ((EditText)findViewById(R.id.txtInviteCode)).setText("");

    }

    public void displayErrMsg(int msgId){
        showSpinner(false);
        TextView txtView = findViewById(R.id.errMsg);
        if(msgId != 0) {
            txtView.setText(msgId);
        }else {
            txtView.setText("");
        }
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
        RecyclerView.Adapter adapter = new WatchOverSomeoneListAdapter(createWatchingsList(),
                WatchOverSomeoneActivity.this);
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
                    //test case
                    switch (observerNotifObject.getType()){
                        case Const.notif.WATCHING_UPDATE:
                            updateWatchings((String)observerNotifObject.getValue());
                            break;
                        case Const.notif.WATCHING_REMOVE:
                            removeWatchings((String)observerNotifObject.getValue());
                            break;
                        case Const.notif.WATCHING_ADDED:
                            addWatchings((String)observerNotifObject.getValue());
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

    private void updateWatchings(String uid){
        Tracer.log(TAG, "updateWatchings: " + uid);
        if(mWatchingsPair.containsKey(uid)){
            try {
                mRecyclerView.getAdapter().notifyItemChanged(mWatchingsPair.get(uid));
            }catch(Exception e){
                Tracer.log(TAG, "updateWatchings.exception: ", e);
            }
        }
    }

    private void removeWatchings(String uid){
        Tracer.log(TAG, "removeWatchings: " + uid);
        if(mWatchingsPair.containsKey(uid)){
            try {
                int removedPos = mWatchingsPair.get(uid);
                ((WatchOverSomeoneListAdapter)mRecyclerView.getAdapter()).removeItem(removedPos);
                mWatchingsPair.remove(uid);
                if(mWatchingsPair.size() == 0){
                    ((WatchOverSomeoneListAdapter)mRecyclerView.getAdapter()).addItem(
                            ListItems.Type.TYPE_TEXT,
                            new ListItems.TextItem(getResources().getString(R.string.watchings_list_empty)),
                            removedPos
                            );
                }else{
                    int pos = 1;
                    for(String invitationUid : mWatchingsPair.keySet()){
                        mWatchingsPair.put(invitationUid, pos++);
                    }
                }
            }catch(Exception e){
                Tracer.log(TAG, "removeWatchings.exception: ", e);
            }
        }
    }

    private void addWatchings(String uid){
        Tracer.log(TAG, "addWatchings: " + uid);
        if(!mWatchingsPair.containsKey(uid)){
            try {
                int size = mWatchingsPair.size();
                if(size > 0){
                    mWatchingsPair.put(uid, (size + 1));
                    ((WatchOverSomeoneListAdapter)mRecyclerView.getAdapter()).addItem(
                            ListItems.Type.TYPE_WATCHING,
                            new ListItems.WatchingItem(uid),
                            (size + 1)
                            );
                }else{
                    mWatchingsPair.put(uid, 1);
                    ((WatchOverSomeoneListAdapter)mRecyclerView.getAdapter()).removeItem(1);
                    ((WatchOverSomeoneListAdapter)mRecyclerView.getAdapter()).addItem(
                            ListItems.Type.TYPE_WATCHING,
                            new ListItems.WatchingItem(uid),
                            1
                            );
                }
            }catch(Exception e){
                Tracer.log(TAG, "addWatchings.exception: ", e);
            }
        }
    }

    private void showSpinner(boolean show){
        //le bouton et le spinner
        ProgressBar progressBar = findViewById(R.id.loading_spinner_butt);
        Button butt = findViewById(R.id.buttValidate);
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            butt.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            butt.setVisibility(View.VISIBLE);
        }
    }


}