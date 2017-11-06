package com.dwizzel.thekids;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dwizzel.utils.Auth;
import com.dwizzel.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/***
 * https://dzone.com/articles/managing-multiple-ui-layouts
 * https://stackoverflow.com/questions/4817900/android-fragments-and-animation
 * https://developers.facebook.com/apps/135994336994028/fb-login/quickstart/
 * https://developers.facebook.com/docs/facebook-login/android
 * */


public class CreateUserActivity extends AppCompatActivity {

    private final static String TAG = "THEKIDS::";
    CallbackManager callbackManager;

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

        //facebook
        LoginButton loginButton = findViewById(R.id.facebook_button);
        //pour avoir au moins le nom
        loginButton.setReadPermissions("public_profile");

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.w(TAG, "onSuccess");
                        Profile profile = Profile.getCurrentProfile();
                        if(profile == null){
                            Log.v(TAG, "facebook.ProfileTracker()");
                            mProfileTracker = new ProfileTracker(){
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile,
                                                                       Profile currentProfile) {
                                    Log.v(TAG, "facebook.onCurrentProfileChanged()");
                                    mProfileTracker.stopTracking();
                                    Profile.setCurrentProfile(currentProfile);
                                    //on load seuleement une fois l'info recu
                                    userFacebookSignInFinished();
                                }
                            };
                        }else{
                            Log.v(TAG, String.format("facebook.getFirstName(): %s", profile.getFirstName()));
                        }

                    }
                    @Override
                    public void onCancel() {
                        Log.w(TAG, "onCancel");
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Log.w(TAG, "onError");
                    }

                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

}
