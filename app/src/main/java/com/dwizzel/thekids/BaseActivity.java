package com.dwizzel.thekids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.dwizzel.utils.Auth;
import com.dwizzel.utils.Utils;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Dwizzel on 30/10/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static Auth sAuth;
    private boolean bLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfLoguedIn();
    }

    private void checkIfLoguedIn(){
        if (sAuth == null) {
            sAuth = Auth.getInstance();
        }
        if(!sAuth.isSignedIn()) {
            //le login page
            Intent intent = new Intent(this, LoginActivity.class);
            //start activity de login car pas encore logue
            //start activity and clear the backStack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            //sinon repart toujours l'acitivity
            if(!bLaunched) {
                bLaunched = true;
                startMainActivity();
            }
        }
    }

    protected abstract void startMainActivity();

    protected void signOutUser(){
        if(sAuth != null){
            sAuth.signOut();
        }
        //
        Utils utils = Utils.getInstance();
        utils.showToastMsg(BaseActivity.this, R.string.toast_signed_out);
        //on reload l'activity dans laquelle il est,
        //qui va checker si est logue ou pas
        recreate();
    }

}
