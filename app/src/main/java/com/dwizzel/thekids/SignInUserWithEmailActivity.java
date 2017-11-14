package com.dwizzel.thekids;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dwizzel.utils.Auth;
import com.dwizzel.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


public class SignInUserWithEmailActivity extends AppCompatActivity {

    private static final String TAG = "TheKids.SignInUserWithEmailActivity";
    private String email;
    private String psw;
    private Utils mUtils;
    private Auth mAuth;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    @Override
    public void onStop() {
        super.onStop();
        mUtils.hideProgressDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_user_with_email);
        setTitle(R.string.signin_with_email_title);

        //firebase
        mAuth = Auth.getInstance();
        //utilitaires de base pour messages et autres
        mUtils = Utils.getInstance();

        //butt create
        final Button buttSignIn = findViewById(R.id.buttSignIn);
        buttSignIn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //get the email entered
                        String email = String.format("%s",((EditText)findViewById(R.id.userEmail)).getText());
                        String[] psw = new String[1];
                        psw[0] = String.format("%s",((EditText)findViewById(R.id.userPsw)).getText());
                        //problem set an error message
                        displayErrMsg(setEmailAndPsw(email, psw));

                    }
                });
    }

    private int setEmailAndPsw(String email, String[] psw){
        //check le email
        int err = mUtils.isValidEmail(email);
        if(err != 0){
            return err;
        }
        //le setter du email
        setEmail(email);
        //on check le psw
        err = mUtils.isValidPsw(psw);
        if(err != 0){
            return err;
        }
        //le setter du password
        setPsw(psw[0]);
        //pas d'erreur alors on va faire le sign in avec firebase
        signInUser();
        return 0;
    }

    private void displayErrMsg(int msgId){
        TextView txtView = findViewById(R.id.errMsg);
        if(msgId != 0) {
            txtView.setText(msgId);
        }else {
            txtView.setText("");
        }
    }

    private void userSignInFinished(){
        //on affiche qu'il est logue
        String loginName = mAuth.getUserLoginName();
        mUtils.showToastMsg(SignInUserWithEmailActivity.this,
                getResources().getString(R.string.toast_connected_as, loginName));
        //on va a activity principal
        Intent intent = new Intent(SignInUserWithEmailActivity.this, HomeActivity.class);
        //start activity and clear the backStack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void signInUser() {


        //on va faire un listener sur le resultat
        try {
            displayErrMsg(0);
            //
            mAuth.signInUser(SignInUserWithEmailActivity.this, email, psw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            Log.w(TAG, "SIGNIN::onComplete[001]");
                            //on hide le loader
                            mUtils.hideProgressDialog();
                            //handling errors
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                }catch(FirebaseAuthInvalidCredentialsException invalidPsw) {
                                    displayErrMsg(R.string.err_invalid_password);
                                }catch(FirebaseAuthInvalidUserException invalidCredential) {
                                    displayErrMsg(R.string.err_invalid_credential);
                                }catch (Exception e){
                                    Log.w(TAG, "SIGNIN::Exception");
                                }
                            } else {
                                //pas erreur alors on continue
                                userSignInFinished();
                            }

                        }
                    });
            //pas exception de conn alors on show le loader
            mUtils.showProgressDialog(SignInUserWithEmailActivity.this);

        }catch (Exception e) {
            Log.w(TAG, e.getMessage());
            //un prob de pas de connection
            mUtils.showToastMsg(SignInUserWithEmailActivity.this, R.string.err_no_connectivity);
        }

    }



}
