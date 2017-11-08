package com.dwizzel.thekids;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dwizzel.Const;
import com.dwizzel.utils.Auth;
import com.dwizzel.utils.Utils;

import java.util.Observable;
import java.util.Observer;

/***
 * https://dzone.com/articles/managing-multiple-ui-layouts
 * https://stackoverflow.com/questions/4817900/android-fragments-and-animation
 * https://developers.facebook.com/apps/135994336994028/fb-login/quickstart/
 * https://developers.facebook.com/docs/facebook-login/android
 * */


public class CreateUserActivity extends AppCompatActivity {

    private final static String TAG = "TheKids.CreateUserActiv";
    private Auth mAuth;
    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        setTitle(R.string.create_user_title);

        //butt create
        final Button buttCreate = findViewById(R.id.buttCreateWithEmail);
        buttCreate.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(CreateUserActivity.this, CreateUserWithEmailActivity.class);
                        //start activity
                        startActivity(intent);
                    }
                });

        mUtils = Utils.getInstance();
        mAuth = Auth.getInstance();
        signInFacebookUser();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //to receive the result from the facebook callback
        mAuth.facebookCallBackManager(requestCode, resultCode, data);
    }


    private void userFacebookSignInFinished(){
        //on affiche qu'il est logue
        Auth auth = Auth.getInstance();
        Utils utils = Utils.getInstance();
        String loginName = auth.getUserLoginName();
        utils.showToastMsg(CreateUserActivity.this,
                getResources().getString(R.string.toast_connected_as, loginName));
        //on va a activity principal
        Intent intent = new Intent(CreateUserActivity.this, HomeActivity.class);
        //start activity
        startActivity(intent);
    }

    private void signInFacebookUser() {

        //on va faire un listener sur le resultat
        try {
            //
            mAuth.initFacebookLogin(CreateUserActivity.this);
            mAuth.deleteObservers();
            mAuth.addObserver(new Observer() {
                public void update(Observable obj, Object arg) {
                    Log.w(TAG, "mAuth.update: " + arg);
                    switch ((int)arg){
                        case Const.notif.TYPE_NOTIF_LOADING:
                            mUtils.showProgressDialog(CreateUserActivity.this);
                            break;
                        case Const.notif.TYPE_NOTIF_SIGNED:
                            mUtils.hideProgressDialog();
                            userFacebookSignInFinished();
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
