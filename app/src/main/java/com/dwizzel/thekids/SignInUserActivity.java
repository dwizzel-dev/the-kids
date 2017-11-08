package com.dwizzel.thekids;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dwizzel.utils.Auth;
import com.dwizzel.Const;
import com.dwizzel.utils.Utils;

import java.util.Observable;
import java.util.Observer;

public class SignInUserActivity extends AppCompatActivity {

    private final static String TAG = "TheKids.SignInUserActiv";
    private Auth mAuth;
    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_user);
        setTitle(R.string.signin_title);

        //butt create
        final Button buttCreate = findViewById(R.id.buttSignInWithEmail);
        buttCreate.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(SignInUserActivity.this,
                                SignInUserWithEmailActivity.class);
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
        utils.showToastMsg(SignInUserActivity.this,
                getResources().getString(R.string.toast_connected_as, loginName));
        //on va a activity principal
        Intent intent = new Intent(SignInUserActivity.this, HomeActivity.class);
        //start activity
        startActivity(intent);
    }



    private void signInFacebookUser() {

        //on va faire un listener sur le resultat
        try {
            //
            mAuth.initFacebookLogin(SignInUserActivity.this);
            mAuth.deleteObservers();
            mAuth.addObserver(new Observer() {
                public void update(Observable obj, Object arg) {
                    Log.w(TAG, "mAuth.update: " + arg);
                    switch ((int)arg){
                        case Const.notif.TYPE_NOTIF_LOADING:
                            mUtils.showProgressDialog(SignInUserActivity.this);
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
