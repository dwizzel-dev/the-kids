package com.dwizzel.thekids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dwizzel.utils.Tracer;

public class CreateUserActivity extends FacebookLoginActivity {

    private final static String TAG = "CreateUserActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Tracer.log(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        setTitle(R.string.create_user_title);

        //butt create
        final Button buttCreate = findViewById(R.id.buttCreateWithEmail);
        buttCreate.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(CreateUserActivity.this,
                                CreateUserWithEmailActivity.class);
                        //start activity
                        startActivity(intent);
                    }
                });
        //
        setFacebookLogin();
    }

}
