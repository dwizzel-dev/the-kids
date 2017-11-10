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

    private Auth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = Auth.getInstance();
        //start la activity principale de la class qui extends
        if(!mAuth.isSignedIn()) {
            //le login page
            Intent intent = new Intent(this, LoginActivity.class);
            //start activity de login car pas encore logue
            startActivity(intent);
        }else {
            startMainActivity();
        }
    }

    protected abstract void startMainActivity();

    protected void signOutUser(){
        if(mAuth != null){
            mAuth.signOut();
        }
        //
        Utils utils = Utils.getInstance();
        utils.showToastMsg(BaseActivity.this, R.string.toast_signed_out);
        //on reload l'activity dans laquelle il est,
        //qui va checker si est logue ou pas
        recreate();
    }

}
