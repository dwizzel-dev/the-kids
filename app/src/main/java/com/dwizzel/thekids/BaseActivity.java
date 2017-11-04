package com.dwizzel.thekids;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.dwizzel.utils.Auth;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Dwizzel on 30/10/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private Auth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = Auth.getInstance();
        //start la activity principale de la class qui extends
        startMainApp();
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(!mAuth.isSignedIn()) {
            //le login page
            Intent intent = new Intent(this, LoginActivity.class);
            //start activity de login car pas encore logue
            startActivity(intent);
        }

    }

    protected void startMainApp() {
        //will be overrided by other activities
    }

}
