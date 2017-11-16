package com.dwizzel.thekids;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dwizzel.utils.Tracer;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tracer.log(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //view
        setContentView(R.layout.activity_login);
        //buttons
        final Button buttCreate = (Button)findViewById(R.id.buttCreateWithEmail);
        final Button buttSignIn = (Button)findViewById(R.id.buttSignIn);
        //butt create
        buttCreate.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, CreateUserActivity.class);
                    //start activity
                    startActivity(intent);
                }
            });
        //butt sign in
        buttSignIn.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignInUserActivity.class);
                    //start activity
                    startActivity(intent);
                }
            });

    }

    @Override
    protected void onDestroy(){
        Tracer.log(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStart(){
        Tracer.log(TAG, "onStart");
        super.onStart();
    }




}
