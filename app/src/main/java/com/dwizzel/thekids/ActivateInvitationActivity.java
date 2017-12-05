package com.dwizzel.thekids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.objects.ServiceResponseObject;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Tracer;

public class ActivateInvitationActivity extends BaseActivity {

    private static final String TAG = "ActivateInvitationActivity";
    private TrackerService.TrackerBinder mTrackerBinder;
    private boolean isActivityCreated = false;
    private String mFromUid;

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
                mFromUid = bundle.getString("fromUid");
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
            public void handleResponse(ServiceResponseObject sro){
                Tracer.log(TAG, "handleResponse: " + sro);
                if(sro.getErr() == 0){
                    Tracer.log(TAG, "handleResponse: " + sro.getMsg());
                    switch(sro.getMsg()){
                        case Const.response.ON_WATCHING_PROFIL_MODIFIED:
                            //c'est beau
                            watchingProfilModified(true);
                            break;
                        default:
                            break;
                    }
                }else if(sro.getErr() == Const.error.ERROR_WATCHING_PROFIL_MODIF_FAILURE){
                    watchingProfilModified(false);
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

    private void watchingProfilModified(boolean sucess){
        if(sucess){
            //on revient au listing en clearant le backstack completment
            //start activity
            Intent intent = new Intent(ActivateInvitationActivity.this,
                    WatchOverSomeoneActivity.class);
            //start activity and clear the backStack
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            this.finish();
        }else{
            displayErrMsg(R.string.err_watching_profil_modif_failure);
        }
    }

    private void setButton() {
        Button butt = findViewById(R.id.buttActivate);
        //butt create
        butt.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //on call la function et on met le loader
                        checkMandatoryFieldsAndSave();
                        //TODO: a enlever
                        //watchingProfilModified(true);
                    }
                });

    }

    private void checkMandatoryFieldsAndSave(){
        displayErrMsg(Const.error.NO_ERROR);
        //on va chercher les infos et on les sets
        String nickname = String.format("%s",((EditText)findViewById(R.id.nickname)).getText());
        String phone = String.format("%s",((EditText)findViewById(R.id.phone)).getText());
        String email = String.format("%s",((EditText)findViewById(R.id.email)).getText());
        //minor check
        if(nickname.isEmpty()){
            displayErrMsg(R.string.err_nickname_invalid);
            return;
        }
        //le reste importe peu
        //on a le tout alors on fait le call au service
        showSpinner(true);
        mTrackerBinder.saveNewWatchingProfil(mFromUid, nickname, phone, email);

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
