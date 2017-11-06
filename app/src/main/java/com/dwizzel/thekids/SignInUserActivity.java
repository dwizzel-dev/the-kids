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

public class SignInUserActivity extends AppCompatActivity {

    private final static String TAG = "THEKIDS::";
    private CallbackManager facebookCallbackManager;

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

        //facebook
        LoginButton loginButton = findViewById(R.id.facebook_button);
        //pour avoir au moins le nom
        loginButton.setReadPermissions("public_profile");

        //facebookCallBack
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {

                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult){
                        Log.w(TAG, "facebook.onSuccess");
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
                        //userFacebookSignInFinished();
                    }
                    @Override
                    public void onCancel() {
                        Log.w(TAG, "facebook.onCancel");
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Log.w(TAG, "facebook.onError");
                    }

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
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

}
