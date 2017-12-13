package com.dwizzel.thekids;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.datamodels.WatchingModel;
import com.dwizzel.objects.UserObject;
import com.dwizzel.services.TrackerService;
import com.dwizzel.utils.Tracer;

public class ModifyWatchingActivity extends BaseActivity {

    private static final String TAG = "ModifyWatchingActivity";
    private boolean isActivityCreated = false;
    private UserObject mUser;
    private String mWatchingId;
    TrackerService.TrackerBinder mTrackerBinder;

    public void onSubDestroy(){
        Tracer.log(TAG, "onSubDestroy");
        //si on a des chose a cleaner ici

    }

    public void onSubCreate(){
        Tracer.log(TAG, "onSubCreate");
        //pas qu'il recommence au onStart
        if(!isActivityCreated) {
            setContentView(R.layout.activity_modify_watcher);
            setTitle(R.string.modify_watcher_title);
            setButton();
            //get le binder
            mTrackerBinder = getTrackerBinder();
            mUser = UserObject.getInstance();
            //on cherche les params passes
            Bundle args = getIntent().getExtras();
            if(args != null) {
                mWatchingId = args.getString("uid");
                //set la form avec les infos de watcherModel
                setFormInput();
            }
        }
        isActivityCreated = true;
    }

    public void displayErrMsg(int msgId){
        TextView txtView = findViewById(R.id.errMsg);
        if(msgId != 0) {
            txtView.setText(msgId);
        }else {
            txtView.setText("");
        }
    }

    //------------------------------------------------------

    private void setButton() {
        Button butt = findViewById(R.id.buttSave);
        //butt create
        butt.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        checkMandatoryFieldsAndSave();

                    }
                });
    }

    private void setFormInput(){
        WatchingModel watchingModel = mUser.getWatching(mWatchingId);
        if(watchingModel != null){
            ((EditText)findViewById(R.id.name)).setText(watchingModel.getName());
            ((EditText)findViewById(R.id.phone)).setText(watchingModel.getPhone());
            ((EditText)findViewById(R.id.email)).setText(watchingModel.getEmail());
        }
    }

    private void checkMandatoryFieldsAndSave(){
        displayErrMsg(Const.error.NO_ERROR);
        //on va chercher les infos et on les sets
        String name = String.valueOf(((EditText)findViewById(R.id.name)).getText());
        String phone = String.valueOf(((EditText)findViewById(R.id.phone)).getText());
        String email = String.valueOf(((EditText)findViewById(R.id.email)).getText());
        //minor check
        if(name.isEmpty()){
            displayErrMsg(R.string.err_nickname_invalid);
            return;
        }
        //on set l'objet
        WatchingModel watchingModel = mUser.getWatching(mWatchingId);
        if(watchingModel != null) {
            //on set les infos
            watchingModel.setName(name);
            watchingModel.setPhone(phone);
            watchingModel.setEmail(email);
            //on les mets dans le mUser localement
            mUser.updateWatching(mWatchingId, watchingModel);
            //on call la DB qui fera le reste connecte ou pas
            mTrackerBinder.modifyWatchingsItem(mWatchingId);
        }
        //on termine et revient a notre liste de watchers
        this.finish();

    }

}
