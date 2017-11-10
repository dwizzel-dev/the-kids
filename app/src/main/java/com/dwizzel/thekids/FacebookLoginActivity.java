package com.dwizzel.thekids;

/***
 * https://dzone.com/articles/managing-multiple-ui-layouts
 * https://stackoverflow.com/questions/4817900/android-fragments-and-animation
 * https://developers.facebook.com/apps/135994336994028/fb-login/quickstart/
 * https://developers.facebook.com/docs/facebook-login/android
 * */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dwizzel.Const;
import com.dwizzel.utils.Auth;
import com.dwizzel.utils.Utils;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dwizzel on 09/11/2017.
 */

public class FacebookLoginActivity extends AppCompatActivity {

    private final static String TAG = "TheKids.FacebookLoginAc";
    private Auth mAuth;
    private Utils mUtils;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //to receive the result from the facebook callback
        mAuth.facebookCallBackManager(requestCode, resultCode, data);
    }

    public void setInstance(){
        mUtils = Utils.getInstance();
        mAuth = Auth.getInstance();
        signInFacebookUser();
    }


    private void userWaitForFirebase(){
        //on enleve les boutons de sign in
        mAuth.disableFacebookButton();
        mUtils.showProgressDialog(FacebookLoginActivity.this);

    }


    private void userSignInFinished(){
        mUtils.hideProgressDialog();
        //on affiche qu'il est logue
        Auth auth = Auth.getInstance();
        Utils utils = Utils.getInstance();
        String loginName = auth.getUserLoginName();
        utils.showToastMsg(FacebookLoginActivity.this,
                getResources().getString(R.string.toast_connected_as, loginName));
        //on va a activity principal
        Intent intent = new Intent(FacebookLoginActivity.this, HomeActivity.class);
        //start activity and clear the backStack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



    private void signInFacebookUser() {

        //on va faire un listener sur le resultat
        try {
            //
            mAuth.initFacebookLogin(FacebookLoginActivity.this);
            mAuth.deleteObservers();
            mAuth.addObserver(new Observer() {
                public void update(Observable obj, Object arg) {
                    Log.w(TAG, "mAuth.update: " + arg);
                    switch ((int)arg){
                        case Const.notif.TYPE_NOTIF_LOADING:
                            userWaitForFirebase();
                            break;
                        case Const.notif.TYPE_NOTIF_SIGNED:
                            userSignInFinished();
                            break;
                        default:
                            break;
                    }

                }
            });

        }catch (Exception e) {
            Log.w(TAG, e.getMessage());
            //un prob de pas de connection

        }

    }




}
