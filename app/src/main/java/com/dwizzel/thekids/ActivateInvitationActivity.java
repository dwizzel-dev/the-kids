package com.dwizzel.thekids;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dwizzel.Const;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Tracer;

public class ActivateInvitationActivity extends BaseActivity {

    private static final String TAG = "ActivateInvitationActivity";
    private TrackerService.TrackerBinder mTrackerBinder;
    private boolean isActivityCreated = false;

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici
    }

    public void onSubCreate(){
        Tracer.log(TAG, "onSubCreate");
        //pas qu'il recommence au onStart
        if(!isActivityCreated) {
            setContentView(R.layout.activity_activate_invitation);
            setTitle(R.string.activate_invitation);
            //set un nouveau callback au lieu de celui de BaseActivity
            //vu qu'il va recevoir une notif quand aura ca liste de Watchers et de Invites
            setTrackerBinderCallback();
        }
        isActivityCreated = true;
    }

    private void setTrackerBinderCallback(){
        //on peut le caller sinon l'activity ne serait meme pas partis
        //on overwrite celui de BaseActivity
        ITrackerBinderCallback serviceCallback = new ITrackerBinderCallback() {
            private static final String TAG = "ActivateInvitationActivity.ITrackerBinder";
            public void handleResponse(ServiceResponseObject sro){
                Tracer.log(TAG, "handleResponse: " + sro);
                if(sro.getErr() == 0){
                    Tracer.log(TAG, "handleResponse: " + sro.getMsg());
                    switch(sro.getMsg()){
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


}
