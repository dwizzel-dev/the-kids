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

public class SignInUserActivity extends FacebookLoginActivity{

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

        setInstance();


    }



}
