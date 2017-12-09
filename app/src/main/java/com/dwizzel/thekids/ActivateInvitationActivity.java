package com.dwizzel.thekids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.datamodels.InviteInfoModel;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Tracer;

public class ActivateInvitationActivity extends BaseActivity {

    private static final String TAG = "ActivateInvitationActivity";
    private TrackerService.TrackerBinder mTrackerBinder;
    private boolean isActivityCreated = false;
    private InviteInfoModel mInviteInfoModel;

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
            setButton();
            //on cherche les params passes
            Bundle bundle = getIntent().getExtras();
            if(bundle != null) {
                mInviteInfoModel = (InviteInfoModel) bundle.getParcelable("inviteInfo");
            }
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
            public void handleResponse(ServiceResponseObject sro) {
                if (sro.getErr() == 0) {
                    //c'est beau
                    switch (sro.getMsg()) {
                        case Const.response.ON_INVITE_ID_ACTIVATED:
                            inviteActivated(true);
                            break;
                        default:
                            break;
                    }
                } else {
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
                        case Const.error.ERROR_INVITE_ID_FAILURE:
                        case Const.error.ERROR_INVITE_INFOS_FAILURE:
                            inviteActivated(false);
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

    private void inviteActivated(boolean success){
        if(success){
            //on revient au listing en clearant le backstack completment
            Intent intent = new Intent(ActivateInvitationActivity.this,
                    WatchOverSomeoneActivity.class);
            //start activity and clear the backStack
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            this.finish();
        }else{
            displayErrMsg(R.string.err_invalid_invite_infos_failure);
        }
    }

    private void setButton() {
        Button butt = findViewById(R.id.buttActivate);
        //butt create
        butt.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //on call la function et on met le loader
                        checkMandatoryFieldsAndActivate();

                    }
                });

    }

    private void checkMandatoryFieldsAndActivate(){
        displayErrMsg(Const.error.NO_ERROR);
        //on va chercher les infos et on les sets
        String name = String.format("%s",((EditText)findViewById(R.id.name)).getText());
        String phone = String.format("%s",((EditText)findViewById(R.id.phone)).getText());
        String email = String.format("%s",((EditText)findViewById(R.id.email)).getText());
        //minor check
        if(name.isEmpty()){
            displayErrMsg(R.string.err_nickname_invalid);
            return;
        }
        //on set l'objet
        if(mInviteInfoModel != null) {
            mInviteInfoModel.setName(name);
            mInviteInfoModel.setPhone(phone);
            mInviteInfoModel.setEmail(email);
            //on a le tout alors on fait le call au service
            showSpinner(true);
            mTrackerBinder.saveInviteInfo(mInviteInfoModel);
        }

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

    private void showSpinner(boolean show){
        //le bouton et le spinner
        ProgressBar progressBar = findViewById(R.id.loading_spinner);
        Button butt = findViewById(R.id.buttActivate);
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            butt.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            butt.setVisibility(View.VISIBLE);
        }
    }


}
