package com.dwizzel.thekids;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.models.CommunicationObject;
import com.dwizzel.observers.BooleanObserver;
import com.dwizzel.services.ITrackerBinderCallback;
import com.dwizzel.services.TrackerService;
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
    private BooleanObserver mServiceBoundObservable = new BooleanObserver(false);
    public TrackerService.TrackerBinder mTrackerBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAG, "onServiceConnected");
            mTrackerBinder = (TrackerService.TrackerBinder)service;
            mTrackerBinder.registerCallback(mServiceCallback);
            mServiceBoundObservable.set(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected");
            mServiceBoundObservable.set(false);
            mTrackerBinder = null;
        }
    };

    public TrackerService.TrackerBinder getTrackerBinder(){
        return mTrackerBinder;
    }

    private void bindToAuthService(){
        if(!mServiceBoundObservable.get()) {
            Intent intent = TrackerService.getIntent(this);
            startService(intent);
            //bind to the service, si pas de startService se ferme auto apres la femeture de L'appli
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ITrackerBinderCallback mServiceCallback = new ITrackerBinderCallback() {

        private static final String TAG = "TheKids.ITrackerBinder";

        @Override
        public void handleResponse(long counter){
            //Log.d(TAG, String.format("thread counter: %d", counter));
        }
        @Override
        public void onSignedIn(Object obj){
            Log.d(TAG, "onSignedIn");
            //on enleve le loader
            Utils.getInstance().hideProgressDialog();
            //check les erreurs et exception
            int err = ((CommunicationObject.ServiceResponseObject)obj).getErr();
            switch(err){
                case Const.except.NO_CONNECTION:
                    Utils.getInstance().showToastMsg(SignInUserWithEmailActivity.this,
                            R.string.err_no_connectivity);
                    break;
                case Const.error.NO_ERROR:
                    userIsSignedInRoutine();
                    break;
                case Const.error.ERROR_INVALID_PASSWORD:
                    displayErrMsg(R.string.err_invalid_password);
                    break;
                case Const.error.ERROR_INVALID_CREDENTIALS:
                    displayErrMsg(R.string.err_invalid_credential);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void onSignedOut(Object obj){
            Log.d(TAG, "onSignedOut");
        }
    };

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.getInstance().hideProgressDialog();
    }

    @Override
    protected void onDestroy(){
        Log.w(TAG, "onDestroy");
        super.onDestroy();
        //reset
        mServiceCallback = null;
        mServiceBoundObservable.set(false);
        //clear le binder
        if(mTrackerBinder != null) {
            unbindService(mConnection);
            mConnection = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindToAuthService();
        setContentView(R.layout.activity_sign_in_user_with_email);
        setTitle(R.string.signin_with_email_title);
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
        int err = Utils.getInstance().isValidEmail(email);
        if(err != 0){
            return err;
        }
        //le setter du email
        setEmail(email);
        //on check le psw
        err = Utils.getInstance().isValidPsw(psw);
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

    private void userIsSignedInRoutine(){
        //on affiche qu'il est logue
        Utils.getInstance().showToastMsg(SignInUserWithEmailActivity.this,
                getResources().getString(R.string.toast_connected_as,
                        mTrackerBinder.getUserLoginName()));
        //on va a activity principal
        Intent intent = new Intent(SignInUserWithEmailActivity.this,
                HomeActivity.class);
        //start activity and clear the backStack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void signInUser() {
        //on va faire un listener sur le resultat
        if (mTrackerBinder != null) {
            //on met un loader
            Utils.getInstance().showProgressDialog(this);
            //on call le service
            mTrackerBinder.signIn(email, psw);
        }
    }

}