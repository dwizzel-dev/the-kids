package com.dwizzel.thekids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dwizzel.utils.Tracer;

public class SignInUserActivity extends AFacebookLoginActivity{

    private final static String TAG = "SignInUserActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Tracer.log(TAG, "onCreate");
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
        //
        setFacebookLogin();
    }

}
